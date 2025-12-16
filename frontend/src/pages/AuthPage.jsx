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