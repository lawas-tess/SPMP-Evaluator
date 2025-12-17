// AuthFormContainer.jsx
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { LoginForm, RegisterForm } from "./Forms.jsx";

const initialFormData = {
  username: "",
  email: "",
  password: "",
  firstName: "",
  lastName: "",
  role: "STUDENT",
};

const AuthFormContainer = () => {
  const navigate = useNavigate();
  const {
    login,
    register,
    error,
    setError,
    isAuthenticated,
    loading: authLoading,
  } = useAuth();

  const [isLogin, setIsLogin] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState(initialFormData);

  // Redirect to dashboard if already authenticated
  useEffect(() => {
    if (!authLoading && isAuthenticated) {
      navigate("/dashboard", { replace: true });
    }
  }, [isAuthenticated, authLoading, navigate]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    if (error) setError(null);
  };

  const handleToggleForm = () => {
    setIsLogin(!isLogin);
    setError(null);
    setFormData(initialFormData);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (isLogin) {
        // Login
        await login({
          username: formData.username,
          password: formData.password,
        });
        navigate("/dashboard", { replace: true });
      } else {
        // Register & Auto-login
        const { username, password } = formData;
        await register(formData);

        // Auto-login after successful registration
        await login({ username, password });
        navigate("/dashboard", { replace: true });
      }
    } catch (err) {
      console.error("Auth error:", err);
      setError(
        err.response?.data?.message || err.message || "Authentication failed"
      );
    } finally {
      setLoading(false);
    }
  };

  const commonProps = {
    formData,
    handleInputChange,
    handleSubmit,
    isProcessing: loading,
    error,
    showPassword,
    setShowPassword,
    handleToggleForm,
  };

  return (
    <div className="bg-white rounded-3xl shadow-2xl p-8 md:p-10 lg:p-12 h-full flex flex-col justify-center">
      {/* Header */}
      <div className="text-center mb-8">
        <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-2">
          {isLogin ? "Welcome Back!" : "Evaluate at QuickCheck!"}
        </h2>
        <p className="text-gray-600">
          {isLogin ? "Sign in to your account" : "Register your account"}
        </p>
      </div>

      {/* Error message */}
      {error && (
        <div className="mb-4 p-4 bg-red-50 border-l-4 border-red-500 rounded-lg">
          <p className="text-red-700 font-semibold text-sm">{error}</p>
          {isLogin && error.toLowerCase().includes("invalid") && (
            <p className="text-red-600 text-xs mt-1">
              Please check your credentials and try again.
            </p>
          )}
        </div>
      )}

      {/* Conditional Form Rendering */}
      {isLogin ? (
        <LoginForm {...commonProps} />
      ) : (
        <RegisterForm {...commonProps} />
      )}

      {/* Toggle login/signup - Kept in container as it's common for both forms */}
      <div className="text-center mt-6 pt-6 border-t border-gray-200">
        <p className="text-gray-700">
          {isLogin ? "Don't have an account?" : "Already have an account?"}{" "}
          <button
            type="button"
            onClick={handleToggleForm}
            className="text-sm text-purple-600 hover:text-purple-700 font-medium"
          >
            {isLogin ? "Sign up" : "Sign in"}
          </button>
        </p>
      </div>
    </div>
  );
};

export default AuthFormContainer;
