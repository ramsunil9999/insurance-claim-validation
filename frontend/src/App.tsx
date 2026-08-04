import { useState } from 'react';
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

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFile(event.target.files?.[0] ?? null);
    setError('');
    setResult(null);
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!file) {
      setError('Please select a PDF file first.');
      return;
    }

    setLoading(true);
    setError('');
    setResult(null);

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
      <div className="card">
        <h1>AI Powered Insurance Claim Validation</h1>
        <p>Upload a PDF claim document and receive AI-based validation and recommendation results.</p>

        <form onSubmit={handleSubmit}>
          <input type="file" accept="application/pdf" onChange={handleFileChange} />
          <button type="submit" disabled={loading}>
            {loading ? 'Analyzing...' : 'Analyze Claim'}
          </button>
        </form>

        {error && <div className="alert error">{error}</div>}

        {result && (
          <div className="result-box">
            <h2>Result Summary</h2>
            <p><strong>File:</strong> {result.fileName}</p>
            <p><strong>Status:</strong> {result.message}</p>
            <p><strong>Claim ID:</strong> {result.claimId || 'Pending'}</p>

            {result.recommendationResult && (
              <div className="panel">
                <h3>Recommendation Result</h3>
                <p><strong>Recommendation:</strong> {result.recommendationResult.recommendation}</p>
                <p><strong>Reason:</strong> {result.recommendationResult.reason}</p>
                <p><strong>Confidence:</strong> {result.recommendationResult.confidence}</p>
                <p><strong>Observations:</strong></p>
                <ul>
                  {result.recommendationResult.observations.map((obs) => (
                    <li key={obs}>{obs}</li>
                  ))}
                </ul>
              </div>
            )}

            {result.validationResult && !result.validationResult.valid && (
              <div className="panel">
                <h3>Validation Result</h3>
                <p><strong>Valid:</strong> {String(result.validationResult.valid)}</p>
                <ul>
                  {result.validationResult.errors.map((err) => (
                    <li key={err}>{err}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
