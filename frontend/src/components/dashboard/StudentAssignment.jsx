import React, { useState, useEffect } from 'react';
import api from '../../services/apiService';

const StudentAssignment = () => {
  const [students, setStudents] = useState([]);
  const [professors, setProfessors] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedStudent, setSelectedStudent] = useState('');
  const [selectedProfessor, setSelectedProfessor] = useState('');
  const [notes, setNotes] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [studentsRes, professorsRes, assignmentsRes] = await Promise.all([
        api.get('/admin/users?role=STUDENT'),
        api.get('/admin/users?role=PROFESSOR'),
        api.get('/admin/assignments')
      ]);
      setStudents(studentsRes.data);
      setProfessors(professorsRes.data);
      setAssignments(assignmentsRes.data);
    } catch (error) {
      console.error('Failed to fetch data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAssign = async (e) => {
    e.preventDefault();
    if (!selectedStudent || !selectedProfessor) {
      alert('Please select both student and professor');
      return;
    }

    try {
      await api.post('/admin/assignments', {
        studentId: parseInt(selectedStudent),
        professorId: parseInt(selectedProfessor),
        notes
      });
      alert('Student assigned successfully');
      setSelectedStudent('');
      setSelectedProfessor('');
      setNotes('');
      fetchData();
    } catch (error) {
      alert('Failed to assign student: ' + (error.response?.data || error.message));
    }
  };

  const handleRemove = async (assignmentId) => {
    if (window.confirm('Are you sure you want to remove this assignment?')) {
      try {
        await api.delete(`/admin/assignments/${assignmentId}`);
        fetchData();
      } catch (error) {
        alert('Failed to remove assignment');
      }
    }
  };

  if (loading) return <div className="p-6">Loading...</div>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6">Student-Professor Assignment</h1>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Assignment Form */}
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-semibold mb-4">Create Assignment</h2>
          <form onSubmit={handleAssign}>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-2">Student</label>
              <select
                value={selectedStudent}
                onChange={(e) => setSelectedStudent(e.target.value)}
                className="w-full border rounded px-3 py-2"
                required
              >
                <option value="">Select Student</option>
                {students.map((student) => (
                  <option key={student.id} value={student.id}>
                    {student.firstName} {student.lastName} ({student.username})
                  </option>
                ))}
              </select>
            </div>

            <div className="mb-4">
              <label className="block text-sm font-medium mb-2">Professor</label>
              <select
                value={selectedProfessor}
                onChange={(e) => setSelectedProfessor(e.target.value)}
                className="w-full border rounded px-3 py-2"
                required
              >
                <option value="">Select Professor</option>
                {professors.map((professor) => (
                  <option key={professor.id} value={professor.id}>
                    {professor.firstName} {professor.lastName} ({professor.username})
                  </option>
                ))}
              </select>
            </div>

            <div className="mb-4">
              <label className="block text-sm font-medium mb-2">Notes (Optional)</label>
              <textarea
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                className="w-full border rounded px-3 py-2"
                rows="3"
                placeholder="Add any notes about this assignment..."
              />
            </div>

            <button
              type="submit"
              className="w-full bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            >
              Assign Student
            </button>
          </form>
        </div>

        {/* Assignments List */}
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-semibold mb-4">Current Assignments ({assignments.length})</h2>
          <div className="space-y-3 max-h-96 overflow-y-auto">
            {assignments.map((assignment) => (
              <div key={assignment.id} className="border rounded p-3">
                <div className="flex justify-between items-start">
                  <div>
                    <div className="font-medium text-blue-600">
                      {assignment.studentName}
                    </div>
                    <div className="text-sm text-gray-600">
                      Assigned to: {assignment.professorName}
                    </div>
                    {assignment.notes && (
                      <div className="text-sm text-gray-500 mt-1">
                        Note: {assignment.notes}
                      </div>
                    )}
                    <div className="text-xs text-gray-400 mt-1">
                      {new Date(assignment.assignedAt).toLocaleDateString()}
                    </div>
                  </div>
                  <button
                    onClick={() => handleRemove(assignment.id)}
                    className="text-red-600 hover:text-red-800 text-sm"
                  >
                    Remove
                  </button>
                </div>
              </div>
            ))}
            {assignments.length === 0 && (
              <div className="text-center text-gray-500 py-8">
                No assignments yet
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default StudentAssignment;
