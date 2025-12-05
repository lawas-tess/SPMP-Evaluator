import React, { useState, useRef } from 'react';
import { FaTimes, FaFileUpload, FaFilePdf, FaFileWord, FaSpinner, FaCheck, FaExclamationTriangle } from 'react-icons/fa';
import { documentAPI } from '../../services/apiService';

/**
 * UC 2.2 - Student File Edit/Replace Modal
 * Basic Flow:
 * 1. Student views list of uploaded files (DocumentList.jsx)
 * 2. Student selects a file to edit (clicks Replace button)
 * 3. Student uploads replacement file (this modal)
 * 4. System validates new file
 * 5. Student confirms changes
 * 6. System updates file and logs change
 */
const FileReplaceModal = ({ document, onClose, onSuccess }) => {
  const [file, setFile] = useState(null);
  const [dragOver, setDragOver] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState(null);
  const [validationStatus, setValidationStatus] = useState(null); // 'valid', 'invalid', null
  const fileInputRef = useRef(null);

  const allowedTypes = [
    'application/pdf',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
  ];

  // Step 4: System validates new file
  const validateFile = (selectedFile) => {
    setError(null);
    setValidationStatus(null);

    if (!allowedTypes.includes(selectedFile.type)) {
      setError('Invalid file type. Please upload PDF or DOCX files only.');
      setValidationStatus('invalid');
      return false;
    }
    if (selectedFile.size > 50 * 1024 * 1024) {
      setError('File too large. Maximum size is 50MB. Consider compressing the file.');
      setValidationStatus('invalid');
      return false;
    }
    
    setValidationStatus('valid');
    return true;
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    setDragOver(true);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    setDragOver(false);
  };

  // Step 3: Student uploads replacement file
  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    const droppedFile = e.dataTransfer.files[0];
    if (droppedFile) {
      setFile(droppedFile);
      validateFile(droppedFile);
    }
  };

  const handleFileSelect = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      validateFile(selectedFile);
    }
  };

  // Step 5 & 6: Student confirms changes, System updates file
  const handleConfirmReplace = async () => {
    if (!file || validationStatus !== 'valid') return;

    setUploading(true);
    setError(null);

    try {
      await documentAPI.replace(document.id, file);
      if (onSuccess) {
        onSuccess();
      }
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to replace file. Original file retained.');
    } finally {
      setUploading(false);
    }
  };

  const getFileIcon = (fileObj) => {
    if (!fileObj) return null;
    if (fileObj.type === 'application/pdf' || fileObj.name?.toLowerCase().endsWith('.pdf')) {
      return <FaFilePdf className="text-red-500 text-3xl" />;
    }
    return <FaFileWord className="text-blue-500 text-3xl" />;
  };

  const formatFileSize = (bytes) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-lg w-full mx-4">
        {/* Header */}
        <div className="flex items-center justify-between p-4 border-b">
          <h3 className="text-lg font-bold text-gray-900">Replace Document</h3>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600"
          >
            <FaTimes />
          </button>
        </div>

        {/* Content */}
        <div className="p-4">
          {/* Current Document Info */}
          <div className="mb-4 p-3 bg-gray-50 rounded-lg">
            <p className="text-sm text-gray-600 mb-1">Current Document:</p>
            <div className="flex items-center gap-2">
              {getFileIcon({ name: document?.fileName })}
              <span className="font-semibold text-gray-900 truncate">{document?.fileName}</span>
            </div>
          </div>

          {/* New File Upload Zone */}
          <div className="mb-4">
            <p className="text-sm font-semibold text-gray-700 mb-2">Select Replacement File:</p>
            <div
              onDragOver={handleDragOver}
              onDragLeave={handleDragLeave}
              onDrop={handleDrop}
              onClick={() => fileInputRef.current?.click()}
              className={`border-2 border-dashed rounded-lg p-6 text-center cursor-pointer transition-all ${
                dragOver
                  ? 'border-purple-500 bg-purple-50'
                  : validationStatus === 'valid'
                  ? 'border-green-400 bg-green-50'
                  : validationStatus === 'invalid'
                  ? 'border-red-400 bg-red-50'
                  : 'border-gray-300 hover:border-purple-400'
              }`}
            >
              <input
                ref={fileInputRef}
                type="file"
                accept=".pdf,.docx"
                onChange={handleFileSelect}
                className="hidden"
              />

              {file ? (
                <div className="flex flex-col items-center gap-2">
                  {getFileIcon(file)}
                  <p className="font-semibold text-gray-900">{file.name}</p>
                  <p className="text-sm text-gray-500">{formatFileSize(file.size)}</p>
                  {validationStatus === 'valid' && (
                    <span className="text-green-600 flex items-center gap-1 text-sm">
                      <FaCheck /> File validated successfully
                    </span>
                  )}
                </div>
              ) : (
                <div className="flex flex-col items-center gap-2">
                  <FaFileUpload className="text-gray-400 text-4xl" />
                  <p className="font-semibold text-gray-700">Drop new file here or click to browse</p>
                  <p className="text-sm text-gray-500">PDF or DOCX, max 50MB</p>
                </div>
              )}
            </div>
          </div>

          {/* Validation Error */}
          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm flex items-start gap-2">
              <FaExclamationTriangle className="mt-0.5 flex-shrink-0" />
              <div>
                <p className="font-semibold">Validation Failed</p>
                <p>{error}</p>
              </div>
            </div>
          )}

          {/* Warning Notice */}
          <div className="mb-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg text-yellow-800 text-sm flex items-start gap-2">
            <FaExclamationTriangle className="mt-0.5 flex-shrink-0" />
            <div>
              <p className="font-semibold">Confirm Replacement</p>
              <p>The original file will be replaced and queued for re-evaluation. This action will be logged.</p>
            </div>
          </div>

          {/* Action Buttons - Step 5: Confirm changes */}
          <div className="flex gap-3">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 rounded-lg text-gray-700 font-semibold hover:bg-gray-50 transition"
            >
              Cancel
            </button>
            <button
              onClick={handleConfirmReplace}
              disabled={!file || validationStatus !== 'valid' || uploading}
              className="flex-1 px-4 py-2 bg-purple-600 hover:bg-purple-700 disabled:bg-gray-300 disabled:cursor-not-allowed text-white font-semibold rounded-lg transition flex items-center justify-center gap-2"
            >
              {uploading ? (
                <>
                  <FaSpinner className="animate-spin" />
                  Replacing...
                </>
              ) : (
                <>
                  <FaCheck />
                  Confirm Replace
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FileReplaceModal;
