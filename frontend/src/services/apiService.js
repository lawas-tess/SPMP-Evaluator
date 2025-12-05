import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to all requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Handle response errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Authentication API
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  validateToken: () => api.get('/auth/validate'),
  health: () => api.get('/auth/health'),
};

// Document API (Module 2: Use Cases 2.1-2.4)
export const documentAPI = {
  // UC 2.1: File Upload
  upload: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/documents/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  
  // UC 2.2: File Edit/Replace
  replace: (documentId, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.put(`/documents/${documentId}/replace`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  
  // UC 2.3: File Removal
  delete: (documentId) => api.delete(`/documents/${documentId}`),
  
  // UC 2.4: View Feedback
  getReport: (documentId) => api.get(`/documents/${documentId}/report`),
  
  // Other document operations
  getMyDocuments: () => api.get('/documents/my-documents'),
  getDocument: (documentId) => api.get(`/documents/${documentId}`),
  evaluate: (documentId) => api.post(`/documents/${documentId}/evaluate`),
  getEvaluated: () => api.get('/documents/evaluated'),
  getPending: () => api.get('/documents/pending'),
  addNotes: (documentId, notes) => api.put(`/documents/${documentId}/notes`, notes),
  
  // UC 2.7: Submission Tracker (Professor)
  getAllSubmissions: (status, studentId) => {
    const params = new URLSearchParams();
    if (status) params.append('status', status);
    if (studentId) params.append('studentId', studentId);
    return api.get(`/documents/all-submissions?${params.toString()}`);
  },
  
  // UC 2.8: Override AI Results (Professor)
  overrideScore: (documentId, score, notes = '') => 
    api.put(`/documents/${documentId}/override-score`, { score, notes }),
};

// Task API (Module 2: Use Cases 2.5-2.6, 2.9)
export const taskAPI = {
  // UC 2.6: Task Creation (Professor)
  create: (taskData) => api.post('/tasks/create', taskData),
  
  // UC 2.5: Task Tracking (Student)
  getMyTasks: () => api.get('/tasks/my'),
  getIncompleteTasks: () => api.get('/tasks/incomplete'),
  
  // UC 2.9: Update Tasks (Professor)
  update: (taskId, taskData) => api.put(`/tasks/${taskId}`, taskData),
  updateStatus: (taskId, status, completed = false) => 
    api.put(`/tasks/${taskId}/status?status=${status}&completed=${completed}`),
  complete: (taskId) => api.put(`/tasks/${taskId}/complete`),
  
  // Other task operations
  getCreatedTasks: () => api.get('/tasks/created'),
  getTask: (taskId) => api.get(`/tasks/${taskId}`),
  delete: (taskId) => api.delete(`/tasks/${taskId}`),
};

// Reports API (Module 2: Use Case 2.10)
export const reportAPI = {
  // UC 2.10: Monitor Student Progress (Professor)
  getStudentProgress: (userId) => api.get(`/reports/student-progress/${userId}`),
  getStudentPerformance: (userId) => api.get(`/reports/student-performance/${userId}`),
  
  // Other report operations
  getComplianceStatistics: () => api.get('/reports/compliance-statistics'),
  getComplianceTrends: (daysBack = 30) => api.get(`/reports/compliance-trends?daysBack=${daysBack}`),
};

// User API (Module 2: Use Cases 2.6, 2.10)
export const userAPI = {
  // UC 2.6: Get students for task assignment
  // UC 2.10: Get student list for progress monitoring
  getAllStudents: () => api.get('/users/students'),
  getAllProfessors: () => api.get('/users/professors'),
  getAllUsers: () => api.get('/users'),
  getUserById: (userId) => api.get(`/users/${userId}`),
  toggleUserStatus: (userId, enabled) => api.put(`/users/${userId}/status?enabled=${enabled}`),
};

// Grading Criteria API (Module 2: Use Case 2.7)
export const gradingCriteriaAPI = {
  // UC 2.7: Set Grading Criteria (Professor)
  create: (criteriaData) => api.post('/grading-criteria', criteriaData),
  update: (criteriaId, criteriaData) => api.put(`/grading-criteria/${criteriaId}`, criteriaData),
  delete: (criteriaId) => api.delete(`/grading-criteria/${criteriaId}`),
  getMyCriteria: () => api.get('/grading-criteria'),
  getActiveCriteria: () => api.get('/grading-criteria/active'),
  getCriteriaById: (criteriaId) => api.get(`/grading-criteria/${criteriaId}`),
  setActiveCriteria: (criteriaId) => api.put(`/grading-criteria/${criteriaId}/activate`),
  getDefaultCriteria: () => api.get('/grading-criteria/default'),
};

export default api;
