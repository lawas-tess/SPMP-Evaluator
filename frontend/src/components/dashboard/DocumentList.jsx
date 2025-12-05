import React, { useState, useEffect } from 'react';
import { 
  FaFilePdf, FaFileWord, FaSpinner, FaEye, FaEdit, FaTrash, 
  FaCheckCircle, FaClock, FaExclamationTriangle, FaSync 
} from 'react-icons/fa';
import { documentAPI } from '../../services/apiService';

const DocumentList = ({ onViewReport, onReplace, refreshTrigger }) => {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [evaluatingId, setEvaluatingId] = useState(null);
  const [deletingId, setDeletingId] = useState(null);

  const fetchDocuments = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await documentAPI.getMyDocuments();
      setDocuments(response.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load documents');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDocuments();
  }, [refreshTrigger]);

  const handleEvaluate = async (documentId) => {
    setEvaluatingId(documentId);
    try {
      await documentAPI.evaluate(documentId);
      await fetchDocuments();
    } catch (err) {
      alert(err.response?.data?.message || 'Evaluation failed');
    } finally {
      setEvaluatingId(null);
    }
  };

  const handleDelete = async (documentId) => {
    if (!window.confirm('Are you sure you want to delete this document?')) return;
    
    setDeletingId(documentId);
    try {
      await documentAPI.delete(documentId);
      await fetchDocuments();
    } catch (err) {
      alert(err.response?.data?.message || 'Delete failed');
    } finally {
      setDeletingId(null);
    }
  };

  const getStatusBadge = (document) => {
    if (document.evaluated) {
      const score = document.complianceScore?.overallScore || 0;
      if (score >= 80) {
        return (
          <span className="px-2 py-1 text-xs font-semibold bg-green-100 text-green-800 rounded-full flex items-center gap-1">
            <FaCheckCircle /> Compliant ({score}%)
          </span>
        );
      } else if (score >= 50) {
        return (
          <span className="px-2 py-1 text-xs font-semibold bg-yellow-100 text-yellow-800 rounded-full flex items-center gap-1">
            <FaExclamationTriangle /> Needs Work ({score}%)
          </span>
        );
      } else {
        return (
          <span className="px-2 py-1 text-xs font-semibold bg-red-100 text-red-800 rounded-full flex items-center gap-1">
            <FaExclamationTriangle /> Non-Compliant ({score}%)
          </span>
        );
      }
    }
    return (
      <span className="px-2 py-1 text-xs font-semibold bg-gray-100 text-gray-800 rounded-full flex items-center gap-1">
        <FaClock /> Pending Evaluation
      </span>
    );
  };

  const getFileIcon = (fileName) => {
    if (fileName?.toLowerCase().endsWith('.pdf')) {
      return <FaFilePdf className="text-red-500 text-2xl" />;
    }
    return <FaFileWord className="text-blue-500 text-2xl" />;
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-8 text-center">
        <FaSpinner className="animate-spin text-purple-600 text-3xl mx-auto mb-3" />
        <p className="text-gray-600">Loading documents...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-8 text-center">
        <div className="text-red-500 mb-4">
          <FaExclamationTriangle className="text-3xl mx-auto mb-2" />
          <p>{error}</p>
        </div>
        <button 
          onClick={fetchDocuments}
          className="text-purple-600 hover:text-purple-700 font-semibold flex items-center gap-2 mx-auto"
        >
          <FaSync /> Retry
        </button>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      <div className="flex justify-between items-center mb-6">
        <h3 className="text-xl font-bold text-gray-900">My Documents</h3>
        <button
          onClick={fetchDocuments}
          className="text-purple-600 hover:text-purple-700 flex items-center gap-1 text-sm"
        >
          <FaSync /> Refresh
        </button>
      </div>

      {documents.length === 0 ? (
        <div className="text-center py-8 text-gray-500">
          <FaFilePdf className="text-4xl mx-auto mb-3 text-gray-400" />
          <p>No documents uploaded yet</p>
          <p className="text-sm mt-1">Upload your first SPMP document to get started</p>
        </div>
      ) : (
        <div className="space-y-4">
          {documents.map((doc) => (
            <div
              key={doc.id}
              className="border border-gray-200 rounded-lg p-4 hover:border-purple-300 transition"
            >
              <div className="flex items-start gap-4">
                {getFileIcon(doc.fileName)}
                
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <h4 className="font-semibold text-gray-900 truncate">{doc.fileName}</h4>
                      <p className="text-sm text-gray-500 mt-1">
                        Uploaded: {formatDate(doc.uploadedAt)}
                      </p>
                    </div>
                    {getStatusBadge(doc)}
                  </div>

                  {/* Action Buttons */}
                  <div className="flex gap-2 mt-3">
                    {doc.evaluated ? (
                      <button
                        onClick={() => onViewReport && onViewReport(doc)}
                        className="px-3 py-1.5 text-sm bg-purple-100 text-purple-700 rounded hover:bg-purple-200 flex items-center gap-1"
                      >
                        <FaEye /> View Report
                      </button>
                    ) : (
                      <button
                        onClick={() => handleEvaluate(doc.id)}
                        disabled={evaluatingId === doc.id}
                        className="px-3 py-1.5 text-sm bg-green-100 text-green-700 rounded hover:bg-green-200 flex items-center gap-1 disabled:opacity-50"
                      >
                        {evaluatingId === doc.id ? (
                          <>
                            <FaSpinner className="animate-spin" /> Evaluating...
                          </>
                        ) : (
                          <>
                            <FaCheckCircle /> Evaluate
                          </>
                        )}
                      </button>
                    )}
                    
                    <button
                      onClick={() => onReplace && onReplace(doc)}
                      className="px-3 py-1.5 text-sm bg-blue-100 text-blue-700 rounded hover:bg-blue-200 flex items-center gap-1"
                    >
                      <FaEdit /> Replace
                    </button>
                    
                    <button
                      onClick={() => handleDelete(doc.id)}
                      disabled={deletingId === doc.id}
                      className="px-3 py-1.5 text-sm bg-red-100 text-red-700 rounded hover:bg-red-200 flex items-center gap-1 disabled:opacity-50"
                    >
                      {deletingId === doc.id ? (
                        <FaSpinner className="animate-spin" />
                      ) : (
                        <>
                          <FaTrash /> Delete
                        </>
                      )}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default DocumentList;
