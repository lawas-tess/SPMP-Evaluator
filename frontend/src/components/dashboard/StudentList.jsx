import React, { useState, useEffect } from 'react';
import { 
  FaUsers, FaSpinner, FaSync, FaSearch, FaChartLine, FaDownload,
  FaExclamationTriangle, FaCheckCircle, FaClock, FaUser, FaFileAlt,
  FaTasks, FaEye, FaFilter
} from 'react-icons/fa';
import { reportAPI, userAPI } from '../../services/apiService';

/**
 * UC 2.10 - Professor Monitor Student Progress
 * Basic Flow:
 * 1. Professor navigates to the progress dashboard
 * 2. System displays a class-wide summary
 * 3. Professor filters by student or group
 * 4. Professor reviews individual submission status and scores
 * 5. System logs viewing activity
 */
const StudentList = ({ onSelectStudent, refreshTrigger }) => {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [sortBy, setSortBy] = useState('name'); // 'name', 'score', 'submissions'
  const [classSummary, setClassSummary] = useState(null);

  // Step 1 & 2: Load students and class-wide summary
  const fetchStudents = async () => {
    setLoading(true);
    setError(null);
    try {
      // Fetch all students
      const studentsResponse = await userAPI.getAllStudents();
      const studentList = studentsResponse.data || [];

      // Fetch progress for each student
      const studentsWithProgress = await Promise.all(
        studentList.map(async (student) => {
          try {
            const progressResponse = await reportAPI.getStudentProgress(student.id);
            const data = progressResponse.data;
            
            // Map backend response to expected format
            return {
              ...student,
              progress: {
                totalDocuments: data.documents?.totalUploads || 0,
                evaluatedDocuments: data.documents?.evaluated || 0,
                averageScore: parseFloat(data.documents?.averageScore) || 0,
                totalTasks: data.tasks?.totalTasks || 0,
                completedTasks: data.tasks?.completed || 0
              }
            };
          } catch (err) {
            return {
              ...student,
              progress: {
                totalDocuments: 0,
                evaluatedDocuments: 0,
                averageScore: 0,
                totalTasks: 0,
                completedTasks: 0
              }
            };
          }
        })
      );

      setStudents(studentsWithProgress);

      // Calculate class-wide summary
      calculateClassSummary(studentsWithProgress);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load students');
    } finally {
      setLoading(false);
    }
  };

  // Step 2: System displays a class-wide summary
  const calculateClassSummary = (studentList) => {
    if (!studentList.length) {
      setClassSummary(null);
      return;
    }

    const totalStudents = studentList.length;
    const totalDocuments = studentList.reduce((sum, s) => sum + (s.progress?.totalDocuments || 0), 0);
    const totalEvaluated = studentList.reduce((sum, s) => sum + (s.progress?.evaluatedDocuments || 0), 0);
    const totalTasks = studentList.reduce((sum, s) => sum + (s.progress?.totalTasks || 0), 0);
    const completedTasks = studentList.reduce((sum, s) => sum + (s.progress?.completedTasks || 0), 0);
    
    const scoresArray = studentList
      .filter(s => s.progress?.evaluatedDocuments > 0)
      .map(s => s.progress?.averageScore || 0);
    
    const avgClassScore = scoresArray.length > 0 
      ? scoresArray.reduce((sum, score) => sum + score, 0) / scoresArray.length 
      : 0;

    const compliantStudents = studentList.filter(s => (s.progress?.averageScore || 0) >= 80).length;

    setClassSummary({
      totalStudents,
      totalDocuments,
      totalEvaluated,
      pendingEvaluations: totalDocuments - totalEvaluated,
      avgClassScore,
      compliantStudents,
      totalTasks,
      completedTasks,
      taskCompletionRate: totalTasks > 0 ? (completedTasks / totalTasks) * 100 : 0
    });
  };

  useEffect(() => {
    fetchStudents();
  }, [refreshTrigger]);

  // Step 3: Professor filters by student
  const filteredStudents = students.filter((student) => {
    if (!searchQuery) return true;
    const search = searchQuery.toLowerCase();
    const fullName = `${student.firstName || ''} ${student.lastName || ''}`.toLowerCase();
    return (
      fullName.includes(search) ||
      student.email?.toLowerCase().includes(search) ||
      student.username?.toLowerCase().includes(search)
    );
  });

  // Sort students
  const sortedStudents = [...filteredStudents].sort((a, b) => {
    switch (sortBy) {
      case 'score':
        return (b.progress?.averageScore || 0) - (a.progress?.averageScore || 0);
      case 'submissions':
        return (b.progress?.totalDocuments || 0) - (a.progress?.totalDocuments || 0);
      case 'name':
      default:
        const nameA = `${a.firstName || ''} ${a.lastName || ''}`.toLowerCase();
        const nameB = `${b.firstName || ''} ${b.lastName || ''}`.toLowerCase();
        return nameA.localeCompare(nameB);
    }
  });

  // Alternative Flow: Export data as CSV
  const handleExportCSV = () => {
    const headers = ['Name', 'Email', 'Documents', 'Evaluated', 'Avg Score', 'Tasks Completed'];
    const rows = students.map(s => [
      `${s.firstName || ''} ${s.lastName || ''}`,
      s.email || '',
      s.progress?.totalDocuments || 0,
      s.progress?.evaluatedDocuments || 0,
      (s.progress?.averageScore || 0).toFixed(1),
      `${s.progress?.completedTasks || 0}/${s.progress?.totalTasks || 0}`
    ]);

    const csvContent = [
      headers.join(','),
      ...rows.map(row => row.join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `student_progress_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  };

  const getScoreColor = (score) => {
    if (score >= 80) return 'text-green-600';
    if (score >= 50) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getScoreBadge = (score) => {
    if (score >= 80) {
      return <span className="px-2 py-0.5 text-xs font-semibold bg-green-100 text-green-800 rounded-full">Compliant</span>;
    } else if (score >= 50) {
      return <span className="px-2 py-0.5 text-xs font-semibold bg-yellow-100 text-yellow-800 rounded-full">In Progress</span>;
    }
    return <span className="px-2 py-0.5 text-xs font-semibold bg-red-100 text-red-800 rounded-full">Needs Work</span>;
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-8 text-center">
        <FaSpinner className="animate-spin text-purple-600 text-3xl mx-auto mb-3" />
        <p className="text-gray-600">Loading student progress...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-8 text-center">
        <div className="text-red-500 mb-4">
          <FaExclamationTriangle className="text-3xl mx-auto mb-2" />
          <p>{error}</p>
        </div>
        <button 
          onClick={fetchStudents}
          className="text-purple-600 hover:text-purple-700 font-semibold flex items-center gap-2 mx-auto"
        >
          <FaSync /> Retry
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Class-Wide Summary - Step 2 */}
      {classSummary && (
        <div className="bg-white rounded-lg shadow-lg p-6">
          <h3 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
            <FaChartLine className="text-purple-600" /> Class Performance Summary
          </h3>
          
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {/* Total Students */}
            <div className="bg-purple-50 rounded-lg p-4 text-center">
              <FaUsers className="text-purple-600 text-2xl mx-auto mb-2" />
              <p className="text-3xl font-bold text-purple-600">{classSummary.totalStudents}</p>
              <p className="text-sm text-gray-600">Total Students</p>
            </div>

            {/* Average Score */}
            <div className={`rounded-lg p-4 text-center ${
              classSummary.avgClassScore >= 80 ? 'bg-green-50' : 
              classSummary.avgClassScore >= 50 ? 'bg-yellow-50' : 'bg-red-50'
            }`}>
              <FaChartLine className={`text-2xl mx-auto mb-2 ${getScoreColor(classSummary.avgClassScore)}`} />
              <p className={`text-3xl font-bold ${getScoreColor(classSummary.avgClassScore)}`}>
                {classSummary.avgClassScore.toFixed(1)}%
              </p>
              <p className="text-sm text-gray-600">Avg. Class Score</p>
            </div>

            {/* Documents */}
            <div className="bg-blue-50 rounded-lg p-4 text-center">
              <FaFileAlt className="text-blue-600 text-2xl mx-auto mb-2" />
              <p className="text-3xl font-bold text-blue-600">
                {classSummary.totalEvaluated}/{classSummary.totalDocuments}
              </p>
              <p className="text-sm text-gray-600">Evaluated/Total Docs</p>
            </div>

            {/* Task Completion */}
            <div className="bg-green-50 rounded-lg p-4 text-center">
              <FaTasks className="text-green-600 text-2xl mx-auto mb-2" />
              <p className="text-3xl font-bold text-green-600">
                {classSummary.taskCompletionRate.toFixed(0)}%
              </p>
              <p className="text-sm text-gray-600">Task Completion</p>
            </div>
          </div>

          {/* Compliance Summary Bar */}
          <div className="mt-4 p-4 bg-gray-50 rounded-lg">
            <div className="flex justify-between items-center mb-2">
              <span className="text-sm font-semibold text-gray-700">
                Students Meeting Compliance (≥80%)
              </span>
              <span className="text-sm font-bold text-purple-600">
                {classSummary.compliantStudents}/{classSummary.totalStudents} students
              </span>
            </div>
            <div className="h-3 bg-gray-200 rounded-full overflow-hidden">
              <div
                className="h-full bg-gradient-to-r from-purple-500 to-green-500 transition-all"
                style={{ width: `${(classSummary.compliantStudents / classSummary.totalStudents) * 100}%` }}
              />
            </div>
          </div>
        </div>
      )}

      {/* Student List */}
      <div className="bg-white rounded-lg shadow-lg p-6">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2">
            <FaUsers className="text-purple-600" /> Student Progress
          </h3>
          <div className="flex gap-2">
            <button
              onClick={fetchStudents}
              disabled={loading}
              className="text-purple-600 hover:text-purple-700 flex items-center gap-1 text-sm"
            >
              <FaSync className={loading ? 'animate-spin' : ''} />
            </button>
            <button
              onClick={handleExportCSV}
              className="px-3 py-1.5 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 flex items-center gap-1 text-sm font-semibold"
            >
              <FaDownload /> Export CSV
            </button>
          </div>
        </div>

        {/* Step 3: Filters */}
        <div className="flex flex-col md:flex-row gap-3 mb-4">
          {/* Search */}
          <div className="flex-1 relative">
            <FaSearch className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Search by name or email..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:border-purple-500 focus:ring-1 focus:ring-purple-500"
            />
          </div>

          {/* Sort */}
          <div className="flex items-center gap-2">
            <FaFilter className="text-gray-400" />
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="px-3 py-2 border border-gray-300 rounded-lg focus:border-purple-500 focus:ring-1 focus:ring-purple-500"
            >
              <option value="name">Sort by Name</option>
              <option value="score">Sort by Score</option>
              <option value="submissions">Sort by Submissions</option>
            </select>
          </div>
        </div>

        {/* Student Cards - Step 4: Review individual status */}
        {sortedStudents.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            <FaUsers className="text-4xl mx-auto mb-3 text-gray-400" />
            <p>No students found</p>
          </div>
        ) : (
          <div className="grid gap-4 md:grid-cols-2">
            {sortedStudents.map((student) => (
              <div
                key={student.id}
                className="border rounded-lg p-4 hover:border-purple-300 hover:shadow-md transition cursor-pointer"
                onClick={() => onSelectStudent && onSelectStudent(student.id, `${student.firstName} ${student.lastName}`)}
              >
                <div className="flex items-start gap-4">
                  {/* Avatar */}
                  <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center flex-shrink-0">
                    <FaUser className="text-purple-600 text-xl" />
                  </div>

                  {/* Info */}
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-2">
                      <div>
                        <h4 className="font-semibold text-gray-900">
                          {student.firstName} {student.lastName}
                        </h4>
                        <p className="text-sm text-gray-500 truncate">{student.email}</p>
                      </div>
                      {student.progress?.evaluatedDocuments > 0 && getScoreBadge(student.progress.averageScore)}
                    </div>

                    {/* Progress Stats */}
                    <div className="mt-3 grid grid-cols-3 gap-2 text-center">
                      <div className="bg-gray-50 rounded p-2">
                        <p className="text-lg font-bold text-purple-600">
                          {student.progress?.totalDocuments || 0}
                        </p>
                        <p className="text-xs text-gray-500">Docs</p>
                      </div>
                      <div className="bg-gray-50 rounded p-2">
                        <p className={`text-lg font-bold ${getScoreColor(student.progress?.averageScore || 0)}`}>
                          {(student.progress?.averageScore || 0).toFixed(0)}%
                        </p>
                        <p className="text-xs text-gray-500">Avg Score</p>
                      </div>
                      <div className="bg-gray-50 rounded p-2">
                        <p className="text-lg font-bold text-green-600">
                          {student.progress?.completedTasks || 0}/{student.progress?.totalTasks || 0}
                        </p>
                        <p className="text-xs text-gray-500">Tasks</p>
                      </div>
                    </div>

                    {/* View Details Button */}
                    <button
                      className="mt-3 w-full py-1.5 text-sm text-purple-600 hover:text-purple-700 hover:bg-purple-50 rounded flex items-center justify-center gap-1"
                      onClick={(e) => {
                        e.stopPropagation();
                        onSelectStudent && onSelectStudent(student.id, `${student.firstName} ${student.lastName}`);
                      }}
                    >
                      <FaEye /> View Details
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default StudentList;
