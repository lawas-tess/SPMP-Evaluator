import React, { useState, useEffect } from 'react';
import api from '../../services/apiService';

const ParserFeedback = ({ documentId }) => {
  const [feedback, setFeedback] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [detectedClauses, setDetectedClauses] = useState([]);
  const [missingClauses, setMissingClauses] = useState([]);
  const [recommendations, setRecommendations] = useState([]);

  useEffect(() => {
    if (documentId) {
      loadFeedback();
    }
  }, [documentId]);

  const loadFeedback = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/parser/feedback/document/${documentId}`);
      
      if (response.data && response.data.length > 0) {
        const latestFeedback = response.data[0];
        setFeedback(latestFeedback);
        
        // Parse JSON strings
        if (latestFeedback.detectedClauses) {
          setDetectedClauses(JSON.parse(latestFeedback.detectedClauses));
        }
        if (latestFeedback.missingClauses) {
          setMissingClauses(JSON.parse(latestFeedback.missingClauses));
        }
        if (latestFeedback.recommendations) {
          setRecommendations(JSON.parse(latestFeedback.recommendations));
        }
      }
      
      setError(null);
    } catch (err) {
      setError('Failed to load parser feedback');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const generateMockFeedback = async () => {
    try {
      setLoading(true);
      const response = await api.post(`/parser/feedback/${documentId}/generate-mock`);
      setFeedback(response.data);
      
      // Parse JSON strings
      if (response.data.detectedClauses) {
        setDetectedClauses(JSON.parse(response.data.detectedClauses));
      }
      if (response.data.missingClauses) {
        setMissingClauses(JSON.parse(response.data.missingClauses));
      }
      if (response.data.recommendations) {
        setRecommendations(JSON.parse(response.data.recommendations));
      }
      
      setError(null);
    } catch (err) {
      setError('Failed to generate mock feedback');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const getScoreColor = (score) => {
    if (score >= 80) return 'text-green-600';
    if (score >= 60) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getScoreBgColor = (score) => {
    if (score >= 80) return 'bg-green-100';
    if (score >= 60) return 'bg-yellow-100';
    return 'bg-red-100';
  };

  const getSeverityColor = (severity) => {
    switch (severity?.toLowerCase()) {
      case 'high':
        return 'bg-red-100 text-red-800';
      case 'medium':
        return 'bg-yellow-100 text-yellow-800';
      case 'low':
        return 'bg-green-100 text-green-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getPriorityColor = (priority) => {
    switch (priority?.toLowerCase()) {
      case 'high':
        return 'bg-red-500 text-white';
      case 'medium':
        return 'bg-yellow-500 text-white';
      case 'low':
        return 'bg-blue-500 text-white';
      default:
        return 'bg-gray-500 text-white';
    }
  };

  if (loading && !feedback) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-gray-600">Loading feedback...</div>
      </div>
    );
  }

  if (!feedback) {
    return (
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="text-center py-8">
          <p className="text-gray-600 mb-4">No parser feedback available for this document.</p>
          <button
            onClick={generateMockFeedback}
            className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-2 rounded"
            disabled={loading}
          >
            Generate Mock Feedback (Demo)
          </button>
          <p className="text-xs text-gray-400 mt-2">
            Note: This is mock data for demonstration. AI parser integration pending.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {/* Compliance Score */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-xl font-semibold mb-4">Overall Compliance Score</h3>
        <div className="flex items-center justify-center">
          <div className={`text-6xl font-bold ${getScoreColor(feedback.complianceScore)} ${getScoreBgColor(feedback.complianceScore)} rounded-full w-40 h-40 flex items-center justify-center`}>
            {feedback.complianceScore}%
          </div>
        </div>
        <div className="mt-4 text-center text-sm text-gray-600">
          <p>Parser Version: {feedback.parserVersion}</p>
          <p>Analyzed: {new Date(feedback.analyzedAt).toLocaleString()}</p>
        </div>
      </div>

      {/* Detected Clauses */}
      {detectedClauses.length > 0 && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-xl font-semibold mb-4">Detected IEEE 1058 Clauses</h3>
          <div className="space-y-3">
            {detectedClauses.map((clause, index) => (
              <div key={index} className="border border-gray-200 rounded p-4">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <h4 className="font-medium text-gray-900">
                      {clause.clauseId}: {clause.clauseName}
                    </h4>
                    {clause.location && (
                      <p className="text-sm text-gray-500 mt-1">Location: {clause.location}</p>
                    )}
                  </div>
                  <div className="ml-4">
                    <span className={`px-3 py-1 rounded font-semibold ${getScoreColor(clause.score)} ${getScoreBgColor(clause.score)}`}>
                      {clause.score}%
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Missing Clauses */}
      {missingClauses.length > 0 && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-xl font-semibold mb-4 text-red-700">Missing or Incomplete Clauses</h3>
          <div className="space-y-3">
            {missingClauses.map((clause, index) => (
              <div key={index} className="border border-red-200 bg-red-50 rounded p-4">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <h4 className="font-medium text-gray-900">
                      {clause.clauseId}: {clause.clauseName}
                    </h4>
                    {clause.reason && (
                      <p className="text-sm text-gray-600 mt-1">{clause.reason}</p>
                    )}
                  </div>
                  <span className={`px-2 py-1 rounded text-xs font-semibold ml-4 ${getSeverityColor(clause.severity)}`}>
                    {clause.severity?.toUpperCase()}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Recommendations */}
      {recommendations.length > 0 && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-xl font-semibold mb-4">AI Recommendations</h3>
          <div className="space-y-4">
            {recommendations.map((rec, index) => (
              <div key={index} className="border border-blue-200 bg-blue-50 rounded p-4">
                <div className="flex items-start">
                  <span className={`px-2 py-1 rounded text-xs font-semibold mr-3 ${getPriorityColor(rec.priority)}`}>
                    {rec.priority?.toUpperCase()}
                  </span>
                  <div className="flex-1">
                    <p className="text-gray-800">{rec.recommendation}</p>
                    {rec.clauseRef && (
                      <p className="text-sm text-gray-500 mt-1">Related to Clause: {rec.clauseRef}</p>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Analysis Report */}
      {feedback.analysisReport && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-xl font-semibold mb-4">Detailed Analysis</h3>
          <p className="text-gray-700 whitespace-pre-wrap">{feedback.analysisReport}</p>
        </div>
      )}

      <div className="text-center">
        <button
          onClick={generateMockFeedback}
          className="bg-gray-500 hover:bg-gray-600 text-white px-6 py-2 rounded"
          disabled={loading}
        >
          Regenerate Feedback (Demo)
        </button>
      </div>
    </div>
  );
};

export default ParserFeedback;
