import React, { useState, useEffect } from 'react';
import { 
  FaTasks, FaCheck, FaClock, FaSpinner, FaCalendar, 
  FaExclamationTriangle, FaSync, FaFilter
} from 'react-icons/fa';
import { taskAPI } from '../../services/apiService';

const TaskTracker = ({ refreshTrigger }) => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('all'); // 'all', 'pending', 'completed'
  const [updatingTaskId, setUpdatingTaskId] = useState(null);

  const fetchTasks = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await taskAPI.getMyTasks();
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

  const handleCompleteTask = async (taskId) => {
    setUpdatingTaskId(taskId);
    try {
      await taskAPI.complete(taskId);
      await fetchTasks();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to complete task');
    } finally {
      setUpdatingTaskId(null);
    }
  };

  const filteredTasks = tasks.filter((task) => {
    if (filter === 'pending') return !task.completed;
    if (filter === 'completed') return task.completed;
    return true;
  });

  const getPriorityBadge = (priority) => {
    const colors = {
      HIGH: 'bg-red-100 text-red-800 border-red-300',
      MEDIUM: 'bg-yellow-100 text-yellow-800 border-yellow-300',
      LOW: 'bg-green-100 text-green-800 border-green-300'
    };
    return (
      <span className={`px-2 py-0.5 text-xs font-semibold rounded border ${colors[priority] || colors.MEDIUM}`}>
        {priority}
      </span>
    );
  };

  const formatDueDate = (dateString) => {
    if (!dateString) return 'No due date';
    const date = new Date(dateString);
    const now = new Date();
    const diffDays = Math.ceil((date - now) / (1000 * 60 * 60 * 24));
    
    const formatted = date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });

    if (diffDays < 0) {
      return <span className="text-red-600 font-semibold">{formatted} (Overdue)</span>;
    } else if (diffDays <= 3) {
      return <span className="text-yellow-600 font-semibold">{formatted} ({diffDays} days left)</span>;
    }
    return formatted;
  };

  const getStatusIcon = (status, completed) => {
    if (completed) return <FaCheck className="text-green-500" />;
    if (status === 'IN_PROGRESS') return <FaSpinner className="text-blue-500 animate-spin" />;
    return <FaClock className="text-gray-400" />;
  };

  // Statistics
  const totalTasks = tasks.length;
  const completedTasks = tasks.filter(t => t.completed).length;
  const pendingTasks = totalTasks - completedTasks;
  const overdueTasks = tasks.filter(t => !t.completed && t.dueDate && new Date(t.dueDate) < new Date()).length;

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-lg p-8 text-center">
        <FaSpinner className="animate-spin text-purple-600 text-3xl mx-auto mb-3" />
        <p className="text-gray-600">Loading tasks...</p>
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
          onClick={fetchTasks}
          className="text-purple-600 hover:text-purple-700 font-semibold flex items-center gap-2 mx-auto"
        >
          <FaSync /> Retry
        </button>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      <div className="flex justify-between items-center mb-6">
        <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2">
          <FaTasks className="text-purple-600" /> My Tasks
        </h3>
        <button
          onClick={fetchTasks}
          className="text-purple-600 hover:text-purple-700 flex items-center gap-1 text-sm"
        >
          <FaSync /> Refresh
        </button>
      </div>

      {/* Statistics Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-6">
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
        <div className="bg-red-50 rounded-lg p-3 text-center">
          <p className="text-2xl font-bold text-red-600">{overdueTasks}</p>
          <p className="text-xs text-gray-600">Overdue</p>
        </div>
      </div>

      {/* Filter Buttons */}
      <div className="flex gap-2 mb-4">
        <button
          onClick={() => setFilter('all')}
          className={`px-3 py-1.5 text-sm rounded-lg font-semibold transition ${
            filter === 'all' 
              ? 'bg-purple-600 text-white' 
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          }`}
        >
          All ({totalTasks})
        </button>
        <button
          onClick={() => setFilter('pending')}
          className={`px-3 py-1.5 text-sm rounded-lg font-semibold transition ${
            filter === 'pending' 
              ? 'bg-yellow-500 text-white' 
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          }`}
        >
          Pending ({pendingTasks})
        </button>
        <button
          onClick={() => setFilter('completed')}
          className={`px-3 py-1.5 text-sm rounded-lg font-semibold transition ${
            filter === 'completed' 
              ? 'bg-green-500 text-white' 
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          }`}
        >
          Completed ({completedTasks})
        </button>
      </div>

      {/* Task List */}
      {filteredTasks.length === 0 ? (
        <div className="text-center py-8 text-gray-500">
          <FaTasks className="text-4xl mx-auto mb-3 text-gray-400" />
          <p>No tasks found</p>
        </div>
      ) : (
        <div className="space-y-3">
          {filteredTasks.map((task) => (
            <div
              key={task.id}
              className={`border rounded-lg p-4 transition ${
                task.completed 
                  ? 'bg-gray-50 border-gray-200' 
                  : 'border-gray-200 hover:border-purple-300'
              }`}
            >
              <div className="flex items-start gap-3">
                {/* Status Icon / Checkbox */}
                <button
                  onClick={() => !task.completed && handleCompleteTask(task.id)}
                  disabled={task.completed || updatingTaskId === task.id}
                  className={`mt-1 w-5 h-5 rounded border-2 flex items-center justify-center transition ${
                    task.completed 
                      ? 'bg-green-500 border-green-500 text-white cursor-default' 
                      : 'border-gray-400 hover:border-purple-500 cursor-pointer'
                  }`}
                >
                  {updatingTaskId === task.id ? (
                    <FaSpinner className="animate-spin text-xs" />
                  ) : task.completed ? (
                    <FaCheck className="text-xs" />
                  ) : null}
                </button>

                <div className="flex-1">
                  <div className="flex items-start justify-between gap-2">
                    <h4 className={`font-semibold ${task.completed ? 'text-gray-500 line-through' : 'text-gray-900'}`}>
                      {task.title}
                    </h4>
                    {getPriorityBadge(task.priority)}
                  </div>
                  
                  {task.description && (
                    <p className={`text-sm mt-1 ${task.completed ? 'text-gray-400' : 'text-gray-600'}`}>
                      {task.description}
                    </p>
                  )}

                  <div className="flex items-center gap-4 mt-2 text-sm">
                    <span className="flex items-center gap-1 text-gray-500">
                      <FaCalendar className="text-xs" />
                      {formatDueDate(task.dueDate)}
                    </span>
                    {task.status && (
                      <span className="flex items-center gap-1 text-gray-500">
                        {getStatusIcon(task.status, task.completed)}
                        {task.status.replace('_', ' ')}
                      </span>
                    )}
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default TaskTracker;
