import React, { useState, useEffect } from 'react';
import api from '../../services/apiService';

const AuditLogViewer = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    username: '',
    action: '',
    resource: ''
  });

  useEffect(() => {
    fetchLogs();
  }, []);

  const fetchLogs = async () => {
    try {
      let url = '/admin/audit-logs';
      const params = new URLSearchParams();
      if (filters.username) params.append('username', filters.username);
      if (filters.action) params.append('action', filters.action);
      if (filters.resource) params.append('resource', filters.resource);
      
      const queryString = params.toString();
      if (queryString) url += '?' + queryString;
      
      const response = await api.get(url);
      setLogs(response.data);
    } catch (error) {
      console.error('Failed to fetch audit logs:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFilter = (e) => {
    e.preventDefault();
    fetchLogs();
  };

  const clearFilters = () => {
    setFilters({ username: '', action: '', resource: '' });
    setTimeout(() => fetchLogs(), 100);
  };

  if (loading) return <div className="p-6">Loading...</div>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6">Audit Log Viewer</h1>

      {/* Filters */}
      <div className="bg-white rounded-lg shadow p-4 mb-6">
        <form onSubmit={handleFilter} className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <input
            type="text"
            placeholder="Filter by username"
            value={filters.username}
            onChange={(e) => setFilters({ ...filters, username: e.target.value })}
            className="border rounded px-3 py-2"
          />
          <input
            type="text"
            placeholder="Filter by action"
            value={filters.action}
            onChange={(e) => setFilters({ ...filters, action: e.target.value })}
            className="border rounded px-3 py-2"
          />
          <input
            type="text"
            placeholder="Filter by resource"
            value={filters.resource}
            onChange={(e) => setFilters({ ...filters, resource: e.target.value })}
            className="border rounded px-3 py-2"
          />
          <div className="flex gap-2">
            <button
              type="submit"
              className="flex-1 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            >
              Apply
            </button>
            <button
              type="button"
              onClick={clearFilters}
              className="flex-1 bg-gray-300 text-gray-700 px-4 py-2 rounded hover:bg-gray-400"
            >
              Clear
            </button>
          </div>
        </form>
      </div>

      {/* Logs Table */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full">
            <thead className="bg-gray-100">
              <tr>
                <th className="px-4 py-2 border text-left">Timestamp</th>
                <th className="px-4 py-2 border text-left">User</th>
                <th className="px-4 py-2 border text-left">Action</th>
                <th className="px-4 py-2 border text-left">Resource</th>
                <th className="px-4 py-2 border text-left">Resource ID</th>
                <th className="px-4 py-2 border text-left">Details</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={log.id} className="hover:bg-gray-50">
                  <td className="px-4 py-2 border text-sm">
                    {new Date(log.createdAt).toLocaleString()}
                  </td>
                  <td className="px-4 py-2 border">
                    {log.user?.username || 'System'}
                  </td>
                  <td className="px-4 py-2 border">
                    <span className={`px-2 py-1 rounded text-xs ${
                      log.action.includes('DELETE') ? 'bg-red-100 text-red-800' :
                      log.action.includes('CREATE') ? 'bg-green-100 text-green-800' :
                      log.action.includes('UPDATE') ? 'bg-yellow-100 text-yellow-800' :
                      'bg-blue-100 text-blue-800'
                    }`}>
                      {log.action}
                    </span>
                  </td>
                  <td className="px-4 py-2 border">{log.resourceType}</td>
                  <td className="px-4 py-2 border">{log.resourceId || '-'}</td>
                  <td className="px-4 py-2 border text-sm text-gray-600">
                    {log.details || '-'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {logs.length === 0 && (
          <div className="text-center text-gray-500 py-8">
            No audit logs found
          </div>
        )}
      </div>

      <div className="mt-4 text-sm text-gray-600">
        Total logs: {logs.length}
      </div>
    </div>
  );
};

export default AuditLogViewer;
