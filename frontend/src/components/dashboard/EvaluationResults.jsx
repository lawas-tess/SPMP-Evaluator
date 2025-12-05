import React, { useState, useEffect } from 'react';
import { 
  FaChartBar, FaCheckCircle, FaExclamationTriangle, FaTimes, 
  FaSpinner, FaLightbulb, FaArrowLeft, FaPrint
} from 'react-icons/fa';
import { documentAPI } from '../../services/apiService';

const EvaluationResults = ({ document, onClose }) => {
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [expandedSection, setExpandedSection] = useState(null);

  useEffect(() => {
    if (document?.id) {
      fetchReport();
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

  // IEEE 1058 Sections for display
  const ieee1058Sections = [
    { id: 1, name: 'Scope', key: 'scope' },
    { id: 2, name: 'Standards References', key: 'standardsReferences' },
    { id: 3, name: 'Definitions', key: 'definitions' },
    { id: 4, name: 'Project Overview', key: 'projectOverview' },
    { id: 5, name: 'Project Organization', key: 'projectOrganization' },
    { id: 6, name: 'Managerial Process', key: 'managerialProcess' },
    { id: 7, name: 'Technical Process', key: 'technicalProcess' },
    { id: 8, name: 'Work Packages', key: 'workPackages' },
    { id: 9, name: 'Schedule', key: 'schedule' },
    { id: 10, name: 'Risk Management', key: 'riskManagement' },
    { id: 11, name: 'Closeout Plan', key: 'closeoutPlan' },
    { id: 12, name: 'Annexes', key: 'annexes' }
  ];

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
        <button
          onClick={() => window.print()}
          className="text-purple-600 hover:text-purple-700 flex items-center gap-2"
        >
          <FaPrint /> Print Report
        </button>
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
          {ieee1058Sections.map((section) => {
            const analysis = sectionAnalyses.find(a => 
              a.sectionName?.toLowerCase().includes(section.key.toLowerCase()) ||
              a.sectionNumber === section.id
            );
            const score = analysis?.score || 0;
            const isExpanded = expandedSection === section.id;

            return (
              <div
                key={section.id}
                className="border rounded-lg overflow-hidden"
              >
                <button
                  onClick={() => setExpandedSection(isExpanded ? null : section.id)}
                  className="w-full p-4 flex items-center justify-between hover:bg-gray-50 transition"
                >
                  <div className="flex items-center gap-3">
                    <span className="bg-purple-600 text-white rounded-full w-6 h-6 flex items-center justify-center text-xs font-bold">
                      {section.id}
                    </span>
                    <span className="font-semibold text-gray-900">{section.name}</span>
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
                      {score}%
                    </span>
                    {getScoreIcon(score)}
                  </div>
                </button>

                {isExpanded && analysis && (
                  <div className="p-4 bg-gray-50 border-t">
                    {analysis.findings && analysis.findings.length > 0 && (
                      <div className="mb-4">
                        <h5 className="font-semibold text-gray-700 mb-2">Findings:</h5>
                        <ul className="list-disc pl-5 space-y-1 text-gray-600">
                          {analysis.findings.map((finding, i) => (
                            <li key={i}>{finding}</li>
                          ))}
                        </ul>
                      </div>
                    )}
                    
                    {analysis.recommendations && analysis.recommendations.length > 0 && (
                      <div className="bg-blue-50 border border-blue-200 rounded p-3">
                        <h5 className="font-semibold text-blue-700 mb-2 flex items-center gap-2">
                          <FaLightbulb /> Recommendations:
                        </h5>
                        <ul className="list-disc pl-5 space-y-1 text-blue-800 text-sm">
                          {analysis.recommendations.map((rec, i) => (
                            <li key={i}>{rec}</li>
                          ))}
                        </ul>
                      </div>
                    )}
                    
                    {!analysis.findings?.length && !analysis.recommendations?.length && (
                      <p className="text-gray-500 italic">No detailed analysis available for this section.</p>
                    )}
                  </div>
                )}
              </div>
            );
          })}
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
    </div>
  );
};

export default EvaluationResults;
