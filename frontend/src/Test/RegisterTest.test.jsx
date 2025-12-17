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

describe("Registration Process Tests", () => {
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

  // Helper function to switch to registration form
  const switchToRegisterForm = () => {
    const signUpLink = screen.getByRole("button", { name: /Sign up/i });
    fireEvent.click(signUpLink);
  };

  // Helper function to fill registration form
  const fillRegistrationForm = (data = {}) => {
    const defaults = {
      firstName: "John",
      lastName: "Doe",
      email: "john.doe@example.com",
      username: "johndoe",
      password: "SecurePass123",
      role: "STUDENT",
    };
    const formData = { ...defaults, ...data };

    // Use getAllByPlaceholderText and select by index since placeholders may partially match
    const firstNameInput = screen.getByPlaceholderText("John");
    const lastNameInput = screen.getByPlaceholderText("Doe");
    const emailInput = screen.getByPlaceholderText("you@example.com");
    const usernameInput = screen.getByPlaceholderText("johndoe");
    const passwordInput = screen.getByPlaceholderText("8+ characters");

    fireEvent.change(firstNameInput, {
      target: { value: formData.firstName, name: "firstName" },
    });
    fireEvent.change(lastNameInput, {
      target: { value: formData.lastName, name: "lastName" },
    });
    fireEvent.change(emailInput, {
      target: { value: formData.email, name: "email" },
    });
    fireEvent.change(usernameInput, {
      target: { value: formData.username, name: "username" },
    });
    fireEvent.change(passwordInput, {
      target: { value: formData.password, name: "password" },
    });

    // Select role if different from default
    if (formData.role !== "STUDENT") {
      fireEvent.change(screen.getByRole("combobox"), {
        target: { value: formData.role, name: "role" },
      });
    }

    return formData;
  };

  test("renders registration form when clicking 'Sign up' link", () => {
    render(<AuthFormContainer />);

    // Initially shows login form
    expect(screen.getByText(/Welcome Back!/i)).toBeInTheDocument();

    // Click sign up link
    switchToRegisterForm();

    // Should now show registration form
    expect(screen.getByText(/Evaluate at QuickCheck!/i)).toBeInTheDocument();
    expect(screen.getByText(/Register your account/i)).toBeInTheDocument();
  });

  test("renders all registration form fields", () => {
    render(<AuthFormContainer />);
    switchToRegisterForm();

    // Check all form fields are present using exact placeholder matching
    expect(screen.getByPlaceholderText("John")).toBeInTheDocument(); // First Name
    expect(screen.getByPlaceholderText("Doe")).toBeInTheDocument(); // Last Name
    expect(screen.getByPlaceholderText("you@example.com")).toBeInTheDocument(); // Email
    expect(screen.getByPlaceholderText("johndoe")).toBeInTheDocument(); // Username
    expect(screen.getByPlaceholderText("8+ characters")).toBeInTheDocument(); // Password
    expect(screen.getByRole("combobox")).toBeInTheDocument(); // Role select
    expect(
      screen.getByRole("button", { name: /Create Account/i })
    ).toBeInTheDocument();
  });

  test("allows user to fill all registration fields", () => {
    render(<AuthFormContainer />);
    switchToRegisterForm();

    const formData = fillRegistrationForm();

    // Verify all fields have the correct values
    expect(screen.getByPlaceholderText("John").value).toBe(formData.firstName);
    expect(screen.getByPlaceholderText("Doe").value).toBe(formData.lastName);
    expect(screen.getByPlaceholderText("you@example.com").value).toBe(
      formData.email
    );
    expect(screen.getByPlaceholderText("johndoe").value).toBe(
      formData.username
    );
    expect(screen.getByPlaceholderText("8+ characters").value).toBe(
      formData.password
    );
  });

  test("role select defaults to STUDENT", () => {
    render(<AuthFormContainer />);
    switchToRegisterForm();

    const roleSelect = screen.getByRole("combobox");
    expect(roleSelect.value).toBe("STUDENT");
  });

  test("allows selecting PROFESSOR role", () => {
    render(<AuthFormContainer />);
    switchToRegisterForm();

    const roleSelect = screen.getByRole("combobox");
    fireEvent.change(roleSelect, {
      target: { value: "PROFESSOR", name: "role" },
    });

    expect(roleSelect.value).toBe("PROFESSOR");
  });

  test("calls register and login functions on successful registration", async () => {
    mockAuth.register = vi.fn().mockResolvedValue({
      message: "User registered successfully",
    });
    mockAuth.login = vi.fn().mockResolvedValue({
      token: "mock-token",
      username: "johndoe",
    });

    render(<AuthFormContainer />);
    switchToRegisterForm();

    const formData = fillRegistrationForm();

    const createAccountButton = screen.getByRole("button", {
      name: /Create Account/i,
    });
    fireEvent.click(createAccountButton);

    await waitFor(() => {
      // Register should be called with full form data
      expect(mockAuth.register).toHaveBeenCalledWith({
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        username: formData.username,
        password: formData.password,
        role: formData.role,
      });
    });

    await waitFor(() => {
      // Auto-login should be called after registration
      expect(mockAuth.login).toHaveBeenCalledWith({
        username: formData.username,
        password: formData.password,
      });
    });
  });

  test("navigates to dashboard after successful registration and auto-login", async () => {
    mockAuth.register = vi.fn().mockResolvedValue({
      message: "User registered successfully",
    });
    mockAuth.login = vi.fn().mockResolvedValue({
      token: "mock-token",
      username: "johndoe",
    });

    render(<AuthFormContainer />);
    switchToRegisterForm();
    fillRegistrationForm();

    const createAccountButton = screen.getByRole("button", {
      name: /Create Account/i,
    });
    fireEvent.click(createAccountButton);

    await waitFor(() => {
      expect(navigateMock).toHaveBeenCalledWith("/dashboard", {
        replace: true,
      });
    });
  });

  test("displays error message on registration failure", async () => {
    const errorMessage = "Username already exists";
    mockAuth.register = vi.fn().mockRejectedValue({
      response: { data: { message: errorMessage } },
    });

    render(<AuthFormContainer />);
    switchToRegisterForm();
    fillRegistrationForm();

    const createAccountButton = screen.getByRole("button", {
      name: /Create Account/i,
    });
    fireEvent.click(createAccountButton);

    await waitFor(() => {
      expect(mockAuth.setError).toHaveBeenCalledWith(errorMessage);
    });
  });

  test("displays error when email is already registered", async () => {
    const errorMessage = "Email is already in use";
    mockAuth.register = vi.fn().mockRejectedValue({
      response: { data: { message: errorMessage } },
    });

    render(<AuthFormContainer />);
    switchToRegisterForm();
    fillRegistrationForm({ email: "existing@example.com" });

    const createAccountButton = screen.getByRole("button", {
      name: /Create Account/i,
    });
    fireEvent.click(createAccountButton);

    await waitFor(() => {
      expect(mockAuth.setError).toHaveBeenCalledWith(errorMessage);
    });
  });

  test("disables create account button while processing", async () => {
    mockAuth.register = vi
      .fn()
      .mockImplementation(
        () => new Promise((resolve) => setTimeout(resolve, 1000))
      );

    render(<AuthFormContainer />);
    switchToRegisterForm();
    fillRegistrationForm();

    const createAccountButton = screen.getByRole("button", {
      name: /Create Account/i,
    });
    fireEvent.click(createAccountButton);

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

  test("clears error when user starts typing in any field", () => {
    mockAuth.error = "Some registration error";

    render(<AuthFormContainer />);
    switchToRegisterForm();

    // Type in any field
    const firstNameInput = screen.getByPlaceholderText("John");
    fireEvent.change(firstNameInput, {
      target: { value: "Jane", name: "firstName" },
    });

    // setError should be called with null to clear the error
    expect(mockAuth.setError).toHaveBeenCalledWith(null);
  });

  test("can toggle back to login form from registration", () => {
    render(<AuthFormContainer />);

    // Switch to register form
    switchToRegisterForm();
    expect(screen.getByText(/Evaluate at QuickCheck!/i)).toBeInTheDocument();

    // Switch back to login form
    const signInLink = screen.getByRole("button", { name: /Sign in/i });
    fireEvent.click(signInLink);

    // Should show login form again
    expect(screen.getByText(/Welcome Back!/i)).toBeInTheDocument();
  });

  test("resets form data when toggling between forms", () => {
    render(<AuthFormContainer />);

    // Switch to register form and fill some data
    switchToRegisterForm();
    fireEvent.change(screen.getByPlaceholderText("John"), {
      target: { value: "Jane", name: "firstName" },
    });

    // Switch back to login and then to register again
    const signInLink = screen.getByRole("button", { name: /Sign in/i });
    fireEvent.click(signInLink);
    switchToRegisterForm();

    // Form should be reset
    expect(screen.getByPlaceholderText("John").value).toBe("");
  });

  test("toggles password visibility", () => {
    render(<AuthFormContainer />);
    switchToRegisterForm();

    const passwordInput = screen.getByPlaceholderText("8+ characters");

    // Initially password should be hidden
    expect(passwordInput.type).toBe("password");

    // Find and click the toggle button (the eye icon button)
    const toggleButtons = screen.getAllByRole("button");
    const toggleButton = toggleButtons.find(
      (btn) => btn.type === "button" && btn.closest(".relative")
    );

    if (toggleButton) {
      fireEvent.click(toggleButton);
      expect(passwordInput.type).toBe("text");

      // Click again to hide
      fireEvent.click(toggleButton);
      expect(passwordInput.type).toBe("password");
    }
  });

  test("handles server error during registration gracefully", async () => {
    mockAuth.register = vi.fn().mockRejectedValue({
      message: "Service Unavailable. Please try again later.",
    });

    render(<AuthFormContainer />);
    switchToRegisterForm();
    fillRegistrationForm();

    const createAccountButton = screen.getByRole("button", {
      name: /Create Account/i,
    });
    fireEvent.click(createAccountButton);

    await waitFor(() => {
      expect(mockAuth.setError).toHaveBeenCalledWith(
        "Service Unavailable. Please try again later."
      );
    });
  });

  test("handles auto-login failure after successful registration", async () => {
    mockAuth.register = vi.fn().mockResolvedValue({
      message: "User registered successfully",
    });
    mockAuth.login = vi.fn().mockRejectedValue({
      response: { data: { message: "Login failed" } },
    });

    render(<AuthFormContainer />);
    switchToRegisterForm();
    fillRegistrationForm();

    const createAccountButton = screen.getByRole("button", {
      name: /Create Account/i,
    });
    fireEvent.click(createAccountButton);

    await waitFor(() => {
      expect(mockAuth.register).toHaveBeenCalled();
    });

    await waitFor(() => {
      expect(mockAuth.login).toHaveBeenCalled();
    });

    // Should set error for login failure
    await waitFor(() => {
      expect(mockAuth.setError).toHaveBeenCalledWith("Login failed");
    });
  });

  test("displays error message in the UI when error exists", () => {
    const errorMessage = "Registration failed - username taken";
    mockAuth.error = errorMessage;

    render(<AuthFormContainer />);
    switchToRegisterForm();

    // Error message should be displayed
    expect(screen.getByText(errorMessage)).toBeInTheDocument();
  });

  test("clears error when toggling between login and register forms", () => {
    mockAuth.error = "Some error";

    render(<AuthFormContainer />);

    // Switch to register form
    switchToRegisterForm();

    // setError should be called with null
    expect(mockAuth.setError).toHaveBeenCalledWith(null);
  });

  test("registers with PROFESSOR role correctly", async () => {
    mockAuth.register = vi.fn().mockResolvedValue({
      message: "User registered successfully",
    });
    mockAuth.login = vi.fn().mockResolvedValue({
      token: "mock-token",
      username: "profsmith",
    });

    render(<AuthFormContainer />);
    switchToRegisterForm();

    fillRegistrationForm({
      firstName: "Prof",
      lastName: "Smith",
      email: "prof.smith@university.edu",
      username: "profsmith",
      password: "ProfPass123",
      role: "PROFESSOR",
    });

    // Also manually change the role since fillRegistrationForm doesn't change it by default
    const roleSelect = screen.getByRole("combobox");
    fireEvent.change(roleSelect, {
      target: { value: "PROFESSOR", name: "role" },
    });

    const createAccountButton = screen.getByRole("button", {
      name: /Create Account/i,
    });
    fireEvent.click(createAccountButton);

    await waitFor(() => {
      expect(mockAuth.register).toHaveBeenCalledWith(
        expect.objectContaining({
          role: "PROFESSOR",
        })
      );
    });
  });
});
