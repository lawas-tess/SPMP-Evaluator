import React, { useState, useEffect } from 'react';
import api from '../../services/apiService';

/**
 * UC 2.15: Admin System Settings
 * Configure system-wide settings (maintenance, registration, features)
 */
const SystemSettingsForm = () => {
  const [settings, setSettings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });
  const [editedSettings, setEditedSettings] = useState({});

  useEffect(() => {
    fetchSettings();
  }, []);

  const fetchSettings = async () => {
    try {
      setLoading(true);
      const response = await api.get('/admin/settings');
      setSettings(response.data);
      
      // Define default settings
      const defaultSettings = {
        'MAINTENANCE_MODE': 'false',
        'ALLOW_STUDENT_REGISTRATION': 'true',
        'ALLOW_PROFESSOR_REGISTRATION': 'true',
        'MAX_FILE_SIZE_MB': '50',
        'SESSION_TIMEOUT_MINUTES': '60',
        'SYSTEM_ANNOUNCEMENT': ''
      };
      
      // Initialize edited settings with defaults first, then override with actual settings
      const initialEdits = { ...defaultSettings };
      response.data.forEach(setting => {
        initialEdits[setting.key] = setting.value;
      });
      setEditedSettings(initialEdits);
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to load settings' });
    } finally {
      setLoading(false);
    }
  };

  const handleSettingChange = (key, value) => {
    setEditedSettings(prev => ({
      ...prev,
      [key]: value
    }));
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      const updates = Object.entries(editedSettings).map(([key, value]) => ({
        key,
        value
      }));

      await api.post('/admin/settings', { settings: updates });
      setMessage({ type: 'success', text: 'Settings saved successfully' });
      fetchSettings();
    } catch (error) {
      setMessage({ type: 'error', text: error.response?.data?.message || 'Failed to save settings' });
    } finally {
      setSaving(false);
    }
  };

  const handleReset = () => {
    const initialEdits = {};
    settings.forEach(setting => {
      initialEdits[setting.key] = setting.value;
    });
    setEditedSettings(initialEdits);
    setMessage({ type: '', text: '' });
  };

  const renderSettingInput = (setting) => {
    const value = editedSettings[setting.key] || '';

    if (setting.type === 'BOOLEAN') {
      return (
        <label className="flex items-center cursor-pointer">
          <input
            type="checkbox"
            checked={value === 'true'}
            onChange={(e) => handleSettingChange(setting.key, e.target.checked ? 'true' : 'false')}
            className="mr-2"
          />
          <span className="text-sm">Enable</span>
        </label>
      );
    }

    if (setting.type === 'NUMBER') {
      return (
        <input
          type="number"
          value={value}
          onChange={(e) => handleSettingChange(setting.key, e.target.value)}
          className="w-full p-2 border rounded"
        />
      );
    }

    if (setting.type === 'TEXT' && setting.description?.includes('large text')) {
      return (
        <textarea
          value={value}
          onChange={(e) => handleSettingChange(setting.key, e.target.value)}
          rows={4}
          className="w-full p-2 border rounded"
        />
      );
    }

    return (
      <input
        type="text"
        value={value}
        onChange={(e) => handleSettingChange(setting.key, e.target.value)}
        className="w-full p-2 border rounded"
      />
    );
  };

  const groupSettingsByCategory = () => {
    // Define all default settings with metadata
    const allSettings = [
      { key: 'MAINTENANCE_MODE', value: editedSettings['MAINTENANCE_MODE'] || 'false', category: 'SYSTEM', type: 'BOOLEAN', description: 'Prevents non-admin users from accessing the system', requiresRestart: false },
      { key: 'ALLOW_STUDENT_REGISTRATION', value: editedSettings['ALLOW_STUDENT_REGISTRATION'] || 'true', category: 'REGISTRATION', type: 'BOOLEAN', description: 'Allow new student registrations', requiresRestart: false },
      { key: 'ALLOW_PROFESSOR_REGISTRATION', value: editedSettings['ALLOW_PROFESSOR_REGISTRATION'] || 'true', category: 'REGISTRATION', type: 'BOOLEAN', description: 'Allow new professor registrations', requiresRestart: false },
      { key: 'MAX_FILE_SIZE_MB', value: editedSettings['MAX_FILE_SIZE_MB'] || '50', category: 'FILE_UPLOAD', type: 'NUMBER', description: 'Maximum allowed upload file size in MB', requiresRestart: false },
      { key: 'SESSION_TIMEOUT_MINUTES', value: editedSettings['SESSION_TIMEOUT_MINUTES'] || '60', category: 'SECURITY', type: 'NUMBER', description: 'Auto-logout after specified minutes of inactivity', requiresRestart: true },
      { key: 'SYSTEM_ANNOUNCEMENT', value: editedSettings['SYSTEM_ANNOUNCEMENT'] || '', category: 'SYSTEM', type: 'TEXT', description: 'Display announcement message to all users', requiresRestart: false }
    ];

    // Merge with existing settings from backend
    const mergedSettings = [...allSettings];
    settings.forEach(setting => {
      const existingIndex = mergedSettings.findIndex(s => s.key === setting.key);
      if (existingIndex >= 0) {
        mergedSettings[existingIndex] = { ...mergedSettings[existingIndex], ...setting };
      } else {
        mergedSettings.push(setting);
      }
    });

    const grouped = {};
    mergedSettings.forEach(setting => {
      const category = setting.category || 'General';
      if (!grouped[category]) {
        grouped[category] = [];
      }
      grouped[category].push(setting);
    });
    return grouped;
  };

  if (loading) {
    return <div className="p-6">Loading settings...</div>;
  }

  const groupedSettings = groupSettingsByCategory();

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold">System Settings</h2>
        <div className="flex gap-2">
          <button
            onClick={handleReset}
            className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
            disabled={saving}
          >
            Reset
          </button>
          <button
            onClick={handleSave}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            disabled={saving}
          >
            {saving ? 'Saving...' : 'Save Changes'}
          </button>
        </div>
      </div>

      {message.text && (
        <div className={`mb-4 p-4 rounded ${
          message.type === 'error' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'
        }`}>
          {message.text}
        </div>
      )}

      {/* Settings by Category */}
      {Object.entries(groupedSettings).map(([category, categorySettings]) => (
        <div key={category} className="bg-white rounded-lg shadow mb-6">
          <div className="bg-gray-50 px-6 py-3 border-b">
            <h3 className="text-lg font-semibold">{category}</h3>
          </div>
          <div className="p-6">
            <div className="space-y-6">
              {categorySettings.map(setting => (
                <div key={setting.key} className="border-b pb-4 last:border-b-0">
                  <div className="flex justify-between items-start mb-2">
                    <div className="flex-1">
                      <label className="block font-medium text-gray-700 mb-1">
                        {setting.key.split('_').map(word => 
                          word.charAt(0) + word.slice(1).toLowerCase()
                        ).join(' ')}
                      </label>
                      {setting.description && (
                        <p className="text-sm text-gray-500 mb-2">{setting.description}</p>
                      )}
                    </div>
                    <span className="text-xs text-gray-400 ml-4">{setting.type}</span>
                  </div>
                  <div className="mt-2">
                    {renderSettingInput(setting)}
                  </div>
                  {setting.requiresRestart && (
                    <p className="text-xs text-orange-600 mt-1">⚠️ Requires application restart</p>
                  )}
                </div>
              ))}
            </div>
          </div>
        </div>
      ))}

      {/* Predefined Settings if none exist */}
      {settings.length === 0 && (
        <div className="bg-white rounded-lg shadow p-6">
          <p className="text-gray-500 mb-4">No settings configured yet. Here are some default settings:</p>
          <div className="space-y-4">
            <div className="border-b pb-4">
              <label className="block font-medium mb-2">Maintenance Mode</label>
              <label className="flex items-center cursor-pointer">
                <input
                  type="checkbox"
                  checked={editedSettings['MAINTENANCE_MODE'] === 'true'}
                  onChange={(e) => handleSettingChange('MAINTENANCE_MODE', e.target.checked ? 'true' : 'false')}
                  className="mr-2"
                />
                <span className="text-sm">Enable maintenance mode</span>
              </label>
              <p className="text-sm text-gray-500 mt-1">Prevents non-admin users from accessing the system</p>
            </div>

            <div className="border-b pb-4">
              <label className="block font-medium mb-2">Student Registration</label>
              <label className="flex items-center cursor-pointer">
                <input
                  type="checkbox"
                  checked={editedSettings['ALLOW_STUDENT_REGISTRATION'] === 'true'}
                  onChange={(e) => handleSettingChange('ALLOW_STUDENT_REGISTRATION', e.target.checked ? 'true' : 'false')}
                  className="mr-2"
                />
                <span className="text-sm">Allow new student registrations</span>
              </label>
            </div>

            <div className="border-b pb-4">
              <label className="block font-medium mb-2">Professor Registration</label>
              <label className="flex items-center cursor-pointer">
                <input
                  type="checkbox"
                  checked={editedSettings['ALLOW_PROFESSOR_REGISTRATION'] === 'true'}
                  onChange={(e) => handleSettingChange('ALLOW_PROFESSOR_REGISTRATION', e.target.checked ? 'true' : 'false')}
                  className="mr-2"
                />
                <span className="text-sm">Allow new professor registrations</span>
              </label>
            </div>

            <div className="border-b pb-4">
              <label className="block font-medium mb-2">Maximum File Size (MB)</label>
              <input
                type="number"
                value={editedSettings['MAX_FILE_SIZE_MB'] || '50'}
                onChange={(e) => handleSettingChange('MAX_FILE_SIZE_MB', e.target.value)}
                className="w-full p-2 border rounded"
                min="1"
                max="100"
              />
              <p className="text-sm text-gray-500 mt-1">Maximum allowed upload file size in MB</p>
            </div>

            <div className="border-b pb-4">
              <label className="block font-medium mb-2">Session Timeout (minutes)</label>
              <input
                type="number"
                value={editedSettings['SESSION_TIMEOUT_MINUTES'] || '60'}
                onChange={(e) => handleSettingChange('SESSION_TIMEOUT_MINUTES', e.target.value)}
                className="w-full p-2 border rounded"
                min="5"
                max="1440"
              />
              <p className="text-sm text-gray-500 mt-1">Auto-logout after specified minutes of inactivity</p>
            </div>

            <div>
              <label className="block font-medium mb-2">System Announcement</label>
              <textarea
                value={editedSettings['SYSTEM_ANNOUNCEMENT'] || ''}
                onChange={(e) => handleSettingChange('SYSTEM_ANNOUNCEMENT', e.target.value)}
                rows={3}
                placeholder="Enter announcement message (leave blank for none)"
                className="w-full p-2 border rounded"
              />
              <p className="text-sm text-gray-500 mt-1">Displayed to all users on login</p>
            </div>
          </div>
        </div>
      )}

      {/* Warning Notice */}
      <div className="bg-yellow-50 border border-yellow-200 rounded p-4 mt-6">
        <p className="text-sm text-yellow-800">
          <strong>⚠️ Warning:</strong> Changing system settings may affect all users. 
          Some settings require application restart to take effect. 
          Please ensure you understand the impact before making changes.
        </p>
      </div>
    </div>
  );
};

export default SystemSettingsForm;
