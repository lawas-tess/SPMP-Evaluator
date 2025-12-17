import React, { useState, useEffect } from 'react';
import api from '../../services/apiService';

/**
 * UC 2.14: Admin System Reports
 * Generate and view system reports (users, submissions, evaluations)
 */
const AdminReports = () => {
  const [reports, setReports] = useState({
    users: null,
    submissions: null,
    evaluations: null
  });
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('users');
  const [dateRange, setDateRange] = useState({
    startDate: '',
    endDate: ''
  });

  useEffect(() => {
    fetchReports();
  }, []);

  const fetchReports = async () => {
    try {
      setLoading(true);
      const [usersRes, submissionsRes, evaluationsRes] = await Promise.all([
        api.get('/admin/reports/users'),
        api.get('/admin/reports/submissions'),
        api.get('/admin/reports/evaluations')
      ]);
      setReports({
        users: usersRes.data,
        submissions: submissionsRes.data,
        evaluations: evaluationsRes.data
      });
    } catch (error) {
      console.error('Failed to fetch reports:', error);
    } finally {
      setLoading(false);
    }
  };

  const exportReport = async (reportType) => {
    try {
      const params = dateRange.startDate && dateRange.endDate 
        ? `?startDate=${dateRange.startDate}&endDate=${dateRange.endDate}`
        : '';
      
      const response = await api.get(`/admin/reports/${reportType}/export${params}`, {
        responseType: 'blob'
      });
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${reportType}_report_${new Date().toISOString().split('T')[0]}.csv`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Failed to export report:', error);
      alert('Failed to export report');
    }
  };

  if (loading) {
    return <div className="p-6">Loading reports...</div>;
  }

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold">System Reports</h2>
        <button
          onClick={fetchReports}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          Refresh Reports
        </button>
      </div>

      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm text-gray-600 mb-1">Total Users</div>
          <div className="text-3xl font-bold text-blue-600">
            {reports.users?.totalUsers || 0}
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm text-gray-600 mb-1">Total Submissions</div>
          <div className="text-3xl font-bold text-green-600">
            {reports.submissions?.totalSubmissions || 0}
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm text-gray-600 mb-1">Completed Evaluations</div>
          <div className="text-3xl font-bold text-purple-600">
            {reports.evaluations?.totalEvaluations || 0}
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-sm text-gray-600 mb-1">Average Score</div>
          <div className="text-3xl font-bold text-orange-600">
            {reports.evaluations?.averageScore?.toFixed(1) || '0.0'}%
          </div>
        </div>
      </div>

      {/* Date Range Filter */}
      <div className="bg-white rounded-lg shadow p-4 mb-6">
        <div className="flex gap-4 items-end">
          <div className="flex-1">
            <label className="block text-sm font-medium mb-1">Start Date</label>
            <input
              type="date"
              value={dateRange.startDate}
              onChange={(e) => setDateRange({ ...dateRange, startDate: e.target.value })}
              className="w-full p-2 border rounded"
            />
          </div>
          <div className="flex-1">
            <label className="block text-sm font-medium mb-1">End Date</label>
            <input
              type="date"
              value={dateRange.endDate}
              onChange={(e) => setDateRange({ ...dateRange, endDate: e.target.value })}
              className="w-full p-2 border rounded"
            />
          </div>
          <button
            onClick={fetchReports}
            className="bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700"
          >
            Apply Filter
          </button>
        </div>
      </div>

      {/* Tabs */}
      <div className="border-b border-gray-200 mb-6">
        <nav className="flex gap-4">
          {['users', 'submissions', 'evaluations'].map(tab => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`py-2 px-4 border-b-2 font-medium text-sm ${
                activeTab === tab
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}
            >
              {tab.charAt(0).toUpperCase() + tab.slice(1)} Report
            </button>
          ))}
        </nav>
      </div>

      {/* Report Content */}
      <div className="bg-white rounded-lg shadow">
        {activeTab === 'users' && (
          <div className="p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-semibold">User Statistics</h3>
              <button
                onClick={() => exportReport('users')}
                className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
              >
                Export CSV
              </button>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="border rounded p-4">
                <div className="text-gray-600 text-sm">Students</div>
                <div className="text-2xl font-bold">{reports.users?.studentCount || 0}</div>
              </div>
              <div className="border rounded p-4">
                <div className="text-gray-600 text-sm">Professors</div>
                <div className="text-2xl font-bold">{reports.users?.professorCount || 0}</div>
              </div>
              <div className="border rounded p-4">
                <div className="text-gray-600 text-sm">Admins</div>
                <div className="text-2xl font-bold">{reports.users?.adminCount || 0}</div>
              </div>
            </div>
            <div className="mt-6">
              <h4 className="font-semibold mb-2">Recent Activity</h4>
              <ul className="space-y-2">
                <li className="text-sm text-gray-600">
                  • New registrations this week: {reports.users?.newUsersThisWeek || 0}
                </li>
                <li className="text-sm text-gray-600">
                  • Active users (last 7 days): {reports.users?.activeUsers || 0}
                </li>
                <li className="text-sm text-gray-600">
                  • Inactive users: {reports.users?.inactiveUsers || 0}
                </li>
              </ul>
            </div>
          </div>
        )}

        {activeTab === 'submissions' && (
          <div className="p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-semibold">Submission Statistics</h3>
              <button
                onClick={() => exportReport('submissions')}
                className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
              >
                Export CSV
              </button>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="border rounded p-4">
                <div className="text-gray-600 text-sm">Total Submissions</div>
                <div className="text-2xl font-bold">{reports.submissions?.totalSubmissions || 0}</div>
              </div>
              <div className="border rounded p-4">
                <div className="text-gray-600 text-sm">Pending Evaluation</div>
                <div className="text-2xl font-bold">{reports.submissions?.pendingEvaluations || 0}</div>
              </div>
              <div className="border rounded p-4">
                <div className="text-gray-600 text-sm">Submissions This Week</div>
                <div className="text-2xl font-bold">{reports.submissions?.submissionsThisWeek || 0}</div>
              </div>
            </div>
            <div className="mt-6">
              <h4 className="font-semibold mb-2">Submission Trends</h4>
              <ul className="space-y-2">
                <li className="text-sm text-gray-600">
                  • Average submissions per student: {reports.submissions?.avgPerStudent?.toFixed(2) || '0.00'}
                </li>
                <li className="text-sm text-gray-600">
                  • Most active student: {reports.submissions?.mostActiveStudent || 'N/A'}
                </li>
                <li className="text-sm text-gray-600">
                  • Peak submission day: {reports.submissions?.peakDay || 'N/A'}
                </li>
              </ul>
            </div>
          </div>
        )}

        {activeTab === 'evaluations' && (
          <div className="p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-semibold">Evaluation Statistics</h3>
              <button
                onClick={() => exportReport('evaluations')}
                className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
              >
                Export CSV
              </button>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="border rounded p-4">
                <div className="text-gray-600 text-sm">Total Evaluations</div>
                <div className="text-2xl font-bold">{reports.evaluations?.totalEvaluations || 0}</div>
              </div>
              <div className="border rounded p-4">
                <div className="text-gray-600 text-sm">Average Score</div>
                <div className="text-2xl font-bold">{reports.evaluations?.averageScore?.toFixed(1) || '0.0'}%</div>
              </div>
              <div className="border rounded p-4">
                <div className="text-gray-600 text-sm">Highest Score</div>
                <div className="text-2xl font-bold">{reports.evaluations?.highestScore?.toFixed(1) || '0.0'}%</div>
              </div>
            </div>
            <div className="mt-6">
              <h4 className="font-semibold mb-2">Performance Metrics</h4>
              <ul className="space-y-2">
                <li className="text-sm text-gray-600">
                  • Passing rate (≥70%): {reports.evaluations?.passingRate?.toFixed(1) || '0.0'}%
                </li>
                <li className="text-sm text-gray-600">
                  • Average evaluation time: {reports.evaluations?.avgEvaluationTime || 'N/A'}
                </li>
                <li className="text-sm text-gray-600">
                  • Most common issues: {reports.evaluations?.commonIssues || 'N/A'}
                </li>
              </ul>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminReports;
