import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AiOutlineEye, AiOutlineEyeInvisible } from 'react-icons/ai';
import { FaFacebook, FaLinkedin, FaGoogle } from 'react-icons/fa';
import { useAuth } from '../context/AuthContext.jsx';

const AuthPage = () => {
  const navigate = useNavigate();
  const { login, register, error, setError } = useAuth();

  const [isLogin, setIsLogin] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    role: 'STUDENT',
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    if (error) setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (isLogin) {
        // Login
        const response = await login({
          username: formData.username,
          password: formData.password,
        });
        console.log('Login response:', response);
        console.log('Navigating to dashboard...');
        navigate('/dashboard');
      } else {
        // Register - store credentials before clearing form
        const registrationUsername = formData.username;
        const registrationPassword = formData.password;
        
        await register({
          username: registrationUsername,
          email: formData.email,
          password: registrationPassword,
          firstName: formData.firstName,
          lastName: formData.lastName,
          role: formData.role,
        });
        
        // Clear form and switch to login
        setFormData({
          username: '',
          email: '',
          password: '',
          firstName: '',
          lastName: '',
          role: 'STUDENT',
        });
        setIsLogin(true);
        
        // Auto-login after registration with stored credentials
        const response = await login({
          username: registrationUsername,
          password: registrationPassword,
        });
        console.log('Registration + auto-login response:', response);
        navigate('/dashboard');
      }
    } catch (err) {
      console.error('Auth error:', err);
      setError(err.response?.data?.message || err.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-500 via-purple-400 to-blue-300 flex items-center justify-center p-4">
      {/* Animated background shapes */}
      <div className="absolute top-0 left-0 w-96 h-96 bg-purple-300 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob"></div>
      <div className="absolute top-0 right-0 w-96 h-96 bg-blue-300 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-2000"></div>
      <div className="absolute -bottom-8 left-20 w-96 h-96 bg-pink-300 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-4000"></div>

      <style>{`
        @keyframes blob {
          0%, 100% { transform: translate(0, 0) scale(1); }
          33% { transform: translate(30px, -50px) scale(1.1); }
          66% { transform: translate(-20px, 20px) scale(0.9); }
        }
        .animate-blob {
          animation: blob 7s infinite;
        }
        .animation-delay-2000 {
          animation-delay: 2s;
        }
        .animation-delay-4000 {
          animation-delay: 4s;
        }
      `}</style>

      {/* Main container */}
      <div className="w-full max-w-5xl">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 items-center">
          {/* Left side - Illustration and branding */}
          <div className="hidden lg:flex flex-col items-center justify-center text-white">
            <div className="w-full h-80 bg-gradient-to-br from-blue-400 to-purple-500 rounded-3xl shadow-2xl flex items-center justify-center p-8 relative overflow-hidden">
              {/* Illustration placeholder with icons */}
              <div className="relative z-10 text-center">
                <div className="text-6xl mb-4">✓</div>
                <div className="w-64 h-40 bg-white bg-opacity-20 rounded-2xl backdrop-blur-sm mb-4 flex items-center justify-center">
                  <div className="text-center">
                    <div className="text-4xl mb-2">📋</div>
                    <p className="text-sm">Project Plans</p>
                  </div>
                </div>
                <div className="flex gap-2 justify-center">
                  <div className="w-12 h-12 bg-white bg-opacity-10 rounded-lg"></div>
                  <div className="w-12 h-12 bg-white bg-opacity-10 rounded-lg"></div>
                  <div className="w-12 h-12 bg-white bg-opacity-10 rounded-lg"></div>
                </div>
              </div>
            </div>
            <h1 className="text-4xl font-bold mt-8 text-shadow">SPMP</h1>
            <p className="text-lg text-purple-100 mt-2">Evaluator</p>
          </div>

          {/* Right side - Form */}
          <div className="w-full">
            <div className="bg-white rounded-3xl shadow-2xl p-8 md:p-10">
              {/* Header */}
              <div className="text-center mb-8">
                <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-2">
                  {isLogin ? 'Welcome Back!' : 'Welcome to SPMP Evaluator!'}
                </h2>
                <p className="text-gray-600">
                  {isLogin
                    ? 'Sign in to your account'
                    : 'Register your account'}
                </p>
              </div>

              {/* Error message */}
              {error && (
                <div className="mb-4 p-4 bg-red-50 border-l-4 border-red-500 rounded-lg">
                  <p className="text-red-700 font-semibold text-sm">{error}</p>
                  {isLogin && error.toLowerCase().includes('invalid') && (
                    <p className="text-red-600 text-xs mt-1">Please check your credentials and try again.</p>
                  )}
                </div>
              )}

              {/* Form */}
              <form onSubmit={handleSubmit} className="space-y-5">
                {!isLogin && (
                  <>
                    {/* Name fields */}
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          First Name
                        </label>
                        <input
                          type="text"
                          name="firstName"
                          value={formData.firstName}
                          onChange={handleInputChange}
                          placeholder="John"
                          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                          required
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Last Name
                        </label>
                        <input
                          type="text"
                          name="lastName"
                          value={formData.lastName}
                          onChange={handleInputChange}
                          placeholder="Doe"
                          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                          required
                        />
                      </div>
                    </div>

                    {/* Email for signup */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Email
                      </label>
                      <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleInputChange}
                        placeholder="you@example.com"
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                        required
                      />
                    </div>

                    {/* Role selection */}
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Role
                      </label>
                      <select
                        name="role"
                        value={formData.role}
                        onChange={handleInputChange}
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition bg-white"
                      >
                        <option value="STUDENT">Student</option>
                        <option value="PROFESSOR">Professor</option>
                      </select>
                    </div>
                  </>
                )}

                {/* Username */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    {isLogin ? 'Username or Email' : 'Username'}
                  </label>
                  <input
                    type="text"
                    name="username"
                    value={formData.username}
                    onChange={handleInputChange}
                    placeholder={isLogin ? 'Enter your username' : 'Choose a username'}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                    required
                  />
                </div>

                {/* Password */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Password
                  </label>
                  <div className="relative">
                    <input
                      type={showPassword ? 'text' : 'password'}
                      name="password"
                      value={formData.password}
                      onChange={handleInputChange}
                      placeholder={isLogin ? 'Enter your password' : '8+ characters'}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition pr-10"
                      required
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                    >
                      {showPassword ? (
                        <AiOutlineEyeInvisible size={20} />
                      ) : (
                        <AiOutlineEye size={20} />
                      )}
                    </button>
                  </div>
                </div>

                {/* Login forgot password */}
                {isLogin && (
                  <div className="flex justify-end">
                    <a
                      href="#"
                      className="text-sm text-purple-600 hover:text-purple-700 font-medium"
                    >
                      Forgot password?
                    </a>
                  </div>
                )}

                {/* Submit button */}
                <button
                  type="submit"
                  disabled={loading}
                  className="w-full bg-gradient-to-r from-purple-600 to-purple-700 hover:from-purple-700 hover:to-purple-800 text-white font-semibold py-3 rounded-full transition transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
                >
                  {loading
                    ? 'Processing...'
                    : isLogin
                    ? 'Login'
                    : 'Create Account'}
                </button>

                {/* Social login */}
                <div className="relative mt-6">
                  <div className="absolute inset-0 flex items-center">
                    <div className="w-full border-t border-gray-300"></div>
                  </div>
                  <div className="relative flex justify-center text-sm">
                    <span className="px-2 bg-white text-gray-500">
                      {isLogin ? 'Or login with' : 'Or create account with'}
                    </span>
                  </div>
                </div>

                <div className="flex gap-4 mt-6 justify-center">
                  <button
                    type="button"
                    className="w-12 h-12 flex items-center justify-center rounded-full border-2 border-gray-300 hover:border-purple-500 hover:bg-purple-50 transition"
                  >
                    <FaFacebook className="text-blue-600" size={20} />
                  </button>
                  <button
                    type="button"
                    className="w-12 h-12 flex items-center justify-center rounded-full border-2 border-gray-300 hover:border-purple-500 hover:bg-purple-50 transition"
                  >
                    <FaLinkedin className="text-blue-700" size={20} />
                  </button>
                  <button
                    type="button"
                    className="w-12 h-12 flex items-center justify-center rounded-full border-2 border-gray-300 hover:border-purple-500 hover:bg-purple-50 transition"
                  >
                    <FaGoogle className="text-red-600" size={20} />
                  </button>
                </div>

                {/* Toggle login/signup */}
                <div className="text-center mt-6 pt-6 border-t border-gray-200">
                  <p className="text-gray-700">
                    {isLogin ? "Don't have an account?" : 'Already have an account?'}{' '}
                    <button
                      type="button"
                      onClick={() => {
                        setIsLogin(!isLogin);
                        setError(null);
                        setFormData({
                          username: '',
                          email: '',
                          password: '',
                          firstName: '',
                          lastName: '',
                          role: 'STUDENT',
                        });
                      }}
                      className="text-purple-600 hover:text-purple-700 font-semibold"
                    >
                      {isLogin ? 'Sign up' : 'Sign in'}
                    </button>
                  </p>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;
