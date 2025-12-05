import React, { useState, useEffect } from 'react';
import { 
  FaBalanceScale, FaSave, FaSpinner, FaPlus, FaTrash, FaSync,
  FaExclamationTriangle, FaCheck, FaUndo, FaFileAlt
} from 'react-icons/fa';
import { reportAPI } from '../../services/apiService';

/**
 * UC 2.7 - Professor Supplement Grading Criteria
 * Basic Flow:
 * 1. Professor selects evaluation criteria section
 * 2. Professor defines custom criteria
 * 3. Professor sets weightings for each criterion
 * 4. Professor saves criteria for use in evaluations
 * 5. System applies to future evaluations
 */
const GradingCriteria = ({ refreshTrigger }) => {
  // IEEE 1058 Default Sections with weights
  const defaultCriteria = [
    { id: 1, name: 'Scope', weight: 8, enabled: true, description: 'Project scope and boundaries' },
    { id: 2, name: 'Standards References', weight: 5, enabled: true, description: 'Referenced standards and documents' },
    { id: 3, name: 'Definitions', weight: 5, enabled: true, description: 'Key terms and definitions' },
    { id: 4, name: 'Project Overview', weight: 10, enabled: true, description: 'High-level project description' },
    { id: 5, name: 'Project Organization', weight: 10, enabled: true, description: 'Team structure and roles' },
    { id: 6, name: 'Managerial Process', weight: 15, enabled: true, description: 'Management approach and processes' },
    { id: 7, name: 'Technical Process', weight: 15, enabled: true, description: 'Technical methodology and approach' },
    { id: 8, name: 'Work Packages', weight: 10, enabled: true, description: 'Work breakdown structure' },
    { id: 9, name: 'Schedule', weight: 10, enabled: true, description: 'Project timeline and milestones' },
    { id: 10, name: 'Risk Management', weight: 7, enabled: true, description: 'Risk identification and mitigation' },
    { id: 11, name: 'Closeout Plan', weight: 3, enabled: true, description: 'Project closure activities' },
    { id: 12, name: 'Annexes', weight: 2, enabled: true, description: 'Supporting documents and appendices' },
  ];

  const [criteria, setCriteria] = useState(defaultCriteria);
  const [customCriteria, setCustomCriteria] = useState([]);
  const [saving, setSaving] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [selectedSection, setSelectedSection] = useState(null);

  // Calculate total weight
  const totalWeight = [...criteria, ...customCriteria]
    .filter(c => c.enabled)
    .reduce((sum, c) => sum + c.weight, 0);

  const isValidTotal = totalWeight === 100;

  // Step 1: Professor selects evaluation criteria section
  const handleSelectSection = (criterionId) => {
    setSelectedSection(selectedSection === criterionId ? null : criterionId);
  };

  // Step 2: Professor defines custom criteria
  const handleAddCustomCriteria = () => {
    const newCriteria = {
      id: `custom-${Date.now()}`,
      name: 'New Criterion',
      weight: 0,
      enabled: true,
      description: 'Enter description',
      isCustom: true
    };
    setCustomCriteria([...customCriteria, newCriteria]);
    setSelectedSection(newCriteria.id);
  };

  const handleRemoveCustomCriteria = (criterionId) => {
    setCustomCriteria(customCriteria.filter(c => c.id !== criterionId));
    if (selectedSection === criterionId) {
      setSelectedSection(null);
    }
  };

  // Step 3: Professor sets weightings for each criterion
  const handleWeightChange = (criterionId, newWeight, isCustom = false) => {
    const weight = Math.max(0, Math.min(100, parseInt(newWeight) || 0));
    
    if (isCustom) {
      setCustomCriteria(customCriteria.map(c => 
        c.id === criterionId ? { ...c, weight } : c
      ));
    } else {
      setCriteria(criteria.map(c => 
        c.id === criterionId ? { ...c, weight } : c
      ));
    }
  };

  const handleToggleCriteria = (criterionId, isCustom = false) => {
    if (isCustom) {
      setCustomCriteria(customCriteria.map(c => 
        c.id === criterionId ? { ...c, enabled: !c.enabled } : c
      ));
    } else {
      setCriteria(criteria.map(c => 
        c.id === criterionId ? { ...c, enabled: !c.enabled } : c
      ));
    }
  };

  const handleUpdateCriteria = (criterionId, field, value, isCustom = false) => {
    if (isCustom) {
      setCustomCriteria(customCriteria.map(c => 
        c.id === criterionId ? { ...c, [field]: value } : c
      ));
    } else {
      setCriteria(criteria.map(c => 
        c.id === criterionId ? { ...c, [field]: value } : c
      ));
    }
  };

  // Step 4: Professor saves criteria for use in evaluations
  const handleSaveCriteria = async () => {
    if (!isValidTotal) {
      setError('Total weight must equal 100%');
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      // Save to backend (API call)
      await reportAPI.saveGradingCriteria({
        standardCriteria: criteria,
        customCriteria: customCriteria
      });
      
      setSuccess('Grading criteria saved successfully! Changes will apply to future evaluations.');
      setTimeout(() => setSuccess(null), 5000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save criteria. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  // Reset to defaults
  const handleResetToDefaults = () => {
    setCriteria(defaultCriteria);
    setCustomCriteria([]);
    setSelectedSection(null);
    setSuccess('Reset to default IEEE 1058 weights.');
    setTimeout(() => setSuccess(null), 3000);
  };

  // Load existing criteria
  useEffect(() => {
    const loadCriteria = async () => {
      setLoading(true);
      try {
        const response = await reportAPI.getGradingCriteria();
        if (response.data) {
          if (response.data.standardCriteria) {
            setCriteria(response.data.standardCriteria);
          }
          if (response.data.customCriteria) {
            setCustomCriteria(response.data.customCriteria);
          }
        }
      } catch (err) {
        // Use defaults if no saved criteria
        console.log('Using default criteria');
      } finally {
        setLoading(false);
      }
    };
    loadCriteria();
  }, [refreshTrigger]);

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-8 text-center">
        <FaSpinner className="animate-spin text-purple-600 text-3xl mx-auto mb-3" />
        <p className="text-gray-600">Loading grading criteria...</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2">
            <FaBalanceScale className="text-purple-600" /> Grading Criteria
          </h3>
          <p className="text-sm text-gray-600 mt-1">
            Customize IEEE 1058 section weights for document evaluation
          </p>
        </div>
        <div className="flex gap-2">
          <button
            onClick={handleResetToDefaults}
            className="px-3 py-1.5 text-sm text-gray-600 hover:text-gray-800 flex items-center gap-1"
          >
            <FaUndo /> Reset
          </button>
          <button
            onClick={handleAddCustomCriteria}
            className="px-3 py-1.5 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 flex items-center gap-1 text-sm font-semibold"
          >
            <FaPlus /> Add Criterion
          </button>
        </div>
      </div>

      {/* Weight Summary */}
      <div className={`p-4 rounded-lg mb-6 ${isValidTotal ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'}`}>
        <div className="flex items-center justify-between">
          <span className="font-semibold text-gray-700">Total Weight:</span>
          <span className={`text-2xl font-bold ${isValidTotal ? 'text-green-600' : 'text-red-600'}`}>
            {totalWeight}%
          </span>
        </div>
        {!isValidTotal && (
          <p className="text-sm text-red-600 mt-1">
            Total must equal 100% (currently {totalWeight > 100 ? 'over' : 'under'} by {Math.abs(100 - totalWeight)}%)
          </p>
        )}
      </div>

      {/* Success/Error Messages */}
      {success && (
        <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-lg text-green-700 flex items-center gap-2">
          <FaCheck /> {success}
        </div>
      )}
      {error && (
        <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 flex items-center gap-2">
          <FaExclamationTriangle /> {error}
        </div>
      )}

      {/* IEEE 1058 Standard Criteria */}
      <div className="mb-6">
        <h4 className="font-semibold text-gray-700 mb-3 flex items-center gap-2">
          <FaFileAlt className="text-purple-500" /> IEEE 1058 Sections
        </h4>
        <div className="space-y-2">
          {criteria.map((criterion) => (
            <div
              key={criterion.id}
              className={`border rounded-lg overflow-hidden transition ${
                selectedSection === criterion.id ? 'border-purple-400 bg-purple-50' : 'border-gray-200'
              }`}
            >
              <div 
                className="flex items-center gap-3 p-3 cursor-pointer hover:bg-gray-50"
                onClick={() => handleSelectSection(criterion.id)}
              >
                {/* Enable/Disable Toggle */}
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleToggleCriteria(criterion.id);
                  }}
                  className={`w-5 h-5 rounded border-2 flex items-center justify-center transition ${
                    criterion.enabled 
                      ? 'bg-purple-600 border-purple-600 text-white' 
                      : 'border-gray-300'
                  }`}
                >
                  {criterion.enabled && <FaCheck className="text-xs" />}
                </button>

                {/* Section Name */}
                <span className={`flex-1 font-medium ${criterion.enabled ? 'text-gray-900' : 'text-gray-400'}`}>
                  {criterion.id}. {criterion.name}
                </span>

                {/* Weight Input */}
                <div className="flex items-center gap-1">
                  <input
                    type="number"
                    min="0"
                    max="100"
                    value={criterion.weight}
                    onChange={(e) => handleWeightChange(criterion.id, e.target.value)}
                    onClick={(e) => e.stopPropagation()}
                    disabled={!criterion.enabled}
                    className={`w-16 px-2 py-1 text-center border rounded ${
                      criterion.enabled 
                        ? 'border-gray-300 focus:border-purple-500' 
                        : 'bg-gray-100 text-gray-400'
                    }`}
                  />
                  <span className="text-gray-500">%</span>
                </div>

                {/* Progress Bar */}
                <div className="w-24 h-2 bg-gray-200 rounded-full overflow-hidden">
                  <div
                    className={`h-full ${criterion.enabled ? 'bg-purple-500' : 'bg-gray-300'}`}
                    style={{ width: `${criterion.weight}%` }}
                  />
                </div>
              </div>

              {/* Expanded Description */}
              {selectedSection === criterion.id && (
                <div className="p-3 bg-gray-50 border-t text-sm text-gray-600">
                  {criterion.description}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Custom Criteria */}
      {customCriteria.length > 0 && (
        <div className="mb-6">
          <h4 className="font-semibold text-gray-700 mb-3 flex items-center gap-2">
            <FaPlus className="text-blue-500" /> Custom Criteria
          </h4>
          <div className="space-y-2">
            {customCriteria.map((criterion) => (
              <div
                key={criterion.id}
                className={`border rounded-lg overflow-hidden transition ${
                  selectedSection === criterion.id ? 'border-blue-400 bg-blue-50' : 'border-gray-200'
                }`}
              >
                <div className="flex items-center gap-3 p-3">
                  {/* Enable/Disable Toggle */}
                  <button
                    onClick={() => handleToggleCriteria(criterion.id, true)}
                    className={`w-5 h-5 rounded border-2 flex items-center justify-center transition ${
                      criterion.enabled 
                        ? 'bg-blue-600 border-blue-600 text-white' 
                        : 'border-gray-300'
                    }`}
                  >
                    {criterion.enabled && <FaCheck className="text-xs" />}
                  </button>

                  {/* Editable Name */}
                  <input
                    type="text"
                    value={criterion.name}
                    onChange={(e) => handleUpdateCriteria(criterion.id, 'name', e.target.value, true)}
                    className="flex-1 px-2 py-1 border border-gray-300 rounded focus:border-blue-500"
                    placeholder="Criterion name"
                  />

                  {/* Weight Input */}
                  <div className="flex items-center gap-1">
                    <input
                      type="number"
                      min="0"
                      max="100"
                      value={criterion.weight}
                      onChange={(e) => handleWeightChange(criterion.id, e.target.value, true)}
                      disabled={!criterion.enabled}
                      className={`w-16 px-2 py-1 text-center border rounded ${
                        criterion.enabled 
                          ? 'border-gray-300 focus:border-blue-500' 
                          : 'bg-gray-100 text-gray-400'
                      }`}
                    />
                    <span className="text-gray-500">%</span>
                  </div>

                  {/* Delete Button */}
                  <button
                    onClick={() => handleRemoveCustomCriteria(criterion.id)}
                    className="p-1.5 text-red-500 hover:bg-red-50 rounded"
                  >
                    <FaTrash />
                  </button>
                </div>

                {/* Description Input */}
                <div className="px-3 pb-3">
                  <input
                    type="text"
                    value={criterion.description}
                    onChange={(e) => handleUpdateCriteria(criterion.id, 'description', e.target.value, true)}
                    className="w-full px-2 py-1 text-sm border border-gray-300 rounded focus:border-blue-500"
                    placeholder="Description of this criterion"
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Save Button */}
      <button
        onClick={handleSaveCriteria}
        disabled={!isValidTotal || saving}
        className="w-full py-3 bg-purple-600 hover:bg-purple-700 disabled:bg-gray-300 disabled:cursor-not-allowed text-white font-semibold rounded-lg transition flex items-center justify-center gap-2"
      >
        {saving ? (
          <>
            <FaSpinner className="animate-spin" />
            Saving...
          </>
        ) : (
          <>
            <FaSave />
            Save Grading Criteria
          </>
        )}
      </button>

      {/* Info Note */}
      <p className="text-xs text-gray-500 text-center mt-3">
        Changes will be applied to all future document evaluations.
      </p>
    </div>
  );
};

export default GradingCriteria;
