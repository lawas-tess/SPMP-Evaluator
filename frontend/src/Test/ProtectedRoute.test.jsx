import React from "react";
import { render, screen } from "@testing-library/react";
import { describe, test, expect, vi, beforeEach } from "vitest";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import ProtectedRoute from "../components/ProtectedRoute";
import { useAuth } from "../context/AuthContext";

// Mock the AuthContext
vi.mock("../context/AuthContext", () => ({
  useAuth: vi.fn(),
}));

describe("ProtectedRoute Tests", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderWithRouter = (
    authState = { isAuthenticated: true, loading: false }
  ) => {
    useAuth.mockReturnValue(authState);

    return render(
      <MemoryRouter initialEntries={["/protected"]}>
        <Routes>
          <Route path="/login" element={<div>Login Page</div>} />
          <Route
            path="/protected"
            element={
              <ProtectedRoute>
                <div>Protected Content</div>
              </ProtectedRoute>
            }
          />
        </Routes>
      </MemoryRouter>
    );
  };

  test("renders loading spinner when loading is true", () => {
    useAuth.mockReturnValue({ isAuthenticated: false, loading: true });

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    // Check for spinner (the div with animate-spin class)
    const spinner = document.querySelector(".animate-spin");
    expect(spinner).toBeInTheDocument();
  });

  test("does not render children when loading", () => {
    useAuth.mockReturnValue({ isAuthenticated: false, loading: true });

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(screen.queryByText("Protected Content")).not.toBeInTheDocument();
  });

  test("renders children when authenticated", () => {
    renderWithRouter({ isAuthenticated: true, loading: false });

    expect(screen.getByText("Protected Content")).toBeInTheDocument();
  });

  test("redirects to login when not authenticated", () => {
    renderWithRouter({ isAuthenticated: false, loading: false });

    expect(screen.getByText("Login Page")).toBeInTheDocument();
    expect(screen.queryByText("Protected Content")).not.toBeInTheDocument();
  });

  test("does not show loading spinner when not loading", () => {
    renderWithRouter({ isAuthenticated: true, loading: false });

    const spinner = document.querySelector(".animate-spin");
    expect(spinner).not.toBeInTheDocument();
  });

  test("calls useAuth hook", () => {
    renderWithRouter({ isAuthenticated: true, loading: false });

    expect(useAuth).toHaveBeenCalled();
  });

  test("renders loading spinner with correct styling", () => {
    useAuth.mockReturnValue({ isAuthenticated: false, loading: true });

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    // Check for the container with gradient background
    const container = document.querySelector(".min-h-screen");
    expect(container).toBeInTheDocument();
    expect(container).toHaveClass("bg-gradient-to-br");
  });

  test("renders spinner circle element", () => {
    useAuth.mockReturnValue({ isAuthenticated: false, loading: true });

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    // Check for the rounded spinner element
    const spinnerCircle = document.querySelector(".rounded-full");
    expect(spinnerCircle).toBeInTheDocument();
  });

  test("renders nested children correctly", () => {
    useAuth.mockReturnValue({ isAuthenticated: true, loading: false });

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>
            <h1>Dashboard</h1>
            <p>Welcome to the dashboard</p>
            <button>Click me</button>
          </div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(screen.getByText("Dashboard")).toBeInTheDocument();
    expect(screen.getByText("Welcome to the dashboard")).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: "Click me" })
    ).toBeInTheDocument();
  });

  test("redirects unauthenticated user even with children", () => {
    renderWithRouter({ isAuthenticated: false, loading: false });

    // Should redirect to login, not show protected content
    expect(screen.getByText("Login Page")).toBeInTheDocument();
  });

  test("handles transition from loading to authenticated", () => {
    // First render with loading
    useAuth.mockReturnValue({ isAuthenticated: false, loading: true });

    const { rerender } = render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(document.querySelector(".animate-spin")).toBeInTheDocument();
    expect(screen.queryByText("Protected Content")).not.toBeInTheDocument();

    // Update to authenticated
    useAuth.mockReturnValue({ isAuthenticated: true, loading: false });

    rerender(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(document.querySelector(".animate-spin")).not.toBeInTheDocument();
    expect(screen.getByText("Protected Content")).toBeInTheDocument();
  });

  test("handles transition from loading to unauthenticated", () => {
    // First render with loading
    useAuth.mockReturnValue({ isAuthenticated: false, loading: true });

    const { rerender } = render(
      <MemoryRouter initialEntries={["/protected"]}>
        <Routes>
          <Route path="/login" element={<div>Login Page</div>} />
          <Route
            path="/protected"
            element={
              <ProtectedRoute>
                <div>Protected Content</div>
              </ProtectedRoute>
            }
          />
        </Routes>
      </MemoryRouter>
    );

    expect(document.querySelector(".animate-spin")).toBeInTheDocument();

    // Update to unauthenticated
    useAuth.mockReturnValue({ isAuthenticated: false, loading: false });

    rerender(
      <MemoryRouter initialEntries={["/protected"]}>
        <Routes>
          <Route path="/login" element={<div>Login Page</div>} />
          <Route
            path="/protected"
            element={
              <ProtectedRoute>
                <div>Protected Content</div>
              </ProtectedRoute>
            }
          />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText("Login Page")).toBeInTheDocument();
  });

  test("renders multiple children components", () => {
    useAuth.mockReturnValue({ isAuthenticated: true, loading: false });

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <header>Header Component</header>
          <main>Main Content</main>
          <footer>Footer Component</footer>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(screen.getByText("Header Component")).toBeInTheDocument();
    expect(screen.getByText("Main Content")).toBeInTheDocument();
    expect(screen.getByText("Footer Component")).toBeInTheDocument();
  });

  test("uses Navigate with replace prop for redirect", () => {
    renderWithRouter({ isAuthenticated: false, loading: false });

    // The Navigate component should redirect to login
    // We verify this by checking that Login Page is shown
    expect(screen.getByText("Login Page")).toBeInTheDocument();
  });

  test("renders component passed as children", () => {
    useAuth.mockReturnValue({ isAuthenticated: true, loading: false });

    const TestComponent = () => <div data-testid="test-component">Test</div>;

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <TestComponent />
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(screen.getByTestId("test-component")).toBeInTheDocument();
  });

  test("loading state has centered content", () => {
    useAuth.mockReturnValue({ isAuthenticated: false, loading: true });

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    const container = document.querySelector(
      ".flex.items-center.justify-center"
    );
    expect(container).toBeInTheDocument();
  });

  test("spinner has border styling", () => {
    useAuth.mockReturnValue({ isAuthenticated: false, loading: true });

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    const spinnerCircle = document.querySelector(".border-4");
    expect(spinnerCircle).toBeInTheDocument();
  });
});
