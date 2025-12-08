import React, { useState, useEffect, useRef } from 'react';
import { notificationAPI } from '../services/apiService';

/**
 * NotificationBell Component
 * 
 * Displays a notification bell icon with unread count badge.
 * Clicking opens a dropdown showing recent notifications.
 * 
 * Use Cases:
 * - UC 2.8: Student receives notification when professor overrides AI score
 * - UC 2.9: Student receives notification when professor updates task
 */
const NotificationBell = () => {
  const [notifications, setNotifications] = useState([]);
  const [allNotifications, setAllNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isOpen, setIsOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [showHistory, setShowHistory] = useState(false);
  const dropdownRef = useRef(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Fetch notifications on mount and periodically
  useEffect(() => {
    fetchNotifications();
    const interval = setInterval(fetchNotifications, 30000); // Poll every 30 seconds
    return () => clearInterval(interval);
  }, []);

  const fetchNotifications = async () => {
    try {
      const [notifResponse, countResponse] = await Promise.all([
        notificationAPI.getUnreadNotifications(),
        notificationAPI.getUnreadCount()
      ]);
      setNotifications(notifResponse.data || []);
      setUnreadCount(countResponse.data?.unreadCount || 0);
    } catch (error) {
      console.error('Failed to fetch notifications:', error);
    }
  };

  const fetchAllNotifications = async () => {
    try {
      setLoading(true);
      const response = await notificationAPI.getMyNotifications();
      setAllNotifications(response.data || []);
    } catch (error) {
      console.error('Failed to fetch notification history:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleViewHistory = () => {
    setShowHistory(true);
    fetchAllNotifications();
  };

  const handleBackToUnread = () => {
    setShowHistory(false);
  };

  const handleMarkAsRead = async (notificationId) => {
    try {
      await notificationAPI.markAsRead(notificationId);
      setNotifications(notifications.filter(n => n.id !== notificationId));
      setUnreadCount(Math.max(0, unreadCount - 1));
    } catch (error) {
      console.error('Failed to mark notification as read:', error);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      setLoading(true);
      await notificationAPI.markAllAsRead();
      setNotifications([]);
      setUnreadCount(0);
    } catch (error) {
      console.error('Failed to mark all notifications as read:', error);
    } finally {
      setLoading(false);
    }
  };

  const getNotificationIcon = (type) => {
    switch (type) {
      case 'SCORE_OVERRIDE':
        return '📊';
      case 'TASK_UPDATED':
        return '📝';
      case 'TASK_ASSIGNED':
        return '📋';
      case 'FEEDBACK_AVAILABLE':
        return '💬';
      default:
        return '🔔';
    }
  };

  const formatTime = (timestamp) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now - date;
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    return date.toLocaleDateString();
  };

  return (
    <div className="relative" ref={dropdownRef}>
      {/* Bell Icon Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="relative p-2 text-gray-600 hover:text-blue-600 hover:bg-gray-100 rounded-full transition-colors"
        title="Notifications"
      >
        <svg 
          xmlns="http://www.w3.org/2000/svg" 
          className="h-6 w-6" 
          fill="none" 
          viewBox="0 0 24 24" 
          stroke="currentColor"
        >
          <path 
            strokeLinecap="round" 
            strokeLinejoin="round" 
            strokeWidth={2} 
            d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" 
          />
        </svg>
        
        {/* Unread Badge */}
        {unreadCount > 0 && (
          <span className="absolute top-0 right-0 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-white transform translate-x-1/2 -translate-y-1/2 bg-red-500 rounded-full">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}
      </button>

      {/* Dropdown */}
      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg border border-gray-200 z-50">
          {/* Header */}
          <div className="px-4 py-3 border-b border-gray-200 flex justify-between items-center">
            <div className="flex items-center gap-2">
              {showHistory && (
                <button
                  onClick={handleBackToUnread}
                  className="text-gray-500 hover:text-gray-700"
                  title="Back to unread"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                  </svg>
                </button>
              )}
              <h3 className="font-semibold text-gray-800">
                {showHistory ? 'Notification History' : 'Notifications'}
              </h3>
            </div>
            {!showHistory && notifications.length > 0 && (
              <button
                onClick={handleMarkAllAsRead}
                disabled={loading}
                className="text-sm text-blue-600 hover:text-blue-800 disabled:opacity-50"
              >
                Mark all read
              </button>
            )}
          </div>

          {/* Notification List */}
          <div className="max-h-96 overflow-y-auto">
            {loading ? (
              <div className="px-4 py-8 text-center text-gray-500">
                <svg className="animate-spin mx-auto h-8 w-8 text-purple-600 mb-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <p>Loading...</p>
              </div>
            ) : showHistory ? (
              // Show all notifications (history)
              allNotifications.length === 0 ? (
                <div className="px-4 py-8 text-center text-gray-500">
                  <svg 
                    className="mx-auto h-12 w-12 text-gray-300 mb-2" 
                    fill="none" 
                    viewBox="0 0 24 24" 
                    stroke="currentColor"
                  >
                    <path 
                      strokeLinecap="round" 
                      strokeLinejoin="round" 
                      strokeWidth={1} 
                      d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" 
                    />
                  </svg>
                  <p>No notification history</p>
                </div>
              ) : (
                allNotifications.map((notification) => (
                  <div
                    key={notification.id}
                    className={`px-4 py-3 border-b border-gray-100 ${
                      notification.read ? 'bg-gray-50' : 'bg-blue-50'
                    }`}
                  >
                    <div className="flex items-start gap-3">
                      <span className="text-xl">
                        {getNotificationIcon(notification.type)}
                      </span>
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2">
                          <p className={`text-sm font-medium ${notification.read ? 'text-gray-600' : 'text-gray-800'}`}>
                            {notification.title}
                          </p>
                          {!notification.read && (
                            <span className="w-2 h-2 bg-blue-500 rounded-full"></span>
                          )}
                        </div>
                        <p className={`text-sm truncate ${notification.read ? 'text-gray-500' : 'text-gray-600'}`}>
                          {notification.message}
                        </p>
                        <p className="text-xs text-gray-400 mt-1">
                          {formatTime(notification.createdAt)}
                        </p>
                      </div>
                    </div>
                  </div>
                ))
              )
            ) : (
              // Show unread notifications
              notifications.length === 0 ? (
                <div className="px-4 py-8 text-center text-gray-500">
                  <svg 
                    className="mx-auto h-12 w-12 text-gray-300 mb-2" 
                    fill="none" 
                    viewBox="0 0 24 24" 
                    stroke="currentColor"
                  >
                    <path 
                      strokeLinecap="round" 
                      strokeLinejoin="round" 
                      strokeWidth={1} 
                      d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" 
                    />
                  </svg>
                  <p>No new notifications</p>
                </div>
              ) : (
                notifications.map((notification) => (
                  <div
                    key={notification.id}
                    className="px-4 py-3 border-b border-gray-100 hover:bg-gray-50 cursor-pointer"
                    onClick={() => handleMarkAsRead(notification.id)}
                  >
                    <div className="flex items-start gap-3">
                      <span className="text-xl">
                        {getNotificationIcon(notification.type)}
                      </span>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-gray-800">
                          {notification.title}
                        </p>
                        <p className="text-sm text-gray-600 truncate">
                          {notification.message}
                        </p>
                        <p className="text-xs text-gray-400 mt-1">
                          {formatTime(notification.createdAt)}
                        </p>
                      </div>
                    </div>
                  </div>
                ))
              )
            )}
          </div>

          {/* Footer - View History */}
          {!showHistory && (
            <div className="px-4 py-2 border-t border-gray-200 text-center">
              <button 
                className="text-sm text-blue-600 hover:text-blue-800 flex items-center gap-1 mx-auto"
                onClick={handleViewHistory}
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                View History
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default NotificationBell;
