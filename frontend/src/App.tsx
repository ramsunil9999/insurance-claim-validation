import { useEffect, useState } from 'react';
import type { ChangeEvent, DragEvent, FormEvent } from 'react';

type RequestType = 'CLAIM' | 'PRIOR_AUTH';

type RecommendationResult = {
    recommendation: string;
    reason: string;
    confidence: number;
    observations: string[];
    riskScore?: number;
    riskLevel?: string;
    medicalNecessityScore?: number;
    coverageStatus?: string;
    waitingPeriodSatisfied?: boolean;
    duplicateDetected?: boolean;
    priorAuthorizationMatched?: boolean;
    riskFactors?: string[];
};

type ValidationResult = {
    valid: boolean;
    errors: string[];
    riskScore?: number;
    riskLevel?: string;
    medicalNecessityScore?: number;
    coverageStatus?: string;
    waitingPeriodSatisfied?: boolean;
    duplicateDetected?: boolean;
    procedureDiagnosisValid?: boolean;
    doctorSpecialtyValid?: boolean;
    hospitalCapabilityValid?: boolean;
    priorAuthorizationMatched?: boolean;
    missingDocuments?: string[];
    riskFactors?: string[];
};

type ClaimDetails = {
    requestType?: RequestType;
    priorAuthorizationId?: string;
    patientName?: string;
    diagnosis?: string;
    procedurePerformed?: string;
    requestedProcedure?: string;
    procedureCategory?: string;
    hospitalName?: string;
    hospitalType?: string;
    doctorName?: string;
    doctorSpeciality?: string;
    insurancePlan?: string;
    estimatedCost?: number;
    claimAmount?: number;
    requestedProcedureDate?: string;
};

type ClaimResponse = {
    claimId?: string;
    fileName: string;
    contentType: string;
    size: number;
    message: string;
    claimDetails?: ClaimDetails;
    validationResult?: ValidationResult;
    recommendationResult?: RecommendationResult;
};

