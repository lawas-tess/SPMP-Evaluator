import React, { useState, useCallback } from 'react';
import { useAuth } from '../context/AuthContext.jsx';
import Navbar from '../components/Navbar.jsx';
import { 
  FaFileUpload, FaChartBar, FaTasks, FaUsers, FaEye,
  FaHome, FaClipboardList, FaUserGraduate, FaCog, FaListAlt,
  FaUserShield, FaHistory, FaCogs
} from 'react-icons/fa';

// Import Dashboard Components
import {
  DocumentUpload,
  DocumentList,
  EvaluationResults,
  TaskTracker,
  SubmissionTracker,
  TaskManager,
  ScoreOverride,
  StudentProgress,
  GradingCriteria,
  StudentList,
  FileReplaceModal,
  ParserConfiguration,
  ParserFeedback,
  AdminDashboard,
  UserManagement,
  StudentAssignment,
  AuditLogViewer
} from '../components/dashboard';

const Dashboard = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('overview');
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  
  // Modal states
  const [selectedDocument, setSelectedDocument] = useState(null);
  const [showReport, setShowReport] = useState(false);
  const [showScoreOverride, setShowScoreOverride] = useState(false);
  const [showStudentProgress, setShowStudentProgress] = useState(false);
  const [selectedStudentId, setSelectedStudentId] = useState(null);
  const [showFileReplace, setShowFileReplace] = useState(false);

  const isStudent = user?.role === 'STUDENT';
  const isProfessor = user?.role === 'PROFESSOR';
  const isAdmin = user?.role === 'ADMIN';

  const triggerRefresh = useCallback(() => {
    setRefreshTrigger(prev => prev + 1);
  }, []);

  // Student Navigation Tabs
  const studentTabs = [
    { id: 'overview', label: 'Overview', icon: FaHome },
    { id: 'upload', label: 'Upload Document', icon: FaFileUpload },
    { id: 'documents', label: 'My Documents', icon: FaClipboardList },
    { id: 'tasks', label: 'My Tasks', icon: FaTasks },
  ];

  // Professor Navigation Tabs
  const professorTabs = [
    { id: 'overview', label: 'Overview', icon: FaHome },
    { id: 'submissions', label: 'Submissions', icon: FaClipboardList },
    { id: 'tasks', label: 'Task Manager', icon: FaTasks },
    { id: 'students', label: 'Student List', icon: FaListAlt },
    { id: 'progress', label: 'Student Progress', icon: FaUserGraduate },
    { id: 'grading', label: 'Grading Criteria', icon: FaCog },
    { id: 'parser', label: 'Parser Configuration', icon: FaCog },
  ];

  // Admin Navigation Tabs
  const adminTabs = [
    { id: 'overview', label: 'Overview', icon: FaHome },
    { id: 'users', label: 'User Management', icon: FaUserShield },
    { id: 'assignments', label: 'Student Assignment', icon: FaUsers },
    { id: 'audit', label: 'Audit Logs', icon: FaHistory },
    { id: 'settings', label: 'System Settings', icon: FaCogs },
  ];

  const tabs = isAdmin ? adminTabs : (isProfessor ? professorTabs : studentTabs);

  // Handler for viewing evaluation report
  const handleViewReport = (document) => {
    setSelectedDocument(document);
    setShowReport(true);
  };

  // Handler for replacing document (UC 2.2)
  const handleReplaceDocument = (document) => {
    setSelectedDocument(document);
    setShowFileReplace(true);
  };

  // Handler for score override
  const handleOverrideScore = (document) => {
    setSelectedDocument(document);
    setShowScoreOverride(true);
  };

  // Handler for viewing student progress
  const handleViewStudentProgress = (userId) => {
    setSelectedStudentId(userId);
    setShowStudentProgress(true);
  };

  // Render Overview Stats
  const renderOverview = () => {
    // Admin Overview
    if (isAdmin) {
      return <AdminDashboard onTabChange={setActiveTab} />;
    }

    // Student/Professor Overview
    return (
    <div className="space-y-6">
      {/* Welcome Section */}
      <div className="bg-gradient-to-r from-purple-600 to-blue-600 rounded-lg shadow-lg p-8 text-white">
        <h2 className="text-3xl font-bold mb-2">
          Welcome back, {user?.username || 'User'}!
        </h2>
        <p className="text-purple-100 text-lg">
          {isStudent 
            ? 'Upload and manage your SPMP documents for IEEE 1058 compliance evaluation.'
            : 'Review student submissions, manage tasks, and monitor progress.'}
        </p>
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-white rounded-lg shadow p-6 border-l-4 border-purple-600">
          <div className="flex items-center gap-3">
            <FaFileUpload className="text-purple-600 text-2xl" />
            <div>
              <p className="text-gray-600 text-sm">Documents</p>
              <p className="text-2xl font-bold text-purple-600">--</p>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6 border-l-4 border-green-600">
          <div className="flex items-center gap-3">
            <FaChartBar className="text-green-600 text-2xl" />
            <div>
              <p className="text-gray-600 text-sm">Evaluated</p>
              <p className="text-2xl font-bold text-green-600">--</p>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6 border-l-4 border-blue-600">
          <div className="flex items-center gap-3">
            <FaTasks className="text-blue-600 text-2xl" />
            <div>
              <p className="text-gray-600 text-sm">Tasks</p>
              <p className="text-2xl font-bold text-blue-600">--</p>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-lg shadow p-6 border-l-4 border-orange-600">
          <div className="flex items-center gap-3">
            <FaEye className="text-orange-600 text-2xl" />
            <div>
              <p className="text-gray-600 text-sm">Avg. Score</p>
              <p className="text-2xl font-bold text-orange-600">--</p>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow-lg p-6">
        <h3 className="text-xl font-bold text-gray-900 mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {isStudent ? (
            <>
              <button
                onClick={() => setActiveTab('upload')}
                className="flex items-center gap-3 p-4 border-2 border-dashed border-purple-300 rounded-lg hover:border-purple-500 hover:bg-purple-50 transition"
              >
                <FaFileUpload className="text-purple-600 text-xl" />
                <div className="text-left">
                  <p className="font-semibold text-gray-900">Upload Document</p>
                  <p className="text-sm text-gray-500">Submit new SPMP for evaluation</p>
                </div>
              </button>
              <button
                onClick={() => setActiveTab('documents')}
                className="flex items-center gap-3 p-4 border-2 border-dashed border-blue-300 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition"
              >
                <FaClipboardList className="text-blue-600 text-xl" />
                <div className="text-left">
                  <p className="font-semibold text-gray-900">View Documents</p>
                  <p className="text-sm text-gray-500">Check evaluation results</p>
                </div>
              </button>
              <button
                onClick={() => setActiveTab('tasks')}
                className="flex items-center gap-3 p-4 border-2 border-dashed border-green-300 rounded-lg hover:border-green-500 hover:bg-green-50 transition"
              >
                <FaTasks className="text-green-600 text-xl" />
                <div className="text-left">
                  <p className="font-semibold text-gray-900">My Tasks</p>
                  <p className="text-sm text-gray-500">View assigned tasks</p>
                </div>
              </button>
            </>
          ) : (
            <>
              <button
                onClick={() => setActiveTab('submissions')}
                className="flex items-center gap-3 p-4 border-2 border-dashed border-purple-300 rounded-lg hover:border-purple-500 hover:bg-purple-50 transition"
              >
                <FaClipboardList className="text-purple-600 text-xl" />
                <div className="text-left">
                  <p className="font-semibold text-gray-900">View Submissions</p>
                  <p className="text-sm text-gray-500">Review student documents</p>
                </div>
              </button>
              <button
                onClick={() => setActiveTab('tasks')}
                className="flex items-center gap-3 p-4 border-2 border-dashed border-blue-300 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition"
              >
                <FaTasks className="text-blue-600 text-xl" />
                <div className="text-left">
                  <p className="font-semibold text-gray-900">Manage Tasks</p>
                  <p className="text-sm text-gray-500">Create and assign tasks</p>
                </div>
              </button>
              <button
                onClick={() => setActiveTab('progress')}
                className="flex items-center gap-3 p-4 border-2 border-dashed border-green-300 rounded-lg hover:border-green-500 hover:bg-green-50 transition"
              >
                <FaUserGraduate className="text-green-600 text-xl" />
                <div className="text-left">
                  <p className="font-semibold text-gray-900">Student Progress</p>
                  <p className="text-sm text-gray-500">Monitor performance</p>
                </div>
              </button>
            </>
          )}
        </div>
      </div>

      {/* IEEE 1058 Info */}
      <div className="bg-white rounded-lg shadow-lg p-6">
        <h3 className="text-xl font-bold text-gray-900 mb-4">IEEE 1058 Standard</h3>
        <p className="text-gray-600 mb-4">
          The system evaluates SPMP documents against the IEEE 1058 standard, covering the following sections:
        </p>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
          {[
            'Scope', 'Standards References', 'Definitions', 'Project Overview',
            'Project Organization', 'Managerial Process', 'Technical Process', 'Work Packages',
            'Schedule', 'Risk Management', 'Closeout Plan', 'Annexes'
          ].map((section, idx) => (
            <div key={idx} className="flex items-center gap-2 p-2 bg-purple-50 rounded">
              <span className="bg-purple-600 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs">
                {idx + 1}
              </span>
              <span className="text-sm text-gray-700">{section}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
    );
  };

  // Render Content Based on Active Tab
  const renderContent = () => {
    // Show Report Modal
    if (showReport && selectedDocument) {
      return (
        <EvaluationResults
          document={selectedDocument}
          onClose={() => {
            setShowReport(false);
            setSelectedDocument(null);
          }}
        />
      );
    }

    // Show Student Progress Modal
    if (showStudentProgress && selectedStudentId) {
      return (
        <StudentProgress
          userId={selectedStudentId}
          onClose={() => {
            setShowStudentProgress(false);
            setSelectedStudentId(null);
          }}
        />
      );
    }

    switch (activeTab) {
      case 'overview':
        return renderOverview();
      
      // Student tabs
      case 'upload':
        return (
          <DocumentUpload onUploadSuccess={triggerRefresh} />
        );
      
      case 'documents':
        return (
          <DocumentList
            onViewReport={handleViewReport}
            onReplace={handleReplaceDocument}
            refreshTrigger={refreshTrigger}
          />
        );
      
      case 'tasks':
        return isStudent ? (
          <TaskTracker refreshTrigger={refreshTrigger} />
        ) : (
          <TaskManager refreshTrigger={refreshTrigger} />
        );
      
      // Professor tabs
      case 'submissions':
        return (
          <SubmissionTracker
            onViewReport={handleViewReport}
            onOverrideScore={handleOverrideScore}
            refreshTrigger={refreshTrigger}
          />
        );
      
      case 'progress':
        return (
          <div className="bg-white rounded-lg shadow-lg p-6">
            <h3 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
              <FaUserGraduate className="text-purple-600" /> Student Progress
            </h3>
            <p className="text-gray-600 mb-4">
              Select a student from the Student List tab to view their detailed progress.
            </p>
            <button
              onClick={() => setActiveTab('students')}
              className="text-purple-600 hover:text-purple-700 font-semibold"
            >
              Go to Student List →
            </button>
          </div>
        );
      
      // UC 2.10: Class-wide Student List
      case 'students':
        return (
          <StudentList
            onSelectStudent={handleViewStudentProgress}
            refreshTrigger={refreshTrigger}
          />
        );
      
      // UC 2.7: Grading Criteria Management
      case 'grading':
        return (
          <GradingCriteria refreshTrigger={refreshTrigger} />
        );
      
      // UC 3.2: Parser Configuration
      case 'parser':
        return (
          <ParserConfiguration />
        );

      // Admin tabs
      case 'users':
        return <UserManagement refreshTrigger={refreshTrigger} />;
      
      case 'assignments':
        return <StudentAssignment refreshTrigger={refreshTrigger} />;
      
      case 'audit':
        return <AuditLogViewer />;
      
      case 'settings':
        return (
          <div className="bg-white rounded-lg shadow-lg p-6">
            <h3 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
              <FaCogs className="text-purple-600" /> System Settings
            </h3>
            <p className="text-gray-600">System settings management coming soon...</p>
          </div>
        );

      default:
        return renderOverview();
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 to-blue-50">
      <Navbar />

      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Navigation Tabs */}
        <div className="mb-6">
          <div className="flex gap-2 flex-wrap bg-white rounded-lg shadow p-2">
            {tabs.map((tab) => {
              const Icon = tab.icon;
              return (
                <button
                  key={tab.id}
                  onClick={() => {
                    setActiveTab(tab.id);
                    setShowReport(false);
                    setShowStudentProgress(false);
                  }}
                  className={`flex items-center gap-2 px-4 py-2 rounded-lg font-semibold transition ${
                    activeTab === tab.id
                      ? 'bg-purple-600 text-white shadow'
                      : 'text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  <Icon />
                  {tab.label}
                </button>
              );
            })}
          </div>
        </div>

        {/* Main Content */}
        {renderContent()}
      </div>

      {/* Score Override Modal */}
      {showScoreOverride && selectedDocument && (
        <ScoreOverride
          document={selectedDocument}
          onClose={() => {
            setShowScoreOverride(false);
            setSelectedDocument(null);
          }}
          onSuccess={triggerRefresh}
        />
      )}

      {/* File Replace Modal (UC 2.2) */}
      {showFileReplace && selectedDocument && (
        <FileReplaceModal
          document={selectedDocument}
          onClose={() => {
            setShowFileReplace(false);
            setSelectedDocument(null);
          }}
          onSuccess={() => {
            setShowFileReplace(false);
            setSelectedDocument(null);
            triggerRefresh();
          }}
        />
      )}
    </div>
  );
};

export default Dashboard;
