import React, { useState, useEffect } from 'react';
import { 
  FaChartBar, FaCheckCircle, FaExclamationTriangle, FaTimes, 
  FaSpinner, FaLightbulb, FaArrowLeft, FaPrint, FaDownload, FaHistory
} from 'react-icons/fa';
import { documentAPI } from '../../services/apiService';

const EvaluationResults = ({ document, onClose }) => {
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [expandedSection, setExpandedSection] = useState(null);
  const [history, setHistory] = useState([]);
  const [exporting, setExporting] = useState(false);

  useEffect(() => {
    if (document?.id) {
      fetchReport();
      fetchHistory();
    }
  }, [document?.id]);

  const fetchReport = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await documentAPI.getReport(document.id);
      setReport(response.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load evaluation report');
    } finally {
      setLoading(false);
    }
  };

  const fetchHistory = async () => {
    try {
      const response = await documentAPI.getHistory(document.id);
      setHistory(response.data || []);
    } catch (err) {
      // history is optional; fail silently to not block report view
      setHistory([]);
    }
  };

  const downloadFile = async (type) => {
    setExporting(true);
    try {
      const apiCall = type === 'pdf' ? documentAPI.exportPdf : documentAPI.exportExcel;
      const res = await apiCall(document.id);
      const blob = new Blob([res.data], { type: type === 'pdf' ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const url = window.URL.createObjectURL(blob);
      const link = window.document.createElement('a');
      link.href = url;
      link.download = `spmp-report-${document.id}.${type === 'pdf' ? 'pdf' : 'xlsx'}`;
      link.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setError(err.response?.data || 'Export failed');
    } finally {
      setExporting(false);
    }
  };

  const getScoreColor = (score) => {
    if (score >= 80) return 'text-green-600';
    if (score >= 50) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getScoreBgColor = (score) => {
    if (score >= 80) return 'bg-green-100 border-green-300';
    if (score >= 50) return 'bg-yellow-100 border-yellow-300';
    return 'bg-red-100 border-red-300';
  };

  const getScoreIcon = (score) => {
    if (score >= 80) return <FaCheckCircle className="text-green-500" />;
    if (score >= 50) return <FaExclamationTriangle className="text-yellow-500" />;
    return <FaTimes className="text-red-500" />;
  };

  const severityTone = {
    HIGH: { bg: 'bg-red-100 text-red-700 border-red-300', label: 'High (missing core content)' },
    MEDIUM: { bg: 'bg-yellow-100 text-yellow-700 border-yellow-300', label: 'Medium (low coverage)' },
    LOW: { bg: 'bg-blue-100 text-blue-700 border-blue-300', label: 'Low (minor gaps)' },
    INFO: { bg: 'bg-green-100 text-green-700 border-green-300', label: 'Info (healthy)' }
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-8 text-center">
        <FaSpinner className="animate-spin text-purple-600 text-4xl mx-auto mb-4" />
        <p className="text-gray-600">Loading evaluation report...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-8">
        <div className="text-center text-red-500 mb-4">
          <FaExclamationTriangle className="text-3xl mx-auto mb-2" />
          <p>{error}</p>
        </div>
        <button
          onClick={onClose}
          className="text-purple-600 hover:text-purple-700 flex items-center gap-2 mx-auto"
        >
          <FaArrowLeft /> Back to Documents
        </button>
      </div>
    );
  }

  const overallScore = report?.overallScore || document?.complianceScore?.overallScore || 0;
  const sectionAnalyses = report?.sectionAnalyses || [];

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <button
          onClick={onClose}
          className="text-gray-600 hover:text-purple-600 flex items-center gap-2"
        >
          <FaArrowLeft /> Back to Documents
        </button>
        <div className="flex gap-2">
          <button
            onClick={() => window.print()}
            className="text-purple-600 hover:text-purple-700 flex items-center gap-2"
          >
            <FaPrint /> Print
          </button>
          <button
            onClick={() => downloadFile('pdf')}
            disabled={exporting}
            className="text-purple-600 hover:text-purple-700 flex items-center gap-2 disabled:opacity-50"
          >
            <FaDownload /> PDF
          </button>
          <button
            onClick={() => downloadFile('excel')}
            disabled={exporting}
            className="text-purple-600 hover:text-purple-700 flex items-center gap-2 disabled:opacity-50"
          >
            <FaDownload /> Excel
          </button>
        </div>
      </div>

      {/* Document Info */}
      <div className="border-b pb-4 mb-6">
        <h2 className="text-2xl font-bold text-gray-900">{document?.fileName}</h2>
        <p className="text-gray-600">IEEE 1058 Compliance Evaluation Report</p>
      </div>

      {/* Overall Score Card */}
      <div className={`p-6 rounded-lg border-2 mb-8 ${getScoreBgColor(overallScore)}`}>
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold text-gray-700">Overall Compliance Score</h3>
            <p className="text-sm text-gray-600">Based on IEEE 1058 standard evaluation</p>
          </div>
          <div className="text-right">
            <div className={`text-5xl font-bold ${getScoreColor(overallScore)}`}>
              {overallScore}%
            </div>
            <div className="flex items-center gap-2 justify-end mt-1">
              {getScoreIcon(overallScore)}
              <span className={`font-semibold ${getScoreColor(overallScore)}`}>
                {overallScore >= 80 ? 'Compliant' : overallScore >= 50 ? 'Needs Improvement' : 'Non-Compliant'}
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Section-by-Section Analysis */}
      <div className="mb-8">
        <h3 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
          <FaChartBar className="text-purple-600" />
          Section-by-Section Analysis
        </h3>
        
        <div className="space-y-3">
          {sectionAnalyses.length > 0 ? sectionAnalyses.map((analysis, index) => {
            const score = analysis?.sectionScore || 0;
            const isExpanded = expandedSection === analysis.id;

            return (
              <div
                key={analysis.id || index}
                className="border rounded-lg overflow-hidden"
              >
                <button
                  onClick={() => setExpandedSection(isExpanded ? null : analysis.id)}
                  className="w-full p-4 flex items-center justify-between hover:bg-gray-50 transition"
                >
                  <div className="flex items-center gap-3">
                    <span className="bg-purple-600 text-white rounded-full w-6 h-6 flex items-center justify-center text-xs font-bold">
                      {index + 1}
                    </span>
                    <span className="font-semibold text-gray-900">{analysis.sectionName}</span>
                    {analysis.sectionWeight ? (
                      <span className="text-xs text-gray-500 ml-2">Weight: {analysis.sectionWeight}%</span>
                    ) : null}
                    {analysis.severity && (
                      <span className={`text-xs font-semibold px-2 py-1 rounded border ${severityTone[analysis.severity]?.bg || 'bg-gray-100 text-gray-700 border-gray-300'}`}>
                        {analysis.severity}
                      </span>
                    )}
                  </div>
                  <div className="flex items-center gap-3">
                    {/* Progress Bar */}
                    <div className="w-32 h-2 bg-gray-200 rounded-full overflow-hidden">
                      <div
                        className={`h-full ${
                          score >= 80 ? 'bg-green-500' : score >= 50 ? 'bg-yellow-500' : 'bg-red-500'
                        }`}
                        style={{ width: `${score}%` }}
                      />
                    </div>
                    <span className={`font-bold min-w-[3rem] text-right ${getScoreColor(score)}`}>
                      {Math.round(score)}%
                    </span>
                    {getScoreIcon(score)}
                  </div>
                </button>

                {isExpanded && (
                  <div className="p-4 bg-gray-50 border-t space-y-4">
                    {analysis.coverage !== undefined && (
                      <div className="text-sm text-gray-600">Coverage: {Math.round(analysis.coverage)}%</div>
                    )}

                    {analysis.findings && (
                      <div className="mb-4">
                        <h5 className="font-semibold text-gray-700 mb-2">Findings:</h5>
                        <p className="text-gray-600">{analysis.findings}</p>
                      </div>
                    )}
                    
                    {analysis.recommendations && (
                      <div className="bg-blue-50 border border-blue-200 rounded p-3">
                        <h5 className="font-semibold text-blue-700 mb-2 flex items-center gap-2">
                          <FaLightbulb /> Recommendations:
                        </h5>
                        <p className="text-blue-800 text-sm">{analysis.recommendations}</p>
                      </div>
                    )}

                    {analysis.missingSubclauses && analysis.missingSubclauses.length > 0 && (
                      <div className="bg-red-50 border border-red-200 rounded p-3">
                        <h5 className="font-semibold text-red-700 mb-2 flex items-center gap-2">
                          <FaExclamationTriangle /> Missing / Weak Subclauses
                        </h5>
                        <ul className="list-disc pl-5 text-sm text-red-800 space-y-1">
                          {analysis.missingSubclauses.map((item, idx) => (
                            <li key={idx}>{item}</li>
                          ))}
                        </ul>
                      </div>
                    )}

                    {analysis.evidenceSnippet && (
                      <div className="bg-white border border-gray-200 rounded p-3">
                        <h5 className="font-semibold text-gray-700 mb-2">Evidence snippet</h5>
                        <p className="text-sm text-gray-700 whitespace-pre-wrap">“{analysis.evidenceSnippet}”</p>
                      </div>
                    )}

                    {!analysis.findings && !analysis.recommendations && (
                      <p className="text-gray-500 italic">No detailed analysis available for this section.</p>
                    )}
                  </div>
                )}
              </div>
            );
          }) : (
            <p className="text-gray-500 italic text-center py-4">No section analysis data available.</p>
          )}
        </div>
      </div>

      {/* Summary */}
      <div className="bg-purple-50 border border-purple-200 rounded-lg p-6">
        <h3 className="text-lg font-bold text-purple-800 mb-3 flex items-center gap-2">
          <FaLightbulb /> Summary & Next Steps
        </h3>
        <div className="text-purple-900 space-y-2">
          {overallScore >= 80 ? (
            <p>
              Your SPMP document shows strong compliance with IEEE 1058 standards. 
              Minor improvements may still be beneficial. Consider reviewing any sections 
              with scores below 100% for potential enhancements.
            </p>
          ) : overallScore >= 50 ? (
            <p>
              Your SPMP document partially meets IEEE 1058 requirements. Focus on improving 
              sections with lower scores, particularly those marked in yellow or red. 
              Use the recommendations provided to guide your revisions.
            </p>
          ) : (
            <p>
              Your SPMP document needs significant improvements to meet IEEE 1058 standards. 
              Review each section's recommendations carefully and consider restructuring 
              your document to include all required components.
            </p>
          )}
        </div>
      </div>

      {/* History */}
      <div className="mt-6 border border-gray-200 rounded-lg p-4">
        <div className="flex items-center gap-2 mb-3 text-gray-800 font-semibold">
          <FaHistory /> Score History
        </div>
        {history.length === 0 ? (
          <p className="text-sm text-gray-500">No previous evaluations recorded.</p>
        ) : (
          <div className="space-y-3">
            {history.map((item, index) => {
              const scoreDiff = index < history.length - 1 
                ? item.overallScore - history[index + 1].overallScore 
                : 0;
              const isIncrease = scoreDiff > 0;
              const isDecrease = scoreDiff < 0;
              
              return (
                <div key={item.id} className="border border-gray-200 rounded-lg p-3 hover:shadow-md transition">
                  <div className="flex justify-between items-start">
                    <div className="space-y-1 flex-1">
                      <div className="flex items-center gap-2">
                        <span className="inline-block px-2 py-1 bg-purple-100 text-purple-700 text-xs font-semibold rounded">
                          Version {item.versionNumber}
                        </span>
                        <span className={`inline-block px-2 py-1 text-xs font-semibold rounded ${
                          item.source === 'OVERRIDE' ? 'bg-orange-100 text-orange-700' :
                          item.source === 'RE_EVALUATION' ? 'bg-blue-100 text-blue-700' :
                          'bg-green-100 text-green-700'
                        }`}>
                          {item.source || 'EVALUATION'}
                        </span>
                        {scoreDiff !== 0 && (
                          <span className={`inline-block px-2 py-1 text-xs font-semibold rounded ${
                            isIncrease ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600'
                          }`}>
                            {isIncrease ? '↑' : '↓'} {Math.abs(scoreDiff).toFixed(1)}%
                          </span>
                        )}
                      </div>
                      <div className="grid grid-cols-3 gap-2 text-sm">
                        <div>
                          <span className="text-gray-500">Overall:</span>
                          <span className={`ml-1 font-semibold ${getScoreColor(item.overallScore)}`}>
                            {Math.round(item.overallScore)}%
                          </span>
                        </div>
                        <div>
                          <span className="text-gray-500">Structure:</span>
                          <span className="ml-1 font-semibold text-gray-700">
                            {Math.round(item.structureScore)}%
                          </span>
                        </div>
                        <div>
                          <span className="text-gray-500">Completeness:</span>
                          <span className="ml-1 font-semibold text-gray-700">
                            {Math.round(item.completenessScore)}%
                          </span>
                        </div>
                      </div>
                      {item.professorOverride != null && (
                        <div className="mt-2 p-2 bg-orange-50 border border-orange-200 rounded text-sm">
                          <span className="font-semibold text-orange-700">Override Score:</span>
                          <span className="ml-1 text-orange-900">{Math.round(item.professorOverride)}%</span>
                          {item.professorNotes && (
                            <p className="text-xs text-orange-600 mt-1">"{item.professorNotes}"</p>
                          )}
                        </div>
                      )}
                    </div>
                    <div className="text-gray-500 text-right text-xs ml-4">
                      <p className="font-semibold">{item.recordedAt ? new Date(item.recordedAt).toLocaleDateString() : ''}</p>
                      <p>{item.recordedAt ? new Date(item.recordedAt).toLocaleTimeString() : ''}</p>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

export default EvaluationResults;
