import React, { useState, useEffect } from 'react';
import api from '../../services/apiService';

const ParserConfiguration = () => {
  const [configurations, setConfigurations] = useState([]);
  const [selectedConfig, setSelectedConfig] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [isCreating, setIsCreating] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    clauseMappings: '',
    customRules: '',
    isActive: true,
    isDefault: false
  });

  useEffect(() => {
    loadConfigurations();
  }, []);

  const loadConfigurations = async () => {
    try {
      setLoading(true);
      const response = await api.get('/parser/config');
      setConfigurations(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to load parser configurations');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateNew = () => {
    setFormData({
      name: '',
      description: '',
      clauseMappings: '',
      customRules: '',
      isActive: true,
      isDefault: false
    });
    setIsCreating(true);
    setIsEditing(false);
    setSelectedConfig(null);
  };

  const handleEdit = (config) => {
    setFormData({
      name: config.name,
      description: config.description || '',
      clauseMappings: config.clauseMappings || '',
      customRules: config.customRules || '',
      isActive: config.isActive,
      isDefault: config.isDefault
    });
    setSelectedConfig(config);
    setIsEditing(true);
    setIsCreating(false);
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      
      if (isEditing && selectedConfig) {
        await api.put(`/parser/config/${selectedConfig.id}`, formData);
      } else {
        await api.post('/parser/config', formData);
      }
      
      await loadConfigurations();
      setIsEditing(false);
      setIsCreating(false);
      setSelectedConfig(null);
      setError(null);
    } catch (err) {
      setError('Failed to save configuration: ' + (err.response?.data || err.message));
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSetDefault = async (configId) => {
    try {
      setLoading(true);
      await api.put(`/parser/config/${configId}/set-default`);
      await loadConfigurations();
      setError(null);
    } catch (err) {
      setError('Failed to set as default');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (configId) => {
    if (!window.confirm('Are you sure you want to delete this configuration?')) {
      return;
    }
    
    try {
      setLoading(true);
      await api.delete(`/parser/config/${configId}`);
      await loadConfigurations();
      setError(null);
    } catch (err) {
      setError('Failed to delete configuration: ' + (err.response?.data || err.message));
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateDefault = async () => {
    try {
      setLoading(true);
      await api.post('/parser/config/create-default');
      await loadConfigurations();
      setError(null);
    } catch (err) {
      setError('Failed to create default configuration');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    setIsCreating(false);
    setSelectedConfig(null);
    setFormData({
      name: '',
      description: '',
      clauseMappings: '',
      customRules: '',
      isActive: true,
      isDefault: false
    });
  };

  if (loading && configurations.length === 0) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-gray-600">Loading configurations...</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-800">Parser Configuration</h2>
        <div className="space-x-2">
          {configurations.length === 0 && (
            <button
              onClick={handleCreateDefault}
              className="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded"
              disabled={loading}
            >
              Create IEEE 1058 Default
            </button>
          )}
          <button
            onClick={handleCreateNew}
            className="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded"
            disabled={loading}
          >
            + New Configuration
          </button>
        </div>
      </div>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {(isCreating || isEditing) && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-xl font-semibold mb-4">
            {isEditing ? 'Edit Configuration' : 'Create New Configuration'}
          </h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Configuration Name *
              </label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                className="w-full border border-gray-300 rounded px-3 py-2"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Description
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                rows={3}
                className="w-full border border-gray-300 rounded px-3 py-2"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Clause Mappings (JSON)
              </label>
              <textarea
                name="clauseMappings"
                value={formData.clauseMappings}
                onChange={handleInputChange}
                rows={10}
                className="w-full border border-gray-300 rounded px-3 py-2 font-mono text-sm"
                placeholder='[{"clauseId": "1", "clauseName": "Overview", "weight": 10, "keywords": ["overview"]}]'
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Custom Rules (JSON)
              </label>
              <textarea
                name="customRules"
                value={formData.customRules}
                onChange={handleInputChange}
                rows={6}
                className="w-full border border-gray-300 rounded px-3 py-2 font-mono text-sm"
                placeholder='[{"ruleId": "R1", "description": "Check completeness", "criteria": "...", "severity": "high"}]'
              />
            </div>

            <div className="flex items-center space-x-6">
              <label className="flex items-center">
                <input
                  type="checkbox"
                  name="isActive"
                  checked={formData.isActive}
                  onChange={handleInputChange}
                  className="mr-2"
                />
                <span className="text-sm font-medium text-gray-700">Active</span>
              </label>

              <label className="flex items-center">
                <input
                  type="checkbox"
                  name="isDefault"
                  checked={formData.isDefault}
                  onChange={handleInputChange}
                  className="mr-2"
                />
                <span className="text-sm font-medium text-gray-700">Set as Default</span>
              </label>
            </div>

            <div className="flex space-x-2">
              <button
                type="submit"
                className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-2 rounded"
                disabled={loading}
              >
                {loading ? 'Saving...' : 'Save Configuration'}
              </button>
              <button
                type="button"
                onClick={handleCancel}
                className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-6 py-2 rounded"
                disabled={loading}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Description</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Created</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {configurations.map(config => (
              <tr key={config.id}>
                <td className="px-6 py-4 whitespace-nowrap">
                  <div className="font-medium text-gray-900">{config.name}</div>
                  {config.isDefault && (
                    <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-800">
                      Default
                    </span>
                  )}
                </td>
                <td className="px-6 py-4">
                  <div className="text-sm text-gray-500 max-w-xs truncate">
                    {config.description || 'No description'}
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${
                    config.isActive 
                      ? 'bg-green-100 text-green-800' 
                      : 'bg-gray-100 text-gray-800'
                  }`}>
                    {config.isActive ? 'Active' : 'Inactive'}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {new Date(config.createdAt).toLocaleDateString()}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                  <button
                    onClick={() => handleEdit(config)}
                    className="text-blue-600 hover:text-blue-900"
                    disabled={loading}
                  >
                    Edit
                  </button>
                  {!config.isDefault && (
                    <>
                      <button
                        onClick={() => handleSetDefault(config.id)}
                        className="text-green-600 hover:text-green-900"
                        disabled={loading}
                      >
                        Set Default
                      </button>
                      <button
                        onClick={() => handleDelete(config.id)}
                        className="text-red-600 hover:text-red-900"
                        disabled={loading}
                      >
                        Delete
                      </button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        
        {configurations.length === 0 && (
          <div className="text-center py-12">
            <p className="text-gray-500">No parser configurations found.</p>
            <p className="text-sm text-gray-400 mt-2">
              Create a default IEEE 1058 configuration to get started.
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ParserConfiguration;
