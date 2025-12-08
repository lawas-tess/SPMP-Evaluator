import React, { useState, useEffect } from 'react';
import { 
  FaTasks, FaPlus, FaSpinner, FaEdit, FaTrash, FaSync,
  FaExclamationTriangle, FaCheck, FaClock, FaUser, FaTimes
} from 'react-icons/fa';
import { taskAPI, userAPI } from '../../services/apiService';

const TaskManager = ({ refreshTrigger }) => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [editingTask, setEditingTask] = useState(null);

  const fetchTasks = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await taskAPI.getCreatedTasks();
      setTasks(response.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load tasks');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTasks();
  }, [refreshTrigger]);

  const handleDelete = async (taskId) => {
    if (!window.confirm('Are you sure you want to delete this task?')) return;
    
    try {
      await taskAPI.delete(taskId);
      await fetchTasks();
    } catch (err) {
      alert(err.response?.data?.message || 'Delete failed');
    }
  };

  const getPriorityBadge = (priority) => {
    const colors = {
      HIGH: 'bg-red-100 text-red-800',
      MEDIUM: 'bg-yellow-100 text-yellow-800',
      LOW: 'bg-green-100 text-green-800'
    };
    return (
      <span className={`px-2 py-0.5 text-xs font-semibold rounded ${colors[priority] || colors.MEDIUM}`}>
        {priority}
      </span>
    );
  };

  const getStatusIcon = (completed) => {
    if (completed) return <FaCheck className="text-green-500" />;
    return <FaClock className="text-gray-400" />;
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'No due date';
    return new Date(dateString).toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  // Statistics
  const totalTasks = tasks.length;
  const completedTasks = tasks.filter(t => t.completed).length;
  const pendingTasks = totalTasks - completedTasks;

  if (loading && tasks.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-8 text-center">
        <FaSpinner className="animate-spin text-purple-600 text-3xl mx-auto mb-3" />
        <p className="text-gray-600">Loading tasks...</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      <div className="flex justify-between items-center mb-6">
        <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2">
          <FaTasks className="text-purple-600" /> Task Manager
        </h3>
        <div className="flex gap-2">
          <button
            onClick={fetchTasks}
            disabled={loading}
            className="text-purple-600 hover:text-purple-700 flex items-center gap-1 text-sm disabled:opacity-50"
          >
            <FaSync className={loading ? 'animate-spin' : ''} />
          </button>
          <button
            onClick={() => setShowCreateModal(true)}
            className="px-3 py-1.5 bg-purple-600 hover:bg-purple-700 text-white rounded-lg font-semibold flex items-center gap-1 text-sm"
          >
            <FaPlus /> Create Task
          </button>
        </div>
      </div>

      {/* Statistics */}
      <div className="grid grid-cols-3 gap-3 mb-6">
        <div className="bg-purple-50 rounded-lg p-3 text-center">
          <p className="text-2xl font-bold text-purple-600">{totalTasks}</p>
          <p className="text-xs text-gray-600">Total Tasks</p>
        </div>
        <div className="bg-green-50 rounded-lg p-3 text-center">
          <p className="text-2xl font-bold text-green-600">{completedTasks}</p>
          <p className="text-xs text-gray-600">Completed</p>
        </div>
        <div className="bg-yellow-50 rounded-lg p-3 text-center">
          <p className="text-2xl font-bold text-yellow-600">{pendingTasks}</p>
          <p className="text-xs text-gray-600">Pending</p>
        </div>
      </div>

      {error && (
        <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 flex items-center gap-2">
          <FaExclamationTriangle />
          {error}
        </div>
      )}

      {/* Task List */}
      {tasks.length === 0 ? (
        <div className="text-center py-8 text-gray-500">
          <FaTasks className="text-4xl mx-auto mb-3 text-gray-400" />
          <p>No tasks created yet</p>
          <p className="text-sm mt-1">Click "Create Task" to assign tasks to students</p>
        </div>
      ) : (
        <div className="space-y-3">
          {tasks.map((task) => (
            <div
              key={task.id}
              className={`border rounded-lg p-4 ${
                task.completed ? 'bg-gray-50 border-gray-200' : 'border-gray-200 hover:border-purple-300'
              }`}
            >
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-start gap-3">
                  <div className="mt-1">{getStatusIcon(task.completed)}</div>
                  <div>
                    <div className="flex items-center gap-2 flex-wrap">
                      <h4 className={`font-semibold ${task.completed ? 'text-gray-500' : 'text-gray-900'}`}>
                        {task.title}
                      </h4>
                      {getPriorityBadge(task.priority)}
                    </div>
                    {task.description && (
                      <p className={`text-sm mt-1 ${task.completed ? 'text-gray-400' : 'text-gray-600'}`}>
                        {task.description}
                      </p>
                    )}
                    <div className="flex items-center gap-4 mt-2 text-sm text-gray-500">
                      <span className="flex items-center gap-1">
                        <FaUser className="text-xs" />
                        {task.assignedToFirstName && task.assignedToLastName
                          ? `${task.assignedToFirstName} ${task.assignedToLastName}`
                          : task.assignedToUsername || 'Unassigned'}
                      </span>
                      <span>Due: {formatDate(task.deadline)}</span>
                    </div>
                  </div>
                </div>
                
                <div className="flex gap-2">
                  <button
                    onClick={() => setEditingTask(task)}
                    className="p-2 text-blue-600 hover:bg-blue-50 rounded"
                  >
                    <FaEdit />
                  </button>
                  <button
                    onClick={() => handleDelete(task.id)}
                    className="p-2 text-red-600 hover:bg-red-50 rounded"
                  >
                    <FaTrash />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Create/Edit Modal */}
      {(showCreateModal || editingTask) && (
        <TaskFormModal
          task={editingTask}
          onClose={() => {
            setShowCreateModal(false);
            setEditingTask(null);
          }}
          onSuccess={() => {
            setShowCreateModal(false);
            setEditingTask(null);
            fetchTasks();
          }}
        />
      )}
    </div>
  );
};

// Task Form Modal Component with Student Selector (UC 2.6)
const TaskFormModal = ({ task, onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    title: task?.title || '',
    description: task?.description || '',
    priority: task?.priority || 'MEDIUM',
    dueDate: task?.deadline ? task.deadline.split('T')[0] : '',
    assignedToId: task?.assignedToUserId || task?.assignedTo?.id || ''
  });
  const [students, setStudents] = useState([]);
  const [loadingStudents, setLoadingStudents] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  // UC 2.6 Step 4: Load students for selection
  useEffect(() => {
    const fetchStudents = async () => {
      try {
        const response = await userAPI.getAllStudents();
        setStudents(response.data || []);
      } catch (err) {
        console.error('Failed to load students:', err);
        setStudents([]);
      } finally {
        setLoadingStudents(false);
      }
    };
    fetchStudents();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.title.trim()) {
      setError('Title is required');
      return;
    }

    // Validate that a student is selected
    if (!formData.assignedToId) {
      setError('Please select a student to assign the task');
      return;
    }

    setSaving(true);
    setError(null);

    try {
      // Map frontend field names to backend expected names
      const taskPayload = {
        title: formData.title,
        description: formData.description,
        priority: formData.priority,
        deadline: formData.dueDate,  // Backend expects 'deadline'
        assignedToUserId: formData.assignedToId  // Backend expects 'assignedToUserId'
      };

      if (task) {
        await taskAPI.update(task.id, taskPayload);
      } else {
        await taskAPI.create(taskPayload);
      }
      onSuccess();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save task');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
        <div className="flex items-center justify-between p-4 border-b">
          <h3 className="text-lg font-bold text-gray-900">
            {task ? 'Edit Task' : 'Create New Task'}
          </h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <FaTimes />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-4">
          {/* Title */}
          <div className="mb-4">
            <label className="block text-sm font-semibold text-gray-700 mb-1">
              Title *
            </label>
            <input
              type="text"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:border-purple-500 focus:ring-1 focus:ring-purple-500"
              placeholder="Enter task title"
            />
          </div>

          {/* Description */}
          <div className="mb-4">
            <label className="block text-sm font-semibold text-gray-700 mb-1">
              Description
            </label>
            <textarea
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:border-purple-500 focus:ring-1 focus:ring-purple-500 resize-none"
              placeholder="Enter task description"
            />
          </div>

          {/* Priority */}
          <div className="mb-4">
            <label className="block text-sm font-semibold text-gray-700 mb-1">
              Priority
            </label>
            <select
              value={formData.priority}
              onChange={(e) => setFormData({ ...formData, priority: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:border-purple-500 focus:ring-1 focus:ring-purple-500"
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>

          {/* Due Date & Time */}
          <div className="mb-4">
            <label className="block text-sm font-semibold text-gray-700 mb-1">
              Due Date & Time
            </label>
            <input
              type="datetime-local"
              value={formData.dueDate}
              onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:border-purple-500 focus:ring-1 focus:ring-purple-500"
            />
          </div>

          {/* UC 2.6 Step 4: Student Selector (Assign To) */}
          <div className="mb-4">
            <label className="block text-sm font-semibold text-gray-700 mb-1">
              Assign to Student
            </label>
            {loadingStudents ? (
              <div className="flex items-center gap-2 text-gray-500 text-sm py-2">
                <FaSpinner className="animate-spin" />
                Loading students...
              </div>
            ) : (
              <select
                value={formData.assignedToId}
                onChange={(e) => setFormData({ ...formData, assignedToId: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:border-purple-500 focus:ring-1 focus:ring-purple-500"
              >
                <option value="">Select a student</option>
                {students.map((student) => (
                  <option key={student.id} value={student.id}>
                    {student.firstName} {student.lastName} ({student.username})
                  </option>
                ))}
              </select>
            )}
          </div>

          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
              {error}
            </div>
          )}

          <div className="flex gap-3">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 rounded-lg text-gray-700 font-semibold hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={saving}
              className="flex-1 px-4 py-2 bg-purple-600 hover:bg-purple-700 disabled:bg-purple-400 text-white font-semibold rounded-lg flex items-center justify-center gap-2"
            >
              {saving ? (
                <>
                  <FaSpinner className="animate-spin" />
                  Saving...
                </>
              ) : (
                <>
                  <FaCheck />
                  {task ? 'Update Task' : 'Create Task'}
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TaskManager;
