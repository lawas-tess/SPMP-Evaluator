import React, { useState } from 'react';
import { FaSave, FaTimes, FaSpinner, FaExclamationTriangle } from 'react-icons/fa';
import { documentAPI } from '../../services/apiService';

const ScoreOverride = ({ document, onClose, onSuccess }) => {
  const [score, setScore] = useState(document?.complianceScore?.overallScore || 0);
  const [notes, setNotes] = useState(document?.complianceScore?.notes || '');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (score < 0 || score > 100) {
      setError('Score must be between 0 and 100');
      return;
    }

    setSaving(true);
    setError(null);

    try {
      await documentAPI.overrideScore(document.id, score, notes);
      if (onSuccess) {
        onSuccess();
      }
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to override score');
    } finally {
      setSaving(false);
    }
  };

  const getScoreColor = (score) => {
    if (score >= 80) return 'text-green-600';
    if (score >= 50) return 'text-yellow-600';
    return 'text-red-600';
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
        {/* Header */}
        <div className="flex items-center justify-between p-4 border-b">
          <h3 className="text-lg font-bold text-gray-900">Override Compliance Score</h3>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600"
          >
            <FaTimes />
          </button>
        </div>

        {/* Content */}
        <form onSubmit={handleSubmit} className="p-4">
          {/* Document Info */}
          <div className="mb-4 p-3 bg-gray-50 rounded-lg">
            <p className="text-sm text-gray-600">Document:</p>
            <p className="font-semibold text-gray-900 truncate">{document?.fileName}</p>
            <p className="text-sm text-gray-500 mt-1">
              Current Score: <span className={`font-bold ${getScoreColor(document?.complianceScore?.overallScore || 0)}`}>
                {Math.round(document?.complianceScore?.overallScore || 0)}%
              </span>
            </p>
          </div>

          {/* Score Input */}
          <div className="mb-4">
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              New Compliance Score
            </label>
            <div className="flex items-center gap-3">
              <input
                type="number"
                min="0"
                max="100"
                value={score}
                onChange={(e) => setScore(parseInt(e.target.value) || 0)}
                className="w-24 px-3 py-2 border border-gray-300 rounded-lg text-center text-xl font-bold focus:border-purple-500 focus:ring-1 focus:ring-purple-500"
              />
              <span className="text-xl text-gray-600">%</span>
              
              {/* Visual Preview */}
              <div className="flex-1 h-3 bg-gray-200 rounded-full overflow-hidden">
                <div
                  className={`h-full transition-all ${
                    score >= 80 ? 'bg-green-500' : score >= 50 ? 'bg-yellow-500' : 'bg-red-500'
                  }`}
                  style={{ width: `${score}%` }}
                />
              </div>
            </div>
            <p className="text-xs text-gray-500 mt-1">Enter a value between 0 and 100</p>
          </div>

          {/* Notes Input */}
          <div className="mb-4">
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Justification Notes (Optional)
            </label>
            <textarea
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="Explain why you're overriding the AI-generated score..."
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:border-purple-500 focus:ring-1 focus:ring-purple-500 resize-none"
            />
          </div>

          {/* Warning */}
          <div className="mb-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg flex items-start gap-2">
            <FaExclamationTriangle className="text-yellow-500 mt-0.5" />
            <div className="text-sm text-yellow-800">
              <p className="font-semibold">Manual Override</p>
              <p>This will replace the AI-generated compliance score. The action will be logged for auditing purposes.</p>
            </div>
          </div>

          {/* Error */}
          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
              {error}
            </div>
          )}

          {/* Actions */}
          <div className="flex gap-3">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 rounded-lg text-gray-700 font-semibold hover:bg-gray-50 transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={saving}
              className="flex-1 px-4 py-2 bg-purple-600 hover:bg-purple-700 disabled:bg-purple-400 text-white font-semibold rounded-lg transition flex items-center justify-center gap-2"
            >
              {saving ? (
                <>
                  <FaSpinner className="animate-spin" />
                  Saving...
                </>
              ) : (
                <>
                  <FaSave />
                  Save Override
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ScoreOverride;
