import React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

// Mock pages and routing wrappers to keep the test focused and deterministic
vi.mock("../pages/AuthPage.jsx", () => ({
  __esModule: true,
  default: () => <div>Mock Auth Page</div>,
}));

vi.mock("../pages/Dashboard.jsx", () => ({
  __esModule: true,
  default: () => <div>Mock Dashboard</div>,
}));

vi.mock("../components/ProtectedRoute.jsx", () => ({
  __esModule: true,
  default: ({ children }) => <div data-testid="protected">{children}</div>,
}));

// Provide a minimal AuthProvider to avoid running the real context logic
vi.mock("../context/AuthContext.jsx", () => ({
  AuthProvider: ({ children }) => <>{children}</>,
}));

import App from "../App.jsx";

afterEach(() => {
  vi.clearAllMocks();
  // Reset path to root between tests
  window.history.pushState({}, "", "/");
});

test("renders AuthPage on default path (redirects to /login)", () => {
  // Ensure default path
  window.history.pushState({}, "root", "/");
  render(<App />);

  expect(screen.getByText("Mock Auth Page")).toBeInTheDocument();
});

test("renders Dashboard when navigating to /dashboard", () => {
  // Simulate navigating to protected route before rendering
  window.history.pushState({}, "dashboard", "/dashboard");
  render(<App />);

  expect(screen.getByText("Mock Dashboard")).toBeInTheDocument();
});
