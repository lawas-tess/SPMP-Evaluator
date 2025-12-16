import React, { useEffect, useRef } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authAPI } from '../services/apiService'; // Import your API service

const OAuth2RedirectHandler = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const processedRef = useRef(false); // Prevent running twice in Strict Mode

  useEffect(() => {
    if (processedRef.current) return;
    processedRef.current = true;

    const token = searchParams.get('token');
    console.log("OAuth Redirect Reached. Token found?", !!token);

    const completeLogin = async () => {
      if (token) {
        try {
          // 1. Save Token with CORRECT key ('authToken')
          localStorage.setItem('authToken', token);

          // 2. Fetch User Details (Required for AuthContext)
          console.log("Fetching user details...");
          const response = await authAPI.validateToken();
          const userData = response.data;

          // 3. Save User Data
          console.log("User data fetched:", userData);
          localStorage.setItem('user', JSON.stringify(userData));

          // 4. Force Reload to Dashboard
          // We use window.location.href to force AuthContext to re-read localStorage
          window.location.href = '/dashboard';
          
        } catch (error) {
          console.error("Login completion failed:", error);
          localStorage.removeItem('authToken');
          localStorage.removeItem('user');
          navigate('/login');
        }
      } else {
        console.error("No token found in URL");
        navigate('/login');
      }
    };

    completeLogin();
  }, [searchParams, navigate]);

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-700 mb-4"></div>
      <h2 className="text-xl font-bold text-gray-800">Finalizing Login...</h2>
      <p className="text-gray-500 mt-2">Setting up your secure session.</p>
    </div>
  );
};

export default OAuth2RedirectHandler;