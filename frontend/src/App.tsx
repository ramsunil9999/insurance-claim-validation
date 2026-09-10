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

  return (
    <div className="page">
      {!result && (
        <div className="hero-panel">
          <div className="hero-copy">
            <span className="eyebrow">Claims & Prior Authorizations Powered by AI</span>
            <h1>Insurance Document Intelligence Platform</h1>
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

          <div className="cards-grid stacked">
            <article className="detail-card">
              <h3>Document Overview</h3>
              <p><strong>File:</strong> {result.fileName}</p>
              <p><strong>Record ID:</strong> {result.claimId || 'Pending'}</p>
              <p><strong>Document Type:</strong> {result.documentType === 'CLAIM' ? 'Claim' : result.documentType === 'PRIOR AUTHORIZATION' ? 'Prior Authorization' : 'N/A'}</p>
              <p><strong>Type:</strong> {result.contentType || 'PDF Upload'}</p>
              <p><strong>Size:</strong> {Math.round(result.size / 1024)} KB</p>
              <p><strong>Status:</strong> {result.message}</p>
            </article>

            {result.recommendationResult && (
              <article className="detail-card accent-card">
                <h3>Recommendation</h3>
                <p className="big-text">{result.recommendationResult.recommendation}</p>
                <p>{result.recommendationResult.reason}</p>
                <p><strong>Confidence:</strong> {(result.recommendationResult.confidence * 100).toFixed(0)}%</p>
                <ul>
                  {result.recommendationResult.observations.map((obs) => (
                    <li key={obs}>{obs}</li>
                  ))}
                </ul>
              </article>
            )}

            {result.validationResult && (
              <article className={`detail-card ${result.validationResult.valid ? 'success' : 'warning'}`}>
                <h3>Validation</h3>
                <p><strong>Status:</strong> {result.validationResult.valid ? 'Valid' : 'Issues found'}</p>
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

            {result.documentType === 'CLAIM' && result.claimDetails && (
              <article className="detail-card">
                <h3>Extracted Details</h3>
                <p><strong>Patient Name:</strong>{' '}
                  {String(result.claimDetails.patientName ?? 'N/A')}
                </p>

                <p><strong>Policy Number:</strong>{' '}
                  {String(result.claimDetails.policyNumber ?? 'N/A')}
                </p>

                <p><strong>Diagnosis:</strong>{' '}
                  {String(result.claimDetails.diagnosis ?? 'N/A')}
                </p>

                <p><strong>Hospital Name:</strong>{' '}
                  {String(result.claimDetails.hospitalName ?? 'N/A')}
                </p>

                <p><strong>Claim Amount: </strong>{' $'}
                  {String(result.claimDetails.claimAmount ?? 'N/A')}
                </p>
              </article>
            )}

            {result.documentType === 'PRIOR AUTHORIZATION' && result.priorAuthorizationDetails && (
                <article className="detail-card">
                  <h3>Extracted Details</h3>
                  <p><strong>Patient Name:</strong>{' '}
                    {String(result.priorAuthorizationDetails.patientName ?? 'N/A')}
                  </p>

                  <p><strong>Policy Number:</strong>{' '}
                    {String(result.priorAuthorizationDetails.policyNumber ?? 'N/A')}
                  </p>

                  <p><strong>Primary Diagnosis:</strong>{' '}
                    {String(result.priorAuthorizationDetails.primaryDiagnosis ?? 'N/A')}
                  </p>

                  <p><strong>Requested Procedure:</strong>{' '}
                    {String(result.priorAuthorizationDetails.requestedProcedure ?? 'N/A')}
                  </p>

                  <p><strong>Procedure Category:</strong>{' '}
                    {String(result.priorAuthorizationDetails.procedureCategory ?? 'N/A')}
                  </p>

                  <p><strong>Estimated Cost: </strong>{' $'}
                    {String(result.priorAuthorizationDetails.estimatedCost ?? 'N/A')}
                  </p>
                </article>
              )}
          </div>
        </section>
      )}
    </div>
  );
}
