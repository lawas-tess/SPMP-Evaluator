import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

afterEach(() => {
  vi.resetModules();
  vi.restoreAllMocks();
});

test("renders login view and toggles to register", async () => {
  // reset module registry so mocks are applied fresh
  vi.resetModules();

  const navigateMock = vi.fn();

  // Mock react-router-dom's useNavigate (use non-hoisted doMock so navigateMock exists)
  vi.doMock("react-router-dom", async () => {
    const actual = await vi.importActual("react-router-dom");
    return { ...actual, useNavigate: () => navigateMock };
  });

  // Mock auth context
  vi.mock("../context/AuthContext.jsx", () => ({
    useAuth: () => ({
      login: vi.fn(),
      register: vi.fn(),
      error: null,
      setError: vi.fn(),
      isAuthenticated: false,
      loading: false,
    }),
  }));

  // Stub the LoginForm and RegisterForm so we can assert which is rendered
  vi.mock("../pages/Forms.jsx", () => ({
    LoginForm: (props) => <div data-testid="login-form">Login Form</div>,
    RegisterForm: (props) => (
      <div data-testid="register-form">Register Form</div>
    ),
  }));

  const AuthFormContainer = (await import("../pages/AuthFormContainer.jsx"))
    .default;

  render(<AuthFormContainer />);

  // Should show login header and login form by default
  expect(screen.getByText(/Welcome Back!/i)).toBeInTheDocument();
  expect(screen.getByTestId("login-form")).toBeInTheDocument();

  // Toggle to register view
  const toggleButton = screen.getByRole("button", { name: /Sign up|Sign in/i });
  fireEvent.click(toggleButton);

  // Now register header and register form should appear
  expect(screen.getByText(/Evaluate at QuickCheck!/i)).toBeInTheDocument();
  expect(screen.getByTestId("register-form")).toBeInTheDocument();
});

test("redirects to dashboard when already authenticated", async () => {
  vi.resetModules();

  const navigateMock = vi.fn();

  vi.doMock("react-router-dom", async () => {
    const actual = await vi.importActual("react-router-dom");
    return { ...actual, useNavigate: () => navigateMock };
  });

  // Simulate authenticated user
  vi.mock("../context/AuthContext.jsx", () => ({
    useAuth: () => ({
      login: vi.fn(),
      register: vi.fn(),
      error: null,
      setError: vi.fn(),
      isAuthenticated: true,
      loading: false,
    }),
  }));

  // Minimal form stubs
  vi.mock("../pages/Forms.jsx", () => ({
    LoginForm: (props) => <div data-testid="login-form">Login Form</div>,
    RegisterForm: (props) => (
      <div data-testid="register-form">Register Form</div>
    ),
  }));

  const AuthFormContainer = (await import("../pages/AuthFormContainer.jsx"))
    .default;

  render(<AuthFormContainer />);

  // Expect navigate to be called to redirect to dashboard
  expect(navigateMock).toHaveBeenCalled();
  // Optionally check called with path
  expect(navigateMock).toHaveBeenCalledWith("/dashboard", { replace: true });
});
