import React, { useState, useEffect } from 'react';
import { 
  FaFileAlt, FaSpinner, FaEye, FaEdit, FaSync, FaFilter, 
  FaSearch, FaCheckCircle, FaClock, FaExclamationTriangle, FaUser
} from 'react-icons/fa';
import { documentAPI } from '../../services/apiService';

const SubmissionTracker = ({ onViewReport, onOverrideScore, refreshTrigger }) => {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [statusFilter, setStatusFilter] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  const fetchSubmissions = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await documentAPI.getAllSubmissions(statusFilter);
      setSubmissions(response.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load submissions');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSubmissions();
  }, [statusFilter, refreshTrigger]);

  const filteredSubmissions = submissions.filter((doc) => {
    if (!searchQuery) return true;
    const search = searchQuery.toLowerCase();
    const fullName = `${doc.uploadedBy?.firstName || ''} ${doc.uploadedBy?.lastName || ''}`.toLowerCase();
    return (
      doc.fileName?.toLowerCase().includes(search) ||
      fullName.includes(search) ||
      doc.uploadedBy?.email?.toLowerCase().includes(search)
    );
  });

  const getStatusBadge = (document) => {
    if (document.evaluated) {
      const score = document.complianceScore?.overallScore || 0;
      if (score >= 80) {
        return (
          <span className="px-2 py-1 text-xs font-semibold bg-green-100 text-green-800 rounded-full flex items-center gap-1">
            <FaCheckCircle /> Compliant ({Math.round(score)}%)
          </span>
        );
      } else if (score >= 50) {
        return (
          <span className="px-2 py-1 text-xs font-semibold bg-yellow-100 text-yellow-800 rounded-full flex items-center gap-1">
            <FaExclamationTriangle /> Needs Work ({Math.round(score)}%)
          </span>
        );
      } else {
        return (
          <span className="px-2 py-1 text-xs font-semibold bg-red-100 text-red-800 rounded-full flex items-center gap-1">
            <FaExclamationTriangle /> Non-Compliant ({Math.round(score)}%)
          </span>
        );
      }
    }
    return (
      <span className="px-2 py-1 text-xs font-semibold bg-gray-100 text-gray-800 rounded-full flex items-center gap-1">
        <FaClock /> Pending
      </span>
    );
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

  // Statistics
  const totalSubmissions = submissions.length;
  const evaluatedCount = submissions.filter(d => d.evaluated).length;
  const pendingCount = totalSubmissions - evaluatedCount;
  const avgScore = submissions.filter(d => d.evaluated)
    .reduce((sum, d) => sum + (d.complianceScore?.overallScore || 0), 0) / (evaluatedCount || 1);

  if (loading && submissions.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-8 text-center">
        <FaSpinner className="animate-spin text-purple-600 text-3xl mx-auto mb-3" />
        <p className="text-gray-600">Loading submissions...</p>
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
          onClick={fetchSubmissions}
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
        <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2">
          <FaFileAlt className="text-purple-600" /> Student Submissions
        </h3>
        <button
          onClick={fetchSubmissions}
          disabled={loading}
          className="text-purple-600 hover:text-purple-700 flex items-center gap-1 text-sm disabled:opacity-50"
        >
          <FaSync className={loading ? 'animate-spin' : ''} /> Refresh
        </button>
      </div>

      {/* Statistics Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-6">
        <div className="bg-purple-50 rounded-lg p-3 text-center">
          <p className="text-2xl font-bold text-purple-600">{totalSubmissions}</p>
          <p className="text-xs text-gray-600">Total Submissions</p>
        </div>
        <div className="bg-green-50 rounded-lg p-3 text-center">
          <p className="text-2xl font-bold text-green-600">{evaluatedCount}</p>
          <p className="text-xs text-gray-600">Evaluated</p>
        </div>
        <div className="bg-yellow-50 rounded-lg p-3 text-center">
          <p className="text-2xl font-bold text-yellow-600">{pendingCount}</p>
          <p className="text-xs text-gray-600">Pending</p>
        </div>
        <div className="bg-blue-50 rounded-lg p-3 text-center">
          <p className="text-2xl font-bold text-blue-600">{avgScore.toFixed(1)}%</p>
          <p className="text-xs text-gray-600">Avg. Score</p>
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-col md:flex-row gap-3 mb-4">
        {/* Search */}
        <div className="flex-1 relative">
          <FaSearch className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="Search by student name or file..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:border-purple-500 focus:ring-1 focus:ring-purple-500"
          />
        </div>

        {/* Status Filter */}
        <div className="flex items-center gap-2">
          <FaFilter className="text-gray-400" />
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-lg focus:border-purple-500 focus:ring-1 focus:ring-purple-500"
          >
            <option value="">All Status</option>
            <option value="EVALUATED">Evaluated</option>
            <option value="PENDING">Pending</option>
          </select>
        </div>
      </div>

      {/* Submissions List */}
      {filteredSubmissions.length === 0 ? (
        <div className="text-center py-8 text-gray-500">
          <FaFileAlt className="text-4xl mx-auto mb-3 text-gray-400" />
          <p>No submissions found</p>
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="bg-gray-50 border-b">
                <th className="text-left p-3 font-semibold text-gray-700">Student</th>
                <th className="text-left p-3 font-semibold text-gray-700">Document</th>
                <th className="text-left p-3 font-semibold text-gray-700">Submitted</th>
                <th className="text-left p-3 font-semibold text-gray-700">Status</th>
                <th className="text-left p-3 font-semibold text-gray-700">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredSubmissions.map((doc) => (
                <tr key={doc.id} className="border-b hover:bg-gray-50">
                  <td className="p-3">
                    <div className="flex items-center gap-2">
                      <div className="w-8 h-8 bg-purple-100 rounded-full flex items-center justify-center">
                        <FaUser className="text-purple-600 text-sm" />
                      </div>
                      <div>
                        <p className="font-semibold text-gray-900">
                          {doc.uploadedBy?.firstName && doc.uploadedBy?.lastName
                            ? `${doc.uploadedBy.firstName} ${doc.uploadedBy.lastName}`
                            : doc.uploadedBy?.username || 'Unknown'}
                        </p>
                        <p className="text-xs text-gray-500">
                          {doc.uploadedBy?.email || ''}
                        </p>
                      </div>
                    </div>
                  </td>
                  <td className="p-3">
                    <p className="font-medium text-gray-900 truncate max-w-xs">
                      {doc.fileName}
                    </p>
                  </td>
                  <td className="p-3 text-sm text-gray-600">
                    {formatDate(doc.uploadedAt)}
                  </td>
                  <td className="p-3">
                    {getStatusBadge(doc)}
                  </td>
                  <td className="p-3">
                    <div className="flex gap-2">
                      {doc.evaluated && (
                        <>
                          <button
                            onClick={() => onViewReport && onViewReport(doc)}
                            className="px-2 py-1 text-sm bg-purple-100 text-purple-700 rounded hover:bg-purple-200 flex items-center gap-1"
                          >
                            <FaEye /> View
                          </button>
                          <button
                            onClick={() => onOverrideScore && onOverrideScore(doc)}
                            className="px-2 py-1 text-sm bg-blue-100 text-blue-700 rounded hover:bg-blue-200 flex items-center gap-1"
                          >
                            <FaEdit /> Override
                          </button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default SubmissionTracker;