const acceptedFileTypes = ['application/pdf', 'image/jpeg', 'image/png', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
const acceptedFileExtensions = ['.pdf', '.jpg', '.jpeg', '.png', '.docx'];

const isSupportedFile = (selectedFile: File) => {
    const extension = selectedFile.name.toLowerCase().slice(selectedFile.name.lastIndexOf('.'));
    return acceptedFileTypes.includes(selectedFile.type) || acceptedFileExtensions.includes(extension);
};

const displayValue = (value: unknown, fallback = 'Not identified') => value === null || value === undefined || value === '' ? fallback : String(value);

const failedChecks = (validation?: ValidationResult) => {
    if (!validation) return [];
    const checks: string[] = [];
    if (validation.procedureDiagnosisValid === false) checks.push('Procedure-Diagnosis Mismatch');
    if (validation.doctorSpecialtyValid === false) checks.push('Doctor Specialty Mismatch');
    if (validation.hospitalCapabilityValid === false) checks.push('Hospital Capability Concern');
    if (validation.coverageStatus === 'NOT_COVERED') checks.push('Coverage Issue');
    if (validation.waitingPeriodSatisfied === false) checks.push('Waiting Period Not Satisfied');
    if (validation.errors.some((error) => /financial|claim amount|charge/i.test(error))) checks.push('Financial Anomaly');
    return [...new Set(checks)];
};

const riskIndicators = (validation?: ValidationResult, recommendation?: RecommendationResult) => {
    const indicators = [...(validation?.riskFactors || []), ...(recommendation?.riskFactors || [])];
    return [...new Set(indicators)].filter(Boolean);
};

const summaryPoints = (reason?: string, fallback?: string) => {
    const text = reason || fallback || 'No decision summary available.';
    return text.split(/(?<=[.!?])\s+/).filter(Boolean);
};

export default function App() {
    const [file, setFile] = useState<File | null>(null);
    const [requestType, setRequestType] = useState<RequestType>('CLAIM');
    const [result, setResult] = useState<ClaimResponse | null>(null);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [dragActive, setDragActive] = useState(false);

    const acceptFile = (selectedFile: File | undefined) => {
        if (!selectedFile) return;
        if (!isSupportedFile(selectedFile)) {
            setFile(null);
            setError('Please select a PDF, JPG, JPEG, PNG, or DOCX file.');
            return;
        }
        setFile(selectedFile);
        setError('');
    };

    const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => acceptFile(event.target.files?.[0]);

    const handleDrop = (event: DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        setDragActive(false);
        acceptFile(event.dataTransfer.files?.[0]);
    };

    const handleReset = () => {
        setFile(null);
        setResult(null);
        setError('');
        setDragActive(false);
    };

    useEffect(() => {
        if (result) window.scrollTo({ top: 0, behavior: 'smooth' });
    }, [result]);

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!file) {
            setError(`Please select a ${requestType === 'PRIOR_AUTH' ? 'prior authorization' : 'claim'} document first.`);
            return;
        }
        setLoading(true);
        setError('');
        const formData = new FormData();
        formData.append('file', file);
        formData.append('requestType', requestType);

        try {
            const response = await fetch('http://localhost:8080/api/v1/claims/upload', { method: 'POST', body: formData, credentials: 'include' });
            const data = await response.json();
            if (!response.ok) throw new Error(data?.message || 'Unable to process the document.');
            if (data?.message?.startsWith('Document type mismatch')) {
                setError(data.message);
                setResult(null);
                return;
            }
            setResult(data as ClaimResponse);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unexpected error occurred.');
        } finally {
            setLoading(false);
        }
    };

    const validation = result?.validationResult;
    const recommendation = result?.recommendationResult;
    const details = result?.claimDetails;
    const isPriorAuth = details?.requestType === 'PRIOR_AUTH' || requestType === 'PRIOR_AUTH';
    const recommendationName = recommendation?.recommendation || 'PENDING';
    const isApproved = recommendationName === 'APPROVED';
    const needsReview = recommendationName === 'MANUAL_REVIEW' || recommendationName === 'REJECTED';
    const hasValidationErrors = Boolean(validation?.errors?.length);
    const checksFailed = failedChecks(validation);
    const indicators = riskIndicators(validation, recommendation);
    const procedure = isPriorAuth ? details?.requestedProcedure : details?.procedurePerformed;
    const amount = isPriorAuth ? details?.estimatedCost : details?.claimAmount;
    const hasAuthorizationDetails = Boolean(details?.requestedProcedure || details?.estimatedCost !== undefined || details?.requestedProcedureDate);

    return (
        <div className={`page ${result ? 'results-page' : 'home-page'}`}>
            {!result && <main className="hero-panel">
                <div className="hero-copy">
                    <span className="eyebrow">AI INSURANCE REVIEW WORKSPACE</span>
                    <h1>One intelligent platform for every healthcare decision.</h1>
                    <p>Automate prior authorization reviews, validate claims, detect risk signals, and generate AI-powered recommendations with complete decision transparency.</p>
                    <div className="stats-grid">
                        <div><strong>Before treatment</strong><span>Prior authorization review</span></div>
                        <div><strong>After treatment</strong><span>Claim validation</span></div>
                        <div><strong>One evidence trail</strong><span>Fraud Risk & Insights</span></div>
                    </div>
                </div>
                <form className="upload-card" onSubmit={handleSubmit}>
                    <div className="request-switch" aria-label="Document type">
                        <button type="button" className={requestType === 'PRIOR_AUTH' ? 'selected' : ''} onClick={() => setRequestType('PRIOR_AUTH')}>Prior authorization</button>
                        <button type="button" className={requestType === 'CLAIM' ? 'selected' : ''} onClick={() => setRequestType('CLAIM')}>Claim validation</button>
                    </div>
                    <div className={`drop-zone ${dragActive ? 'active' : ''}`} onDrop={handleDrop} onDragOver={(event) => { event.preventDefault(); setDragActive(true); }} onDragLeave={() => setDragActive(false)}>
                        <div className="drop-icon">+</div>
                        <h2>{requestType === 'PRIOR_AUTH' ? 'Add authorization documents' : 'Add claim documents'}</h2>
                        <p>Drop a PDF, image, or DOCX here to begin.</p>
                        <label className="file-label"><input type="file" accept=".pdf,.jpg,.jpeg,.png,.docx,application/pdf,image/jpeg,image/png,application/vnd.openxmlformats-officedocument.wordprocessingml.document" onChange={handleFileChange} />Choose file</label>
                        {file && <p className="file-meta">{file.name}</p>}
                    </div>
                    <button type="submit" className="primary-btn" disabled={loading}>{loading ? 'Reading document...' : 'Run assessment'}</button>
                    {error && <div className="alert error">{error}</div>}
                </form>
            </main>}

            {result && <main className="result-panel">
                <div className="result-header"><div><p className="badge">Assessment complete</p><h2>{isPriorAuth ? 'Prior authorization review' : 'Claim validation review'}</h2></div><button type="button" className="secondary-btn" onClick={handleReset}>New assessment</button></div>
                <section className="score-strip">
                    <div><span>Recommendation</span><strong className={`decision ${recommendationName.toLowerCase()}`}>{recommendationName}</strong></div>
                    <div><span>Risk score</span><strong>{validation?.riskScore ?? recommendation?.riskScore ?? '—'}<small>/100</small></strong><em className={`risk-${(validation?.riskLevel || recommendation?.riskLevel || 'unassessed').toLowerCase()}`}>{validation?.riskLevel || recommendation?.riskLevel || 'UNASSESSED'}</em></div>
                    <div><span>Coverage</span><strong>{validation?.coverageStatus || recommendation?.coverageStatus || 'Not assessed'}</strong></div>
                </section>
                <div className="cards-grid result-core">
                    <article className="detail-card accent-card decision-card"><h3>Decision summary</h3><ul className="decision-points">{summaryPoints(recommendation?.reason, result.message).map((point) => <li key={point}>{point}</li>)}</ul><p><strong>Confidence:</strong> {recommendation?.confidence !== undefined ? `${Math.round(recommendation.confidence * 100)}%` : 'Not available'}</p></article>
                    <article className="detail-card evidence-card"><h3>{isPriorAuth ? 'Evidence profile' : 'Claim essentials'}</h3><p><strong>Patient:</strong> {displayValue(details?.patientName)}</p><p><strong>Diagnosis:</strong> {displayValue(details?.diagnosis)}</p><p><strong>{isPriorAuth ? 'Requested procedure' : 'Treatment / Procedure'}:</strong> {displayValue(procedure)}</p>{!isPriorAuth && <p><strong>Claim amount:</strong> {amount !== undefined ? `$${amount.toLocaleString()}` : 'Not identified'}</p>}<p><strong>Doctor specialty:</strong> {displayValue(details?.doctorSpeciality)}</p><p><strong>Hospital:</strong> {displayValue(details?.hospitalName)} / {displayValue(details?.hospitalType)}</p></article>
                    {needsReview && <article className="detail-card exception-card"><h3>{recommendationName === 'REJECTED' ? 'Rejection reasons' : 'Review reasons'}</h3>{checksFailed.length ? <ul>{checksFailed.map((check) => <li key={check}>⚠ {check}</li>)}</ul> : <p>Review the decision summary and risk indicators.</p>}</article>}
                </div>
                {isApproved && <div className="passed-checks">All validation checks passed ✅</div>}
                {isPriorAuth && hasAuthorizationDetails && <article className="detail-card authorization-card"><h3>Authorization details</h3>{details?.requestedProcedure && <p><strong>Requested procedure:</strong> {details.requestedProcedure}</p>}{details?.estimatedCost !== undefined && <p><strong>Estimated cost:</strong> ${details.estimatedCost.toLocaleString()}</p>}{details?.requestedProcedureDate && <p><strong>Procedure date:</strong> {details.requestedProcedureDate}</p>}</article>}
                {validation?.duplicateDetected === true && <article className="detail-card exception-card duplicate-alert"><p>⚠ Possible Duplicate Request Detected</p></article>}
                {needsReview && indicators.length > 0 && <article className="detail-card risk-card"><h3>Risk signals</h3><ul>{indicators.map((indicator) => <li key={indicator}>⚠ {indicator}</li>)}</ul></article>}
                {validation?.missingDocuments?.length ? <article className="detail-card exception-card"><h3>More information needed</h3><ul>{validation.missingDocuments.map((document) => <li key={document}>⚠ {document}</li>)}</ul></article> : null}
                {!isApproved && hasValidationErrors ? <div className="alert warning validation-errors"><strong>{recommendation ? 'Review required' : 'Missing information'}</strong><ul>{validation?.errors.map((validationError) => <li key={validationError}>{validationError}</li>)}</ul></div> : null}
            </main>}
        </div>
    );
}
