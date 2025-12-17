import React, { useState, useEffect } from 'react';
import api from '../../services/apiService';

const AdminDashboard = ({ onTabChange }) => {
  const [reports, setReports] = useState({
    users: null,
    submissions: null,
    evaluations: null
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchReports();
  }, []);

  const fetchReports = async () => {
    try {
      const [users, submissions, evaluations] = await Promise.all([
        api.get('/admin/reports/users'),
        api.get('/admin/reports/submissions'),
        api.get('/admin/reports/evaluations')
      ]);
      setReports({
        users: users.data,
        submissions: submissions.data,
        evaluations: evaluations.data
      });
    } catch (error) {
      console.error('Failed to fetch reports:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="p-6">Loading...</div>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6">Admin Dashboard</h1>

      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm text-gray-600 mb-1">Total Users</div>
          <div className="text-3xl font-bold text-blue-600">
            {reports.users?.totalUsers || 0}
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm text-gray-600 mb-1">Students</div>
          <div className="text-3xl font-bold text-green-600">
            {reports.users?.totalStudents || 0}
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm text-gray-600 mb-1">Professors</div>
          <div className="text-3xl font-bold text-purple-600">
            {reports.users?.totalProfessors || 0}
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm text-gray-600 mb-1">Admins</div>
          <div className="text-3xl font-bold text-red-600">
            {reports.users?.totalAdmins || 0}
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm text-gray-600 mb-1">Total Submissions</div>
          <div className="text-3xl font-bold text-indigo-600">
            {reports.submissions?.totalSubmissions || 0}
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm text-gray-600 mb-1">Total Evaluations</div>
          <div className="text-3xl font-bold text-teal-600">
            {reports.evaluations?.totalEvaluations || 0}
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="mt-6 bg-white rounded-lg shadow p-6">
        <h2 className="text-xl font-semibold mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
          <button
            onClick={() => onTabChange && onTabChange('users')}
            className="block p-4 border-2 border-blue-500 rounded-lg text-center hover:bg-blue-50 transition cursor-pointer"
          >
            <div className="text-2xl mb-2">👥</div>
            <div className="font-medium">Manage Users</div>
          </button>
          <button
            onClick={() => onTabChange && onTabChange('assignments')}
            className="block p-4 border-2 border-green-500 rounded-lg text-center hover:bg-green-50 transition cursor-pointer"
          >
            <div className="text-2xl mb-2">📝</div>
            <div className="font-medium">Assign Students</div>
          </button>
          <button
            onClick={() => onTabChange && onTabChange('audit')}
            className="block p-4 border-2 border-purple-500 rounded-lg text-center hover:bg-purple-50 transition cursor-pointer"
          >
            <div className="text-2xl mb-2">📋</div>
            <div className="font-medium">Audit Logs</div>
          </button>
          <button
            onClick={() => onTabChange && onTabChange('reports')}
            className="block p-4 border-2 border-orange-500 rounded-lg text-center hover:bg-orange-50 transition cursor-pointer"
          >
            <div className="text-2xl mb-2">📊</div>
            <div className="font-medium">System Reports</div>
          </button>
          <button
            onClick={() => onTabChange && onTabChange('settings')}
            className="block p-4 border-2 border-red-500 rounded-lg text-center hover:bg-red-50 transition cursor-pointer"
          >
            <div className="text-2xl mb-2">⚙️</div>
            <div className="font-medium">System Settings</div>
          </button>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;