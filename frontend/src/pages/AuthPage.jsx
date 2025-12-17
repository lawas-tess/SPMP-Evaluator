import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { AiOutlineEye, AiOutlineEyeInvisible } from 'react-icons/ai';
import { FaBolt, FaChartBar, FaComment } from 'react-icons/fa';
import { forgotPassword, resetPassword } from '../services/apiService';

// Updated to use the local image from the public folder
const DASHBOARD_IMAGE = "/AuthBackground.png";

const AuthPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { login, register, error, setError, isAuthenticated, loading: authLoading } = useAuth();

  // View state: 'login' | 'register' | 'forgot' | 'reset'
  const [view, setView] = useState('login');
  
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  // Form Data
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    role: 'STUDENT',
  });

  // Redirect to dashboard if already authenticated
  useEffect(() => {
    if (!authLoading && isAuthenticated) {
      navigate('/dashboard', { replace: true });
    }
  }, [isAuthenticated, authLoading, navigate]);

  // Check for Reset Token in URL
  useEffect(() => {
    const token = searchParams.get('token');
    if (token) {
      setView('reset');
    }
  }, [searchParams]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    if (error) setError(null);
  };

  // 1. Handle Login or Register
  const handleAuthSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (view === 'login') {
        // Login
        await login({
          username: formData.username,
          password: formData.password,
        });
        navigate('/dashboard', { replace: true });
      } else if (view === 'register') {
        // Register
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
        
        // Reset form but keep credentials for auto-login
        setFormData({ ...formData, role: 'STUDENT' });
        
        // Auto-login
        await login({
          username: registrationUsername,
          password: registrationPassword,
        });
        navigate('/dashboard', { replace: true });
      }
    } catch (err) {
      console.error('Auth error:', err);
      setError(err.response?.data?.message || err.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  // 2. Handle Forgot Password (Send Email)
  const handleForgotSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setMessage('');
    try {
      await forgotPassword(formData.email);
      setMessage('Password reset link sent! Please check your email.');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to send reset email.');
    } finally {
      setLoading(false);
    }
  };

  // 3. Handle Reset Password (New Password)
  const handleResetSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const token = searchParams.get('token');
      await resetPassword(token, formData.password);
      setMessage('Password successfully reset! You can now login.');
      setTimeout(() => {
        setView('login');
        setMessage('');
        // Clear URL params
        navigate('/', { replace: true });
      }, 3000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to reset password.');
    } finally {
      setLoading(false);
    }
  };

  // Decide which submit handler to use
  const getSubmitHandler = () => {
    if (view === 'forgot') return handleForgotSubmit;
    if (view === 'reset') return handleResetSubmit;
    return handleAuthSubmit;
  };

  return (
    <div className="h-screen flex bg-slate-50 font-sans text-slate-900 overflow-hidden">
      
      {/* ==========================================
          LEFT SIDE: Branding Panel
         ========================================== */}
      <div className="hidden lg:flex lg:flex-1 relative h-full overflow-hidden bg-slate-900">
        <div className="absolute inset-0 bg-gradient-to-br from-indigo-900 via-purple-900 to-blue-900" />
        <div className="absolute inset-0 opacity-30 mix-blend-overlay">
          <img src={DASHBOARD_IMAGE} alt="SPMP Analytics Dashboard" className="w-full h-full object-cover" />
        </div>
        <div className="absolute top-1/4 -left-20 w-96 h-96 bg-purple-600 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob" />
        <div className="absolute bottom-1/4 -right-20 w-96 h-96 bg-blue-600 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-2000" />
        
        <div className="relative z-10 flex flex-col justify-center px-12 py-16 text-white w-full max-w-2xl mx-auto">
          <div className="mb-10">
            <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/10 backdrop-blur-sm border border-white/20 mb-6 shadow-sm">
              <div className="w-2 h-2 bg-green-400 rounded-full shadow-[0_0_8px_rgba(74,222,128,0.5)]" />
              <span className="text-sm font-medium tracking-wide">IEEE 1058 Standard Compliant</span>
            </div>
            <h1 className="text-5xl font-bold mb-6 tracking-tight leading-tight">Software Project Management Plan Evaluator</h1>
            <p className="text-lg text-indigo-100 leading-relaxed max-w-md">Streamline your Software Project Management Plan assessment with our automated analysis and grading system.</p>
          </div>
          
          <div className="grid grid-cols-3 gap-4 max-w-lg">
            {[
              { label: 'Automated Grading', icon: FaBolt, gradient: 'from-yellow-400 to-orange-500' },
              { label: 'Detailed Analytics', icon: FaChartBar, gradient: 'from-blue-400 to-blue-600' },
              { label: 'Instant Feedback', icon: FaComment, gradient: 'from-purple-400 to-purple-600' }
            ].map((item, i) => (
              <div key={i} className="flex flex-col items-center text-center p-4 rounded-xl bg-white/5 backdrop-blur-md border border-white/10 shadow-lg hover:bg-white/10 transition duration-300 transform hover:-translate-y-1">
                <div className={`w-12 h-12 rounded-lg bg-gradient-to-br ${item.gradient} flex items-center justify-center mb-3 shadow-inner`}>
                  <item.icon className="w-5 h-5 text-white" />
                </div>
                <span className="text-sm font-medium text-white/90">{item.label}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* ==========================================
          RIGHT SIDE: Authentication Form
         ========================================== */}
      <div className="w-full lg:flex-1 h-full overflow-y-auto bg-white relative scrollbar-thin scrollbar-thumb-slate-200 scrollbar-track-transparent">
        <div className="min-h-full flex items-center justify-center p-4 sm:p-8">
          <div className="w-full max-w-md space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
            
            {/* Header Text Changes based on View */}
            <div className="mb-8">
              <h2 className="text-3xl font-bold text-gray-900 tracking-tight mb-2">
                {view === 'login' ? 'Welcome back' : 
                 view === 'register' ? 'Create an account' :
                 view === 'forgot' ? 'Reset Password' : 'New Password'}
              </h2>
              <p className="text-slate-500 text-lg">
                {view === 'login' ? 'Enter your details to access your dashboard.' :
                 view === 'register' ? 'Get started with your project evaluation journey.' :
                 view === 'forgot' ? 'Enter your email to receive a reset link.' :
                 'Enter your new password below.'}
              </p>
            </div>

            {/* Error Message */}
            {error && (
              <div className="p-4 rounded-xl bg-red-50 border border-red-100 flex items-start gap-3">
                <div className="flex-shrink-0 w-5 h-5 text-red-500 mt-0.5">
                  <svg fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                </div>
                <div>
                  <p className="text-sm font-medium text-red-800">{error}</p>
                </div>
              </div>
            )}

            {/* Success Message */}
            {message && (
              <div className="p-4 rounded-xl bg-green-50 border border-green-100 text-green-700 font-medium">
                {message}
              </div>
            )}

            <form onSubmit={getSubmitHandler()} className="space-y-5">

              {/* === REGISTER FIELDS === */}
              {view === 'register' && (
                <div className="space-y-5 animate-in slide-in-from-top-2 duration-300">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-2">First Name</label>
                      <input type="text" name="firstName" value={formData.firstName} onChange={handleInputChange} className="w-full px-4 py-3 rounded-lg border border-slate-200 focus:outline-none focus:ring-2 focus:ring-purple-500 bg-slate-50 focus:bg-white" placeholder="John" required />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-slate-700 mb-2">Last Name</label>
                      <input type="text" name="lastName" value={formData.lastName} onChange={handleInputChange} className="w-full px-4 py-3 rounded-lg border border-slate-200 focus:outline-none focus:ring-2 focus:ring-purple-500 bg-slate-50 focus:bg-white" placeholder="Doe" required />
                    </div>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-2">Email Address</label>
                    <input type="email" name="email" value={formData.email} onChange={handleInputChange} className="w-full px-4 py-3 rounded-lg border border-slate-200 focus:outline-none focus:ring-2 focus:ring-purple-500 bg-slate-50 focus:bg-white" placeholder="you@university.edu" required />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-2">Role</label>
                    <div className="relative">
                      <select name="role" value={formData.role} onChange={handleInputChange} className="w-full px-4 py-3 rounded-lg border border-slate-200 focus:outline-none focus:ring-2 focus:ring-purple-500 bg-slate-50 focus:bg-white appearance-none cursor-pointer">
                        <option value="STUDENT">Student</option>
                        <option value="PROFESSOR">Professor</option>
                      </select>
                    </div>
                  </div>
                </div>
              )}

              {/* === FORGOT PASSWORD FIELDS === */}
              {view === 'forgot' && (
                <div>
                   <label className="block text-sm font-medium text-slate-700 mb-2">Email Address</label>
                   <input type="email" name="email" value={formData.email} onChange={handleInputChange} className="w-full px-4 py-3 rounded-lg border border-slate-200 focus:outline-none focus:ring-2 focus:ring-purple-500 bg-slate-50 focus:bg-white" placeholder="you@university.edu" required />
                   <button type="button" onClick={() => { setView('login'); setError(null); }} className="text-sm text-slate-500 hover:text-slate-700 underline mt-4">Back to Sign In</button>
                </div>
              )}

              {/* === LOGIN / REGISTER USERNAME FIELD === */}
              {(view === 'login' || view === 'register') && (
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">{view === 'login' ? 'Username or Email' : 'Username'}</label>
                  <input type="text" name="username" value={formData.username} onChange={handleInputChange} className="w-full px-4 py-3 rounded-lg border border-slate-200 focus:outline-none focus:ring-2 focus:ring-purple-500 bg-slate-50 focus:bg-white" placeholder={view === 'login' ? 'Enter your username' : 'Choose a username'} required />
                </div>
              )}

              {/* === PASSWORD FIELD (Used in Login, Register, and Reset) === */}
              {view !== 'forgot' && (
                <div>
                  <div className="flex justify-between items-center mb-2">
                    <label className="block text-sm font-medium text-slate-700">{view === 'reset' ? 'New Password' : 'Password'}</label>
                    {view === 'login' && (
                      <button type="button" onClick={() => { setView('forgot'); setError(null); setMessage(''); }} className="text-sm font-medium text-purple-600 hover:text-purple-700 hover:underline">
                        Forgot password?
                      </button>
                    )}
                  </div>
                  <div className="relative">
                    <input
                      type={showPassword ? 'text' : 'password'}
                      name="password"
                      value={formData.password}
                      onChange={handleInputChange}
                      className="w-full px-4 py-3 rounded-lg border border-slate-200 focus:outline-none focus:ring-2 focus:ring-purple-500 bg-slate-50 focus:bg-white pr-10"
                      placeholder={view === 'login' ? '••••••••••' : '8+ characters'}
                      required
                    />
                    <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                      {showPassword ? <AiOutlineEyeInvisible size={20} /> : <AiOutlineEye size={20} />}
                    </button>
                  </div>
                </div>
              )}

              <button
                type="submit"
                disabled={loading}
                className="w-full bg-gradient-to-r from-purple-600 to-purple-700 hover:from-purple-700 hover:to-purple-800 text-white font-bold py-3.5 rounded-xl shadow-lg shadow-purple-600/20 transition-all duration-200 transform hover:-translate-y-0.5 disabled:opacity-70 disabled:cursor-not-allowed"
              >
                {loading ? 'Processing...' : 
                 view === 'login' ? 'Sign In' : 
                 view === 'register' ? 'Create Account' :
                 view === 'forgot' ? 'Send Reset Link' : 'Set New Password'}
              </button>
            </form>
          
          {(view === 'login' || view === 'register') && (
              <div>
                <div className="relative my-6">
                  <div className="absolute inset-0 flex items-center">
                    <div className="w-full border-t border-slate-200"></div>
                  </div>
                  <div className="relative flex justify-center text-xs uppercase tracking-wider">
                    <span className="bg-white px-3 text-slate-400 font-medium">Or continue with</span>
                  </div>
                </div>

                <div className="grid grid-cols-3 gap-3">
                  <button type="button" className="flex items-center justify-center px-4 py-2.5 border border-slate-200 rounded-lg hover:bg-slate-50 transition-colors">
                    <svg className="w-5 h-5" viewBox="0 0 24 24">
                      <path fill="#EA4335" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
                      <path fill="#4285F4" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
                      <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" />
                      <path fill="#34A853" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" />
                    </svg>
                  </button>
                  <button type="button" className="flex items-center justify-center px-4 py-2.5 border border-slate-200 rounded-lg hover:bg-slate-50 transition-colors">
                    <svg className="w-5 h-5" viewBox="0 0 24 24" fill="#1877F2">
                      <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
                    </svg>
                  </button>
                  <button type="button" className="flex items-center justify-center px-4 py-2.5 border border-slate-200 rounded-lg hover:bg-slate-50 transition-colors">
                    <svg className="w-5 h-5" viewBox="0 0 24 24" fill="#0A66C2">
                      <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z" />
                    </svg>
                  </button>
                </div>
              </div>
            )}
            
            {/* Toggle Login/Signup (Only show on login/register views) */}
            {(view === 'login' || view === 'register') && (
              <div className="text-center mt-6">
                <p className="text-slate-600">
                  {view === 'login' ? "Don't have an account?" : 'Already have an account?'}{' '}
                  <button
                    type="button"
                    onClick={() => {
                      setView(view === 'login' ? 'register' : 'login');
                      setError(null);
                      setFormData(prev => ({ ...prev, username: '', email: '', password: '' })); // Clear sensitive fields
                    }}
                    className="font-bold text-purple-600 hover:text-purple-700 hover:underline transition-all"
                  >
                    {view === 'login' ? 'Sign up' : 'Sign in'}
                  </button>
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;