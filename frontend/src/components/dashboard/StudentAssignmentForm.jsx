import React, { useState, useEffect } from 'react';
import api from '../../services/apiService';

/**
 * UC 2.12: Admin Assign Students to Professors
 * Allows admin to assign students to professors for supervision
 */
const StudentAssignmentForm = () => {
  const [professors, setProfessors] = useState([]);
  const [students, setStudents] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [selectedProfessor, setSelectedProfessor] = useState('');
  const [selectedStudents, setSelectedStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState({ type: '', text: '' });
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [profsRes, studentsRes, assignmentsRes] = await Promise.all([
        api.get('/admin/users?role=PROFESSOR'),
        api.get('/admin/users?role=STUDENT'),
        api.get('/admin/assignments')
      ]);
      setProfessors(profsRes.data);
      setStudents(studentsRes.data);
      setAssignments(assignmentsRes.data);
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to load data' });
    } finally {
      setLoading(false);
    }
  };

  const handleAssign = async (e) => {
    e.preventDefault();
    if (!selectedProfessor || selectedStudents.length === 0) {
      setMessage({ type: 'error', text: 'Please select a professor and at least one student' });
      return;
    }

    try {
      await api.post('/admin/assignments', {
        professorId: selectedProfessor,
        studentIds: selectedStudents
      });
      setMessage({ type: 'success', text: `Successfully assigned ${selectedStudents.length} student(s)` });
      setSelectedProfessor('');
      setSelectedStudents([]);
      setShowForm(false);
      fetchData();
    } catch (error) {
      setMessage({ type: 'error', text: error.response?.data?.message || 'Failed to assign students' });
    }
  };

  const handleUnassign = async (assignmentId) => {
    if (!window.confirm('Are you sure you want to remove this assignment?')) return;

    try {
      await api.delete(`/admin/assignments/${assignmentId}`);
      setMessage({ type: 'success', text: 'Assignment removed successfully' });
      fetchData();
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to remove assignment' });
    }
  };

  const toggleStudentSelection = (studentId) => {
    setSelectedStudents(prev =>
      prev.includes(studentId)
        ? prev.filter(id => id !== studentId)
        : [...prev, studentId]
    );
  };

  const getAssignedStudentsForProfessor = (professorId) => {
    return assignments.filter(a => a.professorId === professorId);
  };

  if (loading) {
    return <div className="p-6">Loading...</div>;
  }

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold">Student-Professor Assignments</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          {showForm ? 'Cancel' : 'New Assignment'}
        </button>
      </div>

      {message.text && (
        <div className={`mb-4 p-4 rounded ${
          message.type === 'error' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'
        }`}>
          {message.text}
        </div>
      )}

      {showForm && (
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h3 className="text-xl font-semibold mb-4">Assign Students to Professor</h3>
          <form onSubmit={handleAssign}>
            {/* Professor Selection */}
            <div className="mb-4">
              <label className="block text-sm font-medium mb-2">Select Professor</label>
              <select
                value={selectedProfessor}
                onChange={(e) => setSelectedProfessor(e.target.value)}
                className="w-full p-2 border rounded"
                required
              >
                <option value="">-- Select Professor --</option>
                {professors.map(prof => (
                  <option key={prof.id} value={prof.id}>
                    {prof.name} ({prof.email}) - {getAssignedStudentsForProfessor(prof.id).length} students
                  </option>
                ))}
              </select>
            </div>

            {/* Student Selection */}
            <div className="mb-4">
              <label className="block text-sm font-medium mb-2">
                Select Students ({selectedStudents.length} selected)
              </label>
              <div className="border rounded p-4 max-h-64 overflow-y-auto">
                {students.length === 0 ? (
                  <p className="text-gray-500">No students available</p>
                ) : (
                  students.map(student => {
                    const isAssigned = assignments.some(a => a.studentId === student.id);
                    const assignedProfessor = isAssigned 
                      ? professors.find(p => p.id === assignments.find(a => a.studentId === student.id)?.professorId)
                      : null;

                    return (
                      <div key={student.id} className="flex items-center mb-2">
                        <input
                          type="checkbox"
                          id={`student-${student.id}`}
                          checked={selectedStudents.includes(student.id)}
                          onChange={() => toggleStudentSelection(student.id)}
                          className="mr-2"
                        />
                        <label htmlFor={`student-${student.id}`} className="flex-1">
                          {student.name} ({student.email})
                          {isAssigned && (
                            <span className="text-sm text-yellow-600 ml-2">
                              Already assigned to {assignedProfessor?.name}
                            </span>
                          )}
                        </label>
                      </div>
                    );
                  })
                )}
              </div>
            </div>

            <button
              type="submit"
              className="bg-green-600 text-white px-6 py-2 rounded hover:bg-green-700"
            >
              Assign Students
            </button>
          </form>
        </div>
      )}

      {/* Current Assignments Table */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <h3 className="text-xl font-semibold p-4 border-b">Current Assignments</h3>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Professor</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Student</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Assigned Date</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {assignments.length === 0 ? (
                <tr>
                  <td colSpan="4" className="px-6 py-4 text-center text-gray-500">
                    No assignments yet
                  </td>
                </tr>
              ) : (
                assignments.map(assignment => {
                  return (
                    <tr key={assignment.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4">
                        {assignment.professorName || 'Unknown'}<br />
                        <span className="text-sm text-gray-500">{assignment.professorEmail || 'N/A'}</span>
                      </td>
                      <td className="px-6 py-4">
                        {assignment.studentName || 'Unknown'}<br />
                        <span className="text-sm text-gray-500">{assignment.studentEmail || 'N/A'}</span>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-500">
                        {assignment.assignedAt ? new Date(assignment.assignedAt).toLocaleDateString() : 'N/A'}
                      </td>
                      <td className="px-6 py-4">
                        <button
                          onClick={() => handleUnassign(assignment.id)}
                          className="text-red-600 hover:text-red-800"
                        >
                          Remove
                        </button>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Statistics Summary */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
        <div className="bg-blue-50 rounded-lg p-4">
          <div className="text-sm text-gray-600">Total Professors</div>
          <div className="text-2xl font-bold text-blue-600">{professors.length}</div>
        </div>
        <div className="bg-green-50 rounded-lg p-4">
          <div className="text-sm text-gray-600">Total Students</div>
          <div className="text-2xl font-bold text-green-600">{students.length}</div>
        </div>
        <div className="bg-purple-50 rounded-lg p-4">
          <div className="text-sm text-gray-600">Active Assignments</div>
          <div className="text-2xl font-bold text-purple-600">{assignments.length}</div>
        </div>
      </div>
    </div>
  );
};

export default StudentAssignmentForm;
