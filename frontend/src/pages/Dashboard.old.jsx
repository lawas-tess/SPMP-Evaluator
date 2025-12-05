import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext.jsx';
import Navbar from '../components/Navbar.jsx';
import { FaFileUpload, FaChartBar, FaCheckCircle, FaTasks, FaArrowRight } from 'react-icons/fa';

const Dashboard = () => {
  const { user } = useAuth();
  const [activeSection, setActiveSection] = useState('overview');

  const workflowSteps = [
    {
      id: 1,
      title: 'Upload SPMP Document',
      description: 'Upload your Software Project Management Plan (PDF or DOCX)',
      icon: FaFileUpload,
      details: 'Supported formats: PDF, DOCX. Maximum file size: 50MB. The system will extract and analyze the document content.'
    },
    {
      id: 2,
      title: 'Document Analysis',
      description: 'Our AI-powered system analyzes your document against IEEE 1058 standards',
      icon: FaCheckCircle,
      details: 'The system evaluates compliance across 10 major sections: Scope, Standards References, Definitions, Project Overview, Project Organization, Managerial Process, Technical Process, Work Packages, Schedule, and Risk Management.'
    },
    {
      id: 3,
      title: 'Compliance Scoring',
      description: 'Receive detailed compliance scores and recommendations',
      icon: FaChartBar,
      details: 'Get section-by-section compliance scores, identify missing elements, and receive actionable recommendations for improvement.'
    },
    {
      id: 4,
      title: 'Task Management',
      description: 'Track improvements and assign tasks to team members',
      icon: FaTasks,
      details: 'Create action items based on recommendations, assign tasks to team members, and track progress towards full compliance.'
    }
  ];

  const features = [
    {
      title: 'IEEE 1058 Compliance',
      description: 'Evaluate compliance against the IEEE Standard for Software Project Management Plans',
      icon: '✓'
    },
    {
      title: 'Detailed Analysis',
      description: 'Get comprehensive section-by-section analysis with specific recommendations',
      icon: '📊'
    },
    {
      title: 'Multi-User Collaboration',
      description: 'Share documents and collaborate with team members across different roles',
      icon: '👥'
    },
    {
      title: 'Progress Tracking',
      description: 'Monitor improvement progress and track task completion',
      icon: '📈'
    },
    {
      title: 'Document Management',
      description: 'Manage multiple SPMP documents and maintain version history',
      icon: '📁'
    },
    {
      title: 'Comprehensive Reports',
      description: 'Generate detailed reports for stakeholders and documentation',
      icon: '📄'
    }
  ];

  const roles = [
    {
      title: 'Student',
      responsibilities: ['Upload SPMP documents', 'Review feedback', 'Make improvements', 'Track progress']
    },
    {
      title: 'Professor',
      responsibilities: ['Review submissions', 'Provide feedback', 'Track student progress', 'Generate reports']
    }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 to-blue-50">
      <Navbar />

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 py-12">
        {/* Header Section */}
        <div className="mb-12">
          <h2 className="text-4xl font-bold text-gray-900 mb-4">Welcome to SPMP Evaluator</h2>
          <p className="text-lg text-gray-600">
            Your comprehensive platform for evaluating Software Project Management Plans against IEEE 1058 standards
          </p>
        </div>

        {/* Navigation Tabs */}
        <div className="flex gap-4 mb-8 flex-wrap">
          {['overview', 'workflow', 'features', 'roles'].map((section) => (
            <button
              key={section}
              onClick={() => setActiveSection(section)}
              className={`px-6 py-2 rounded-lg font-semibold transition ${
                activeSection === section
                  ? 'bg-purple-600 text-white shadow-lg'
                  : 'bg-white text-gray-700 border border-gray-300 hover:border-purple-600'
              }`}
            >
              {section.charAt(0).toUpperCase() + section.slice(1)}
            </button>
          ))}
        </div>

        {/* Overview Section */}
        {activeSection === 'overview' && (
          <div className="space-y-8">
            <div className="bg-white rounded-lg shadow-lg p-8">
              <h3 className="text-2xl font-bold text-gray-900 mb-6">How SPMP Evaluator Works</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div>
                  <h4 className="text-lg font-semibold text-purple-600 mb-4">What is SPMP?</h4>
                  <p className="text-gray-700 leading-relaxed mb-4">
                    A Software Project Management Plan (SPMP) is a comprehensive document that defines how a software project will be managed from start to finish. It's required for all software projects and contains critical information about project scope, organization, resources, and processes.
                  </p>
                  <p className="text-gray-700 leading-relaxed">
                    The IEEE 1058 standard provides a detailed framework for what a complete and professional SPMP should contain.
                  </p>
                </div>
                <div className="bg-purple-50 rounded-lg p-6 border border-purple-200">
                  <h4 className="text-lg font-semibold text-purple-600 mb-4">Why Evaluate?</h4>
                  <ul className="space-y-3 text-gray-700">
                    <li className="flex items-start gap-3">
                      <span className="text-purple-600 font-bold">✓</span>
                      <span>Ensure compliance with industry standards</span>
                    </li>
                    <li className="flex items-start gap-3">
                      <span className="text-purple-600 font-bold">✓</span>
                      <span>Identify missing critical components</span>
                    </li>
                    <li className="flex items-start gap-3">
                      <span className="text-purple-600 font-bold">✓</span>
                      <span>Improve project management quality</span>
                    </li>
                    <li className="flex items-start gap-3">
                      <span className="text-purple-600 font-bold">✓</span>
                      <span>Receive actionable improvement recommendations</span>
                    </li>
                  </ul>
                </div>
              </div>
            </div>

            {/* Quick Stats */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="bg-white rounded-lg shadow p-6 border-l-4 border-purple-600">
                <p className="text-gray-600 text-sm font-semibold">IEEE 1058 Sections</p>
                <p className="text-3xl font-bold text-purple-600">10</p>
              </div>
              <div className="bg-white rounded-lg shadow p-6 border-l-4 border-blue-600">
                <p className="text-gray-600 text-sm font-semibold">Compliance Score</p>
                <p className="text-3xl font-bold text-blue-600">0-100%</p>
              </div>
              <div className="bg-white rounded-lg shadow p-6 border-l-4 border-green-600">
                <p className="text-gray-600 text-sm font-semibold">Analysis Areas</p>
                <p className="text-3xl font-bold text-green-600">50+</p>
              </div>
              <div className="bg-white rounded-lg shadow p-6 border-l-4 border-orange-600">
                <p className="text-gray-600 text-sm font-semibold">Max File Size</p>
                <p className="text-3xl font-bold text-orange-600">50 MB</p>
              </div>
            </div>
          </div>
        )}

        {/* Workflow Section */}
        {activeSection === 'workflow' && (
          <div className="bg-white rounded-lg shadow-lg p-8">
            <h3 className="text-2xl font-bold text-gray-900 mb-8">4-Step Workflow</h3>
            <div className="space-y-6">
              {workflowSteps.map((step, index) => {
                const Icon = step.icon;
                return (
                  <div key={step.id} className="flex gap-6">
                    <div className="flex flex-col items-center">
                      <div className="bg-purple-600 text-white rounded-full w-12 h-12 flex items-center justify-center font-bold text-lg">
                        {step.id}
                      </div>
                      {index < workflowSteps.length - 1 && (
                        <div className="w-1 h-16 bg-purple-200 mt-2"></div>
                      )}
                    </div>
                    <div className="flex-1 pb-6">
                      <div className="flex items-start gap-3 mb-3">
                        <Icon className="text-purple-600 text-xl mt-1" />
                        <h4 className="text-xl font-bold text-gray-900">{step.title}</h4>
                      </div>
                      <p className="text-gray-700 mb-3">{step.description}</p>
                      <div className="bg-blue-50 border border-blue-200 rounded p-4 text-sm text-gray-700">
                        {step.details}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* Features Section */}
        {activeSection === 'features' && (
          <div className="space-y-8">
            <h3 className="text-2xl font-bold text-gray-900">Key Features</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {features.map((feature, index) => (
                <div key={index} className="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition">
                  <div className="text-4xl mb-3">{feature.icon}</div>
                  <h4 className="text-lg font-bold text-gray-900 mb-2">{feature.title}</h4>
                  <p className="text-gray-600">{feature.description}</p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Roles Section */}
        {activeSection === 'roles' && (
          <div className="space-y-8">
            <h3 className="text-2xl font-bold text-gray-900">User Roles & Responsibilities</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {roles.map((role, index) => (
                <div key={index} className="bg-white rounded-lg shadow-lg p-6">
                  <h4 className="text-xl font-bold text-purple-600 mb-4">{role.title}</h4>
                  <ul className="space-y-3">
                    {role.responsibilities.map((resp, i) => (
                      <li key={i} className="flex items-start gap-3 text-gray-700">
                        <span className="text-purple-600 font-bold mt-1">→</span>
                        <span>{resp}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              ))}
            </div>

            {/* IEEE 1058 Sections */}
            <div className="bg-white rounded-lg shadow-lg p-8 mt-8">
              <h4 className="text-xl font-bold text-gray-900 mb-6">IEEE 1058 Standard Sections</h4>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {[
                  'Scope',
                  'Standards References',
                  'Definitions',
                  'Project Overview',
                  'Project Organization',
                  'Managerial Process',
                  'Technical Process',
                  'Work Packages',
                  'Schedule',
                  'Risk Management'
                ].map((section, i) => (
                  <div key={i} className="flex items-center gap-3 p-3 bg-purple-50 rounded border border-purple-200">
                    <span className="bg-purple-600 text-white rounded-full w-6 h-6 flex items-center justify-center text-xs font-bold">
                      {i + 1}
                    </span>
                    <span className="font-semibold text-gray-900">{section}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* Call to Action */}
        <div className="mt-12 bg-gradient-to-r from-purple-600 to-blue-600 rounded-lg shadow-lg p-8 text-white text-center">
          <h3 className="text-2xl font-bold mb-4">Ready to Get Started?</h3>
          <p className="text-lg mb-6">Upload your SPMP document to begin comprehensive evaluation</p>
          <button className="bg-white text-purple-600 px-8 py-3 rounded-lg font-bold hover:bg-gray-100 transition flex items-center justify-center gap-2 mx-auto">
            Upload Document
            <FaFileUpload />
          </button>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
