import { useEffect, useState } from 'react';
import type { ChangeEvent, FormEvent } from 'react';

type RecommendationResult = {
  recommendation: string;
  reason: string;
  confidence: number;
  observations: string[];
};

type ValidationResult = {
  valid: boolean;
  errors: string[];
};

type DocumentResponse = {
  claimId: string;
  documentType?: string;
  fileName: string;
  contentType: string;
  size: number;
  message: string;
  extractedText?: string;
  claimDetails?: Record<string, unknown>;
  priorAuthorizationDetails?: Record<string, unknown>;
  validationResult?: ValidationResult;
  recommendationResult?: RecommendationResult;
};

const acceptedFileTypes = [
  'application/pdf',
  'image/jpeg',
  'image/png',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
];

const acceptedFileExtensions = ['.pdf', '.jpg', '.jpeg', '.png', '.docx'];

const isSupportedFile = (selectedFile: File) => {
  const extension = selectedFile.name.toLowerCase().slice(selectedFile.name.lastIndexOf('.'));
  return acceptedFileTypes.includes(selectedFile.type) || acceptedFileExtensions.includes(extension);
};

export default function App() {
  const [file, setFile] = useState<File | null>(null);
  const [result, setResult] = useState<DocumentResponse | null>(null);
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [dragActive, setDragActive] = useState(false);

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    const selectedFile = event.target.files?.[0];

    if (!selectedFile) {
      setFile(null);
      return;
    }

    if (!isSupportedFile(selectedFile)) {
      setFile(null);
      setError('Please select a PDF, JPG, JPEG, PNG, or DOCX file.');
      return;
    }

    setFile(selectedFile);
    setError('');
  };

  const handleDrop = (event: React.DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    setDragActive(false);
    const droppedFile = event.dataTransfer.files?.[0];
    if (!droppedFile) {
      return;
    }

    if (!isSupportedFile(droppedFile)) {
      setFile(null);
      setError('Please select a PDF, JPG, JPEG, PNG, or DOCX file.');
      return;
    }

    setFile(droppedFile);
    setError('');
  };

  const handleDragOver = (event: React.DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    setDragActive(true);
  };

  const handleDragLeave = () => {
    setDragActive(false);
  };

  const handleReset = () => {
    setFile(null);
    setResult(null);
    setError('');
    setDragActive(false);
  };

  useEffect(() => {
    if (result) {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }, [result]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!file) {
      setError('Please select a supported insurance document first.');
      return;
    }

    setLoading(true);
    setError('');

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await fetch('http://localhost:8080/api/v1/claims/upload', {
        method: 'POST',
        body: formData,
        credentials: 'include',
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data?.message || 'Unable to process the insurance document.');
      }

      setResult(data as DocumentResponse);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unexpected error occurred.');
    } finally {
      setLoading(false);
    }
  };

  const isClaim = result?.documentType === 'CLAIM';

  const isPriorAuthorization = result?.documentType === 'PRIOR AUTHORIZATION';

  const recommendation = result?.recommendationResult;

  return (
    <div className="page">
      {!result && (
        <div className="hero-panel">
          <div className="hero-copy">
            <span className="eyebrow">Claims & Prior Authorizations Powered by AI</span>
            <h1>One intelligent platform for every healthcare decision.</h1>
            <p>
              Upload a Claim or Prior Authorization document and receive AI-powered extraction, validation, recommendation, and automated decision support.
            </p>
            <div className="stats-grid">
              <div>
                <strong>100% AI Driven</strong>
                <span>Automated insurance document processing</span>
              </div>
              <div>
                <strong>Instant Feedback</strong>
                <span>Actionable insights in seconds</span>
              </div>
              <div>
                <strong>Cleaner Reports</strong>
                <span>Claims & Prior Authorization Insights</span>
              </div>
            </div>
          </div>

          <div className="upload-card">
            <div className={`drop-zone ${dragActive ? 'active' : ''}`} onDrop={handleDrop} onDragOver={handleDragOver} onDragLeave={handleDragLeave}>
              <div className="drop-icon">📄</div>
              <h2>Upload Insurance Document</h2>
              <p>Drag & drop here, or click to choose a file.</p>
              <label className="file-label">
                <input
                  type="file"
                  accept=".pdf,.jpg,.jpeg,.png,.docx,application/pdf,image/jpeg,image/png,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                  onChange={handleFileChange}
                />
                Browse file
              </label>
              {file && <p className="file-meta">Selected Document: {file.name}</p>}
            </div>

            <button type="button" className="primary-btn" onClick={(event) => handleSubmit(event as unknown as FormEvent<HTMLFormElement>)} disabled={loading}>
              {loading ? 'Analyzing...' : 'Analyze Document'}
            </button>

            {error && <div className="alert error">{error}</div>}
          </div>
        </div>
      )}

      {result && (
        <section className="result-panel">
          <div className="result-header">
            <div>
              <p className="badge">Analysis Complete</p>
              <h2>Document Results</h2>
            </div>
            <button type="button" className="secondary-btn" onClick={handleReset}>
              Upload another document
            </button>
          </div>

          <section className="score-strip">
            <div>
              <span>Recommendation</span>
              <strong
                className={recommendation?.recommendation === 'APPROVED' ? 'decision-approved'
                  : recommendation?.recommendation === 'REJECTED' ? 'decision-rejected' : recommendation?.recommendation === 'MANUAL_REVIEW' ? 'decision-review' : 'decision-pending'
                } > {recommendation?.recommendation === 'APPROVED' ? '✅ Approved' : recommendation?.recommendation === 'REJECTED' ? '❌ Rejected' : recommendation?.recommendation === 'MANUAL_REVIEW' ? '⚠️ Manual Review' : '⏳ Pending Assessment'}
              </strong>
            </div>

            <div>
              <span>Confidence</span>
              <strong>{recommendation ? `${(recommendation.confidence * 100).toFixed(0)}%` : 'N/A'}</strong>
            </div>

            <div>
              <span>Document Type</span>
              <strong>{isClaim ? '📄 Claim' : '🏥 Prior Authorization'}</strong>
            </div>
          </section>

          <div className="review-layout">
            <div className="primary-review">
              <article className="detail-card case-card">
                <h3>Case Summary</h3>
                {isClaim && result.claimDetails && (
                  <>
                    <p><strong>Patient Name:</strong>{' '}
                      {String(result.claimDetails.patientName ?? 'N/A')}
                    </p>
                    <p><strong>Policy Number:</strong>{' '}
                      {String(result.claimDetails.policyNumber ?? 'N/A')}
                    </p>
                    <p><strong>Diagnosis:</strong>{' '}
                      {String(result.claimDetails.diagnosis ?? 'N/A')}
                    </p>
                    <p><strong>Hospital:</strong>{' '}
                      {String(result.claimDetails.hospitalName ?? 'N/A')}
                    </p>
                    <p><strong>Claim Amount:</strong>{' '}
                      ${String(result.claimDetails.claimAmount ?? 'N/A')}
                    </p>
                  </>
                )}

                {isPriorAuthorization &&
                  result.priorAuthorizationDetails && (
                    <>
                      <p><strong>Patient Name:</strong>{' '}
                        {String(result.priorAuthorizationDetails.patientName ?? 'N/A')}
                      </p>

                      <p><strong>Policy Number:</strong>{' '}
                        {String(result.priorAuthorizationDetails.policyNumber ?? 'N/A')}
                      </p>

                      <p><strong>Diagnosis:</strong>{' '}
                        {String(result.priorAuthorizationDetails.primaryDiagnosis ?? 'N/A')}
                      </p>

                      <p><strong>Requested Procedure:</strong>{' '}
                        {String(result.priorAuthorizationDetails.requestedProcedure ?? 'N/A')}
                      </p>

                      <p><strong>Estimated Cost:</strong>{' '}
                        ${String(result.priorAuthorizationDetails.estimatedCost ?? 'N/A')}
                      </p>
                    </>
                  )}
              </article>

              <article className="detail-card accent-card decision-card">
                <h3>Decision Summary</h3>
                {result.recommendationResult ? (
                  <>
                    <div className={recommendation?.recommendation === 'APPROVED' ? 'decision-approved'
                      : recommendation?.recommendation === 'REJECTED' ? 'decision-rejected' : recommendation?.recommendation === 'MANUAL_REVIEW' ? 'decision-review' : 'decision-pending'}
                    > {recommendation?.recommendation === 'APPROVED' ? '✅ Approved'
                      : recommendation?.recommendation === 'REJECTED' ? '❌ Rejected' : recommendation?.recommendation === 'MANUAL_REVIEW' ? '⚠️ Manual Review' : '⏳ Pending Assessment'}
                    </div>
                    <p>{result.recommendationResult.reason}</p>
                    <p><strong>Confidence:</strong> {(result.recommendationResult.confidence * 100).toFixed(0)}%</p>
                    <ul>
                      {result.recommendationResult.observations.map((obs) => (
                        <li key={obs}>{obs}</li>
                      ))}
                    </ul>
                  </>)
                  : (
                    <>
                      <div className="decision-pending">⏳ Pending Assessment </div>
                      <p> Recommendation could not be generated because the uploaded document did not pass validation.</p>
                      <h4>Required Action</h4>
                      <ul>
                        <li>Review the validation errors shown in the validation summary card.</li>
                        <li>Correct missing or invalid fields.</li>
                        <li>Upload the updated document.</li>
                      </ul>
                    </>
                  )}
              </article>
            </div>
            <div className="secondary-review">
              <article className="detail-card support-card">
                <h3>Document Information</h3>
                <p><strong>File:</strong> {result.fileName}</p>
                <p><strong>Record ID:</strong> {result.claimId || 'Pending'}</p>
                <p><strong>Document Type:</strong> {isClaim ? 'Claim' : 'Prior Authorization'}</p>
                <p><strong>File Type:</strong> {result.contentType}</p>
                <p><strong>Size:</strong> {Math.round(result.size / 1024)} KB</p>
                <p><strong>Status:</strong>{result.message}</p>
              </article>

              {result.validationResult && (
                <article className={`detail-card support-card ${result.validationResult.valid ? 'success' : 'warning'}`}>
                  <h3>Validation Summary</h3>
                  <div
                    className={result.validationResult.valid ? 'validation-passed' : 'validation-failed'}
                  > {result.validationResult.valid ? '✅ Validation Passed' : '⚠️ Validation Failed'}
                  </div>
                  {result.validationResult.valid ? 'All details are Valid' : 'Issues found'}
                  {result.validationResult.errors.length > 0 ? (
                    <ul>
                      {result.validationResult.errors.map((err) => (
                        <li key={err}>{err}</li>
                      ))}
                    </ul>
                  ) : (
                    <p>No validation issues detected.</p>
                  )}
                </article>
              )}
            </div>
          </div>
        </section>
      )
      }
    </div >
  );
}
