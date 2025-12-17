import React, { useEffect, useRef } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authAPI } from '../services/apiService';

const OAuthCallback = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const processedRef = useRef(false);

    useEffect(() => {
        if (processedRef.current) return;
        processedRef.current = true;

        const token = searchParams.get('token');

        const completeLogin = async () => {
            if (token) {
                try {
                    // 1. Save Token
                    localStorage.setItem('authToken', token);

                    // 2. Fetch User Details to ensure we have the full profile
                    const response = await authAPI.validateToken();
                    const userData = response.data;

                    // 3. Save User Data
                    localStorage.setItem('user', JSON.stringify(userData));

                    // 4. Force Reload to Dashboard (to refresh AuthContext)
                    window.location.href = '/dashboard';
                    
                } catch (error) {
                    console.error("Login failed:", error);
                    navigate('/login?error=google_auth_failed');
                }
            } else {
                navigate('/login');
            }
        };

        completeLogin();
    }, [searchParams, navigate]);

    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-700 mb-4"></div>
            <h2 className="text-xl font-bold text-gray-800">Logging you in...</h2>
        </div>
    );
};

export default OAuthCallback;