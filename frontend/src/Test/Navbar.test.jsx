import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

// Mock the auth context hook used by Navbar
vi.mock("../context/AuthContext.jsx", () => ({
  useAuth: vi.fn(),
}));

// Mock NotificationBell to keep the test focused on Navbar behavior
vi.mock("../components/NotificationBell", () => ({
  __esModule: true,
  default: () => <div data-testid="notification-mock">Notifications</div>,
}));

import { useAuth } from "../context/AuthContext.jsx";
import Navbar from "../components/Navbar";

afterEach(() => {
  vi.clearAllMocks();
});

test("renders brand and user name", () => {
  useAuth.mockReturnValue({
    user: { firstName: "Test", lastName: "User" },
    logout: vi.fn(),
  });

  render(<Navbar />);

  // Brand
  expect(screen.getByText("QuickCheck")).toBeInTheDocument();
  expect(screen.getByText("SPMP Evaluator")).toBeInTheDocument();

  // Welcome and user's full name
  expect(screen.getByText(/Welcome,/i)).toBeInTheDocument();
  expect(screen.getByText(/Test\s+User/)).toBeInTheDocument();

  // Notification bell is rendered (mock)
  expect(screen.getByTestId("notification-mock")).toBeInTheDocument();
});

test("clicking logout calls logout from context", () => {
  const logout = vi.fn();
  useAuth.mockReturnValue({
    user: { firstName: "Alice", lastName: "Smith" },
    logout,
  });

  render(<Navbar />);

  const btn = screen.getByRole("button", { name: /Logout/i });
  fireEvent.click(btn);

  expect(logout).toHaveBeenCalled();
});
