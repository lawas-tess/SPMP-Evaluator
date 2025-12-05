import React, { useState, useEffect } from 'react';
import { 
  FaUser, FaChartLine, FaSpinner, FaArrowLeft, FaSync,
  FaExclamationTriangle, FaCheckCircle, FaClock, FaTasks, FaFileAlt
} from 'react-icons/fa';
import { reportAPI } from '../../services/apiService';

const StudentProgress = ({ userId, studentName, onClose }) => {
  const [progress, setProgress] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchProgress = async () => {
    if (!userId) return;
    
    setLoading(true);
    setError(null);
    try {
      const response = await reportAPI.getStudentProgress(userId);
      setProgress(response.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load student progress');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProgress();
  }, [userId]);

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

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-8 text-center">
        <FaSpinner className="animate-spin text-purple-600 text-4xl mx-auto mb-4" />
        <p className="text-gray-600">Loading student progress...</p>
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
        <div className="flex justify-center gap-3">
          <button
            onClick={onClose}
            className="text-gray-600 hover:text-gray-800 flex items-center gap-2"
          >
            <FaArrowLeft /> Back
          </button>
          <button
            onClick={fetchProgress}
            className="text-purple-600 hover:text-purple-700 flex items-center gap-2"
          >
            <FaSync /> Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <button
          onClick={onClose}
          className="text-gray-600 hover:text-purple-600 flex items-center gap-2"
        >
          <FaArrowLeft /> Back to Submissions
        </button>
        <button
          onClick={fetchProgress}
          className="text-purple-600 hover:text-purple-700 flex items-center gap-1 text-sm"
        >
          <FaSync /> Refresh
        </button>
      </div>

      {/* Student Info */}
      <div className="flex items-center gap-4 mb-6 p-4 bg-gray-50 rounded-lg">
        <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center">
          <FaUser className="text-purple-600 text-2xl" />
        </div>
        <div>
          <h2 className="text-2xl font-bold text-gray-900">
            {progress?.studentName || studentName || 'Student'}
          </h2>
          <p className="text-gray-600">{progress?.studentEmail || ''}</p>
        </div>
      </div>

      {/* Progress Overview */}
      <h3 className="text-lg font-bold text-gray-900 mb-4 flex items-center gap-2">
        <FaChartLine className="text-purple-600" /> Progress Overview
      </h3>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        {/* Total Documents */}
        <div className="bg-purple-50 rounded-lg p-4 text-center">
          <FaFileAlt className="text-purple-600 text-2xl mx-auto mb-2" />
          <p className="text-3xl font-bold text-purple-600">
            {progress?.totalDocuments || 0}
          </p>
          <p className="text-sm text-gray-600">Documents</p>
        </div>

        {/* Evaluated Documents */}
        <div className="bg-green-50 rounded-lg p-4 text-center">
          <FaCheckCircle className="text-green-600 text-2xl mx-auto mb-2" />
          <p className="text-3xl font-bold text-green-600">
            {progress?.evaluatedDocuments || 0}
          </p>
          <p className="text-sm text-gray-600">Evaluated</p>
        </div>

        {/* Average Score */}
        <div className={`rounded-lg p-4 text-center border-2 ${getScoreBgColor(progress?.averageScore || 0)}`}>
          <FaChartLine className={`text-2xl mx-auto mb-2 ${getScoreColor(progress?.averageScore || 0)}`} />
          <p className={`text-3xl font-bold ${getScoreColor(progress?.averageScore || 0)}`}>
            {(progress?.averageScore || 0).toFixed(1)}%
          </p>
          <p className="text-sm text-gray-600">Avg. Score</p>
        </div>

        {/* Tasks Completed */}
        <div className="bg-blue-50 rounded-lg p-4 text-center">
          <FaTasks className="text-blue-600 text-2xl mx-auto mb-2" />
          <p className="text-3xl font-bold text-blue-600">
            {progress?.completedTasks || 0}/{progress?.totalTasks || 0}
          </p>
          <p className="text-sm text-gray-600">Tasks Done</p>
        </div>
      </div>

      {/* Task Progress Bar */}
      {progress?.totalTasks > 0 && (
        <div className="mb-8">
          <div className="flex justify-between items-center mb-2">
            <h4 className="font-semibold text-gray-700">Task Completion</h4>
            <span className="text-sm text-gray-600">
              {Math.round((progress.completedTasks / progress.totalTasks) * 100)}%
            </span>
          </div>
          <div className="h-4 bg-gray-200 rounded-full overflow-hidden">
            <div
              className="h-full bg-purple-600 transition-all"
              style={{ width: `${(progress.completedTasks / progress.totalTasks) * 100}%` }}
            />
          </div>
        </div>
      )}

      {/* Recent Documents */}
      {progress?.recentDocuments?.length > 0 && (
        <div className="mb-8">
          <h4 className="font-semibold text-gray-900 mb-3">Recent Submissions</h4>
          <div className="space-y-2">
            {progress.recentDocuments.map((doc, idx) => (
              <div key={idx} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                <div className="flex items-center gap-3">
                  <FaFileAlt className="text-gray-400" />
                  <span className="font-medium text-gray-900 truncate max-w-xs">
                    {doc.fileName}
                  </span>
                </div>
                <div className="flex items-center gap-3">
                  {doc.evaluated ? (
                    <span className={`font-bold ${getScoreColor(doc.score)}`}>
                      {doc.score}%
                    </span>
                  ) : (
                    <span className="text-gray-500 flex items-center gap-1">
                      <FaClock /> Pending
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Pending Tasks */}
      {progress?.pendingTasks?.length > 0 && (
        <div>
          <h4 className="font-semibold text-gray-900 mb-3">Pending Tasks</h4>
          <div className="space-y-2">
            {progress.pendingTasks.map((task, idx) => (
              <div key={idx} className="flex items-center justify-between p-3 bg-yellow-50 rounded-lg border border-yellow-200">
                <div className="flex items-center gap-3">
                  <FaClock className="text-yellow-500" />
                  <span className="font-medium text-gray-900">{task.title}</span>
                </div>
                <div className="text-sm text-gray-600">
                  Due: {task.dueDate ? new Date(task.dueDate).toLocaleDateString() : 'No date'}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Empty State */}
      {!progress?.totalDocuments && !progress?.totalTasks && (
        <div className="text-center py-8 text-gray-500">
          <FaExclamationTriangle className="text-4xl mx-auto mb-3 text-gray-400" />
          <p>No activity data available for this student yet.</p>
        </div>
      )}
    </div>
  );
};

export default StudentProgress;
