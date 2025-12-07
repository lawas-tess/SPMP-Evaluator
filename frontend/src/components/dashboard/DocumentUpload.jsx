import React, { useState, useRef } from 'react';
import { FaFileUpload, FaFilePdf, FaFileWord, FaSpinner, FaCheck, FaTimes } from 'react-icons/fa';
import { documentAPI } from '../../services/apiService';

const DocumentUpload = ({ onUploadSuccess }) => {
  const [dragOver, setDragOver] = useState(false);
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [uploadStatus, setUploadStatus] = useState(null); // 'success', 'error', null
  const [errorMessage, setErrorMessage] = useState('');
  const fileInputRef = useRef(null);

  const allowedTypes = [
    'application/pdf',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
  ];

  const validateFile = (file) => {
    if (!allowedTypes.includes(file.type)) {
      setErrorMessage('Invalid file type. Please upload PDF or DOCX files only.');
      return false;
    }
    if (file.size > 50 * 1024 * 1024) { // 50MB
      setErrorMessage('File too large. Maximum size is 50MB.');
      return false;
    }
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

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    const droppedFile = e.dataTransfer.files[0];
    if (droppedFile && validateFile(droppedFile)) {
      setFile(droppedFile);
      setUploadStatus(null);
      setErrorMessage('');
    }
  };

  const handleFileSelect = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile && validateFile(selectedFile)) {
      setFile(selectedFile);
      setUploadStatus(null);
      setErrorMessage('');
    }
  };

  const handleUpload = async () => {
    if (!file) return;

    setUploading(true);
    setUploadStatus(null);
    setErrorMessage('');

    try {
      const response = await documentAPI.upload(file);
      setUploadStatus('success');
      setFile(null);
      if (onUploadSuccess) {
        onUploadSuccess(response.data);
      }
    } catch (error) {
      setUploadStatus('error');
      setErrorMessage(error.response?.data?.message || 'Upload failed. Please try again.');
    } finally {
      setUploading(false);
    }
  };

  const getFileIcon = () => {
    if (!file) return null;
    if (file.type === 'application/pdf') {
      return <FaFilePdf className="text-red-500 text-4xl" />;
    }
    return <FaFileWord className="text-blue-500 text-4xl" />;
  };

  const formatFileSize = (bytes) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  };

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      <h3 className="text-xl font-bold text-gray-900 mb-4">Upload SPMP Document</h3>
      <p className="text-gray-600 mb-6">
        Upload your Software Project Management Plan for IEEE 1058 compliance evaluation
      </p>

      {/* Drag & Drop Zone */}
      <div
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        onClick={() => fileInputRef.current?.click()}
        className={`border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition-all ${
          dragOver
            ? 'border-purple-500 bg-purple-50'
            : 'border-gray-300 hover:border-purple-400 hover:bg-gray-50'
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
          <div className="flex flex-col items-center gap-3">
            {getFileIcon()}
            <div>
              <p className="font-semibold text-gray-900">{file.name}</p>
              <p className="text-sm text-gray-500">{formatFileSize(file.size)}</p>
            </div>
            <button
              onClick={(e) => {
                e.stopPropagation();
                setFile(null);
                setUploadStatus(null);
              }}
              className="text-red-500 hover:text-red-700 text-sm flex items-center gap-1"
            >
              <FaTimes /> Remove
            </button>
          </div>
        ) : (
          <div className="flex flex-col items-center gap-3">
            <FaFileUpload className="text-gray-400 text-5xl" />
            <div>
              <p className="font-semibold text-gray-700">Drop your file here or click to browse</p>
              <p className="text-sm text-gray-500 mt-1">Supports PDF and DOCX files up to 50MB</p>
            </div>
          </div>
        )}
      </div>

      {/* Error Message */}
      {errorMessage && (
        <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm flex items-center gap-2">
          <FaTimes className="text-red-500" />
          {errorMessage}
        </div>
      )}

      {/* Success Message */}
      {uploadStatus === 'success' && (
        <div className="mt-4 p-3 bg-green-50 border border-green-200 rounded-lg text-green-700 text-sm flex items-center gap-2">
          <FaCheck className="text-green-500" />
          Document uploaded. Go to "My Documents" and click "Evaluate" to run the analysis.
        </div>
      )}

      {/* Upload Button */}
      {file && (
        <button
          onClick={handleUpload}
          disabled={uploading}
          className="mt-4 w-full bg-purple-600 hover:bg-purple-700 disabled:bg-purple-400 text-white font-semibold py-3 px-6 rounded-lg transition flex items-center justify-center gap-2"
        >
          {uploading ? (
            <>
              <FaSpinner className="animate-spin" />
              Uploading...
            </>
          ) : (
            <>
              <FaFileUpload />
              Upload Document
            </>
          )}
        </button>
      )}
    </div>
  );
};

export default DocumentUpload;
