import React from 'react';
import { useAuth } from '../context/AuthContext.jsx';
import NotificationBell from './NotificationBell';

const Navbar = () => {
  const { user, logout } = useAuth();

  return (
    <nav className="bg-white shadow-lg sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-purple-600">SPMP</h1>
          <p className="text-sm text-gray-600">Evaluator</p>
        </div>
        <div className="flex items-center gap-6">
          <span className="text-gray-700">
            Welcome, <strong>{user?.firstName} {user?.lastName}</strong>
          </span>
          <span className="bg-purple-100 text-purple-700 px-3 py-1 rounded-full text-sm font-medium">
            {user?.role}
          </span>
          
          {/* Notification Bell - UC 2.8, 2.9: Student notification alerts */}
          <NotificationBell />
          
          <button
            onClick={logout}
            className="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded-lg transition"
          >
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
