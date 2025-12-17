import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, beforeEach, describe, test, expect } from "vitest";

// Mock the api service
vi.mock("../services/apiService", () => ({
  default: {
    get: vi.fn(),
  },
}));

import api from "../services/apiService";
import AdminDashboard from "../components/dashboard/AdminDashboard";

describe("AdminDashboard Tests", () => {
  // Sample mock data for reports
  const mockUsersReport = {
    totalUsers: 150,
    totalStudents: 120,
    totalProfessors: 25,
    totalAdmins: 5,
  };

  const mockSubmissionsReport = {
    totalSubmissions: 450,
  };

  const mockEvaluationsReport = {
    totalEvaluations: 380,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  test("shows loading state initially", () => {
    // Make the API calls hang indefinitely to show loading state
    api.get.mockImplementation(() => new Promise(() => {}));

    render(<AdminDashboard />);

    expect(screen.getByText(/Loading.../i)).toBeInTheDocument();
  });

  test("renders dashboard with statistics after loading", async () => {
    api.get.mockImplementation((url) => {
      if (url === "/admin/reports/users") {
        return Promise.resolve({ data: mockUsersReport });
      }
      if (url === "/admin/reports/submissions") {
        return Promise.resolve({ data: mockSubmissionsReport });
      }
      if (url === "/admin/reports/evaluations") {
        return Promise.resolve({ data: mockEvaluationsReport });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<AdminDashboard />);

    // Wait for the dashboard to load
    await waitFor(() => {
      expect(screen.getByText(/Admin Dashboard/i)).toBeInTheDocument();
    });

    // Verify statistics are displayed
    expect(screen.getByText("Total Users")).toBeInTheDocument();
    expect(screen.getByText("150")).toBeInTheDocument(); // totalUsers

    expect(screen.getByText("Students")).toBeInTheDocument();
    expect(screen.getByText("120")).toBeInTheDocument(); // totalStudents

    expect(screen.getByText("Professors")).toBeInTheDocument();
    expect(screen.getByText("25")).toBeInTheDocument(); // totalProfessors

    expect(screen.getByText("Admins")).toBeInTheDocument();
    expect(screen.getByText("5")).toBeInTheDocument(); // totalAdmins
  });

  test("displays submissions and evaluations statistics", async () => {
    api.get.mockImplementation((url) => {
      if (url === "/admin/reports/users") {
        return Promise.resolve({ data: mockUsersReport });
      }
      if (url === "/admin/reports/submissions") {
        return Promise.resolve({ data: mockSubmissionsReport });
      }
      if (url === "/admin/reports/evaluations") {
        return Promise.resolve({ data: mockEvaluationsReport });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<AdminDashboard />);

    await waitFor(() => {
      expect(screen.getByText(/Admin Dashboard/i)).toBeInTheDocument();
    });

    expect(screen.getByText("Total Submissions")).toBeInTheDocument();
    expect(screen.getByText("450")).toBeInTheDocument(); // totalSubmissions

    expect(screen.getByText("Total Evaluations")).toBeInTheDocument();
    expect(screen.getByText("380")).toBeInTheDocument(); // totalEvaluations
  });

  test("renders all quick action buttons", async () => {
    api.get.mockImplementation((url) => {
      if (url === "/admin/reports/users") {
        return Promise.resolve({ data: mockUsersReport });
      }
      if (url === "/admin/reports/submissions") {
        return Promise.resolve({ data: mockSubmissionsReport });
      }
      if (url === "/admin/reports/evaluations") {
        return Promise.resolve({ data: mockEvaluationsReport });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<AdminDashboard />);

    await waitFor(() => {
      expect(screen.getByText(/Quick Actions/i)).toBeInTheDocument();
    });

    // Check all quick action buttons are rendered
    expect(screen.getByText("Manage Users")).toBeInTheDocument();
    expect(screen.getByText("Assign Students")).toBeInTheDocument();
    expect(screen.getByText("Audit Logs")).toBeInTheDocument();
    expect(screen.getByText("System Reports")).toBeInTheDocument();
    expect(screen.getByText("System Settings")).toBeInTheDocument();
  });

  test("calls onTabChange with 'users' when Manage Users is clicked", async () => {
    const onTabChangeMock = vi.fn();

    api.get.mockImplementation((url) => {
      if (url === "/admin/reports/users") {
        return Promise.resolve({ data: mockUsersReport });
      }
      if (url === "/admin/reports/submissions") {
        return Promise.resolve({ data: mockSubmissionsReport });
      }
      if (url === "/admin/reports/evaluations") {
        return Promise.resolve({ data: mockEvaluationsReport });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<AdminDashboard onTabChange={onTabChangeMock} />);

    await waitFor(() => {
      expect(screen.getByText("Manage Users")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Manage Users"));
    expect(onTabChangeMock).toHaveBeenCalledWith("users");
  });

  test("calls onTabChange with 'assignments' when Assign Students is clicked", async () => {
    const onTabChangeMock = vi.fn();

    api.get.mockImplementation((url) => {
      if (url === "/admin/reports/users") {
        return Promise.resolve({ data: mockUsersReport });
      }
      if (url === "/admin/reports/submissions") {
        return Promise.resolve({ data: mockSubmissionsReport });
      }
      if (url === "/admin/reports/evaluations") {
        return Promise.resolve({ data: mockEvaluationsReport });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<AdminDashboard onTabChange={onTabChangeMock} />);

    await waitFor(() => {
      expect(screen.getByText("Assign Students")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Assign Students"));
    expect(onTabChangeMock).toHaveBeenCalledWith("assignments");
  });

  test("calls onTabChange with 'audit' when Audit Logs is clicked", async () => {
    const onTabChangeMock = vi.fn();

    api.get.mockImplementation((url) => {
      if (url === "/admin/reports/users") {
        return Promise.resolve({ data: mockUsersReport });
      }
      if (url === "/admin/reports/submissions") {
        return Promise.resolve({ data: mockSubmissionsReport });
      }
      if (url === "/admin/reports/evaluations") {
        return Promise.resolve({ data: mockEvaluationsReport });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<AdminDashboard onTabChange={onTabChangeMock} />);

    await waitFor(() => {
      expect(screen.getByText("Audit Logs")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Audit Logs"));
    expect(onTabChangeMock).toHaveBeenCalledWith("audit");
  });

  test("calls onTabChange with 'reports' when System Reports is clicked", async () => {
    const onTabChangeMock = vi.fn();

    api.get.mockImplementation((url) => {
      if (url === "/admin/reports/users") {
        return Promise.resolve({ data: mockUsersReport });
      }
      if (url === "/admin/reports/submissions") {
        return Promise.resolve({ data: mockSubmissionsReport });
      }
      if (url === "/admin/reports/evaluations") {
        return Promise.resolve({ data: mockEvaluationsReport });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<AdminDashboard onTabChange={onTabChangeMock} />);

    await waitFor(() => {
      expect(screen.getByText("System Reports")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("System Reports"));
    expect(onTabChangeMock).toHaveBeenCalledWith("reports");
  });

  test("calls onTabChange with 'settings' when System Settings is clicked", async () => {
    const onTabChangeMock = vi.fn();

    api.get.mockImplementation((url) => {
      if (url === "/admin/reports/users") {
        return Promise.resolve({ data: mockUsersReport });
      }
      if (url === "/admin/reports/submissions") {
        return Promise.resolve({ data: mockSubmissionsReport });
      }
      if (url === "/admin/reports/evaluations") {
        return Promise.resolve({ data: mockEvaluationsReport });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<AdminDashboard onTabChange={onTabChangeMock} />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("System Settings"));
    expect(onTabChangeMock).toHaveBeenCalledWith("settings");
  });

  test("handles missing onTabChange prop gracefully", async () => {
    api.get.mockImplementation((url) => {
      if (url === "/admin/reports/users") {
        return Promise.resolve({ data: mockUsersReport });
      }
      if (url === "/admin/reports/submissions") {
        return Promise.resolve({ data: mockSubmissionsReport });
      }
      if (url === "/admin/reports/evaluations") {
        return Promise.resolve({ data: mockEvaluationsReport });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    // Render without onTabChange prop
    render(<AdminDashboard />);

    await waitFor(() => {
      expect(screen.getByText("Manage Users")).toBeInTheDocument();
    });

    // Should not throw error when clicking without onTabChange
    expect(() => {
      fireEvent.click(screen.getByText("Manage Users"));
    }).not.toThrow();
  });

  test("displays zero values when reports data is empty", async () => {
    api.get.mockImplementation((url) => {
      if (url === "/admin/reports/users") {
        return Promise.resolve({ data: {} });
      }
      if (url === "/admin/reports/submissions") {
        return Promise.resolve({ data: {} });
      }
      if (url === "/admin/reports/evaluations") {
        return Promise.resolve({ data: {} });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<AdminDashboard />);

    await waitFor(() => {
      expect(screen.getByText(/Admin Dashboard/i)).toBeInTheDocument();
    });

    // All statistics should show 0 when data is empty
    const zeros = screen.getAllByText("0");
    expect(zeros.length).toBeGreaterThanOrEqual(6); // 6 statistics cards
  });

  test("handles API error gracefully", async () => {
    const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => {});

    api.get.mockRejectedValue(new Error("Network error"));

    render(<AdminDashboard />);

    await waitFor(() => {
      expect(screen.getByText(/Admin Dashboard/i)).toBeInTheDocument();
    });

    // Dashboard should still render even after error
    expect(screen.getByText("Quick Actions")).toBeInTheDocument();
    expect(consoleSpy).toHaveBeenCalledWith(
      "Failed to fetch reports:",
      expect.any(Error)
    );

    consoleSpy.mockRestore();
  });

  test("fetches all three reports on mount", async () => {
    api.get.mockImplementation((url) => {
      if (url === "/admin/reports/users") {
        return Promise.resolve({ data: mockUsersReport });
      }
      if (url === "/admin/reports/submissions") {
        return Promise.resolve({ data: mockSubmissionsReport });
      }
      if (url === "/admin/reports/evaluations") {
        return Promise.resolve({ data: mockEvaluationsReport });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<AdminDashboard />);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith("/admin/reports/users");
      expect(api.get).toHaveBeenCalledWith("/admin/reports/submissions");
      expect(api.get).toHaveBeenCalledWith("/admin/reports/evaluations");
    });

    expect(api.get).toHaveBeenCalledTimes(3);
  });
});
