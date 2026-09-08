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

type ClaimResponse = {
  claimId: string;
  fileName: string;
  contentType: string;
  size: number;
  message: string;
  extractedText?: string;
  claimDetails?: Record<string, unknown>;
  validationResult?: ValidationResult;
  recommendationResult?: RecommendationResult;
};

export default function App() {
  const [file, setFile] = useState<File | null>(null);
  const [result, setResult] = useState<ClaimResponse | null>(null);
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [dragActive, setDragActive] = useState(false);

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFile(event.target.files?.[0] ?? null);
    setError('');
  };

  const handleDrop = (event: React.DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    setDragActive(false);
    const droppedFile = event.dataTransfer.files?.[0];
    if (droppedFile) {
      setFile(droppedFile);
      setError('');
    }
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
      setError('Please select a PDF file first.');
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
        throw new Error(data?.message || 'Unable to process the PDF.');
      }

      setResult(data as ClaimResponse);
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
            <span className="eyebrow">Smart claims, faster decisions</span>
            <h1>Insurance Claim Validation with AI</h1>
            <p>
              Upload your claim PDF and get instant validation, confidence scoring, and AI-backed recommendations in a beautifully responsive interface.
            </p>
            <div className="stats-grid">
              <div>
                <strong>100% AI Driven</strong>
                <span>Automated claim checks</span>
              </div>
              <div>
                <strong>Instant Feedback</strong>
                <span>Actionable insights in seconds</span>
              </div>
              <div>
                <strong>Cleaner Reports</strong>
                <span>Vibrant results and recommendations</span>
              </div>
            </div>
          </div>

          <div className="upload-card">
            <div className={`drop-zone ${dragActive ? 'active' : ''}`} onDrop={handleDrop} onDragOver={handleDragOver} onDragLeave={handleDragLeave}>
              <div className="drop-icon">📄</div>
              <h2>Upload your claim PDF</h2>
              <p>Drag & drop here, or click to choose a file.</p>
              <label className="file-label">
                <input type="file" accept="application/pdf" onChange={handleFileChange} />
                Browse file
              </label>
              {file && <p className="file-meta">Selected: {file.name}</p>}
            </div>

            <button type="button" className="primary-btn" onClick={(event) => handleSubmit(event as unknown as FormEvent<HTMLFormElement>)} disabled={loading}>
              {loading ? 'Analyzing...' : 'Analyze Claim'}
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
              <h2>Claim results</h2>
            </div>
            <button type="button" className="secondary-btn" onClick={handleReset}>
              Upload another claim
            </button>
          </div>

          <div className="cards-grid stacked">
            <article className="detail-card">
              <h3>Claim Overview</h3>
              <p><strong>File:</strong> {result.fileName}</p>
              <p><strong>Claim ID:</strong> {result.claimId || 'Pending'}</p>
              <p><strong>Type:</strong> {result.contentType || 'PDF Upload'}</p>
              <p><strong>Size:</strong> {Math.round(result.size / 1024)} KB</p>
              <p><strong>Status:</strong> {result.message}</p>
            </article>

            {result.recommendationResult && (
              <article className="detail-card accent-card">
                <h3>Recommendation</h3>
                <p className="big-text">{result.recommendationResult.recommendation}</p>
                <p>{result.recommendationResult.reason}</p>
                <p><strong>Confidence:</strong> {result.recommendationResult.confidence}%</p>
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
          </div>
        </section>
      )}
    </div>
  );
}
