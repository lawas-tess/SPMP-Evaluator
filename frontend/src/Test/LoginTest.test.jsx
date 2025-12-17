import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, beforeEach, describe, test, expect } from "vitest";

// Create mutable mock references at module level
const mockAuth = {
  login: vi.fn(),
  register: vi.fn(),
  error: null,
  setError: vi.fn(),
  isAuthenticated: false,
  loading: false,
};

const navigateMock = vi.fn();

// Mock modules at module level (hoisted)
vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return {
    ...actual,
    useNavigate: () => navigateMock,
  };
});

vi.mock("../context/AuthContext.jsx", () => ({
  useAuth: () => mockAuth,
}));

// Import the component after mocks are set up
import AuthFormContainer from "../pages/AuthFormContainer.jsx";

describe("Login Process Tests", () => {
  beforeEach(() => {
    // Reset mocks before each test
    vi.clearAllMocks();
    navigateMock.mockReset();

    // Reset mockAuth to default state
    mockAuth.login = vi.fn();
    mockAuth.register = vi.fn();
    mockAuth.error = null;
    mockAuth.setError = vi.fn();
    mockAuth.isAuthenticated = false;
    mockAuth.loading = false;
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  test("renders login form with username and password fields", () => {
    render(<AuthFormContainer />);

    // Check that login form elements are present
    expect(screen.getByText(/Welcome Back!/i)).toBeInTheDocument();
    expect(
      screen.getByPlaceholderText(/Enter your username/i)
    ).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/8\+ characters/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /Login/i })).toBeInTheDocument();
  });

  test("allows user to type in username and password fields", () => {
    render(<AuthFormContainer />);

    const usernameInput = screen.getByPlaceholderText(/Enter your username/i);
    const passwordInput = screen.getByPlaceholderText(/8\+ characters/i);

    fireEvent.change(usernameInput, {
      target: { value: "testuser", name: "username" },
    });
    fireEvent.change(passwordInput, {
      target: { value: "testpassword123", name: "password" },
    });

    expect(usernameInput.value).toBe("testuser");
    expect(passwordInput.value).toBe("testpassword123");
  });

  test("calls login function with correct credentials on form submit", async () => {
    mockAuth.login = vi.fn().mockResolvedValue({
      token: "mock-token",
      username: "testuser",
      email: "test@example.com",
    });

    render(<AuthFormContainer />);

    const usernameInput = screen.getByPlaceholderText(/Enter your username/i);
    const passwordInput = screen.getByPlaceholderText(/8\+ characters/i);
    const loginButton = screen.getByRole("button", { name: /Login/i });

    fireEvent.change(usernameInput, {
      target: { value: "testuser", name: "username" },
    });
    fireEvent.change(passwordInput, {
      target: { value: "testpassword123", name: "password" },
    });
    fireEvent.click(loginButton);

    await waitFor(() => {
      expect(mockAuth.login).toHaveBeenCalledWith({
        username: "testuser",
        password: "testpassword123",
      });
    });
  });

  test("navigates to dashboard after successful login", async () => {
    mockAuth.login = vi.fn().mockResolvedValue({
      token: "mock-token",
      username: "testuser",
    });

    render(<AuthFormContainer />);

    const usernameInput = screen.getByPlaceholderText(/Enter your username/i);
    const passwordInput = screen.getByPlaceholderText(/8\+ characters/i);
    const loginButton = screen.getByRole("button", { name: /Login/i });

    fireEvent.change(usernameInput, {
      target: { value: "testuser", name: "username" },
    });
    fireEvent.change(passwordInput, {
      target: { value: "testpassword123", name: "password" },
    });
    fireEvent.click(loginButton);

    await waitFor(() => {
      expect(navigateMock).toHaveBeenCalledWith("/dashboard", {
        replace: true,
      });
    });
  });

  test("displays error message on login failure", () => {
    const errorMessage = "Invalid username or password";
    mockAuth.error = errorMessage;

    render(<AuthFormContainer />);

    // Error message should be displayed
    expect(screen.getByText(errorMessage)).toBeInTheDocument();
    // Additional hint for invalid credentials
    expect(
      screen.getByText(/Please check your credentials and try again/i)
    ).toBeInTheDocument();
  });

  test("disables login button while processing", async () => {
    mockAuth.login = vi
      .fn()
      .mockImplementation(
        () => new Promise((resolve) => setTimeout(resolve, 1000))
      );

    render(<AuthFormContainer />);

    const usernameInput = screen.getByPlaceholderText(/Enter your username/i);
    const passwordInput = screen.getByPlaceholderText(/8\+ characters/i);
    const loginButton = screen.getByRole("button", { name: /Login/i });

    fireEvent.change(usernameInput, {
      target: { value: "testuser", name: "username" },
    });
    fireEvent.change(passwordInput, {
      target: { value: "testpassword123", name: "password" },
    });
    fireEvent.click(loginButton);

    // Button should show "Processing..." and be disabled
    await waitFor(() => {
      expect(
        screen.getByRole("button", { name: /Processing.../i })
      ).toBeInTheDocument();
      expect(
        screen.getByRole("button", { name: /Processing.../i })
      ).toBeDisabled();
    });
  });

  test("clears error when user starts typing", () => {
    mockAuth.error = "Some error";

    render(<AuthFormContainer />);

    const usernameInput = screen.getByPlaceholderText(/Enter your username/i);

    // Type in the username field
    fireEvent.change(usernameInput, {
      target: { value: "newuser", name: "username" },
    });

    // setError should be called with null to clear the error
    expect(mockAuth.setError).toHaveBeenCalledWith(null);
  });

  test("shows forgot password link", () => {
    render(<AuthFormContainer />);

    expect(screen.getByText(/Forgot password\?/i)).toBeInTheDocument();
  });

  test("handles login with empty credentials gracefully", async () => {
    mockAuth.login = vi.fn().mockRejectedValue({
      message: "Username and password are required",
    });

    render(<AuthFormContainer />);

    // Get the form and submit it directly to bypass HTML5 validation
    const form = screen.getByRole("button", { name: /Login/i }).closest("form");
    fireEvent.submit(form);

    await waitFor(() => {
      expect(mockAuth.login).toHaveBeenCalledWith({
        username: "",
        password: "",
      });
    });
  });
});
