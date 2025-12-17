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
import AdminReports from "../components/dashboard/AdminReports";

describe("AdminReports Tests", () => {
  // Sample mock data for reports
  const mockUsersReport = {
    totalUsers: 150,
    studentCount: 120,
    professorCount: 25,
    adminCount: 5,
    newUsersThisWeek: 10,
    activeUsers: 85,
    inactiveUsers: 65,
  };

  const mockSubmissionsReport = {
    totalSubmissions: 450,
    pendingEvaluations: 25,
    submissionsThisWeek: 50,
    avgPerStudent: 3.75,
    mostActiveStudent: "John Doe",
    peakDay: "Monday",
  };

  const mockEvaluationsReport = {
    totalEvaluations: 380,
    averageScore: 78.5,
    highestScore: 98.0,
    passingRate: 82.5,
    avgEvaluationTime: "2.5 minutes",
    commonIssues: "Missing sections",
  };

  const setupMocks = () => {
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
      // Export endpoints
      if (url.includes("/export")) {
        return Promise.resolve({ data: new Blob(["test,data"]) });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });
  };

  beforeEach(() => {
    vi.clearAllMocks();
    // Mock URL.createObjectURL
    global.URL.createObjectURL = vi.fn(() => "blob:test-url");
    global.URL.revokeObjectURL = vi.fn();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  test("shows loading state initially", () => {
    api.get.mockImplementation(() => new Promise(() => {}));

    render(<AdminReports />);

    expect(screen.getByText(/Loading reports.../i)).toBeInTheDocument();
  });

  test("renders system reports header and refresh button", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("System Reports")).toBeInTheDocument();
    });

    expect(
      screen.getByRole("button", { name: /Refresh Reports/i })
    ).toBeInTheDocument();
  });

  test("displays statistics cards with correct data", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("System Reports")).toBeInTheDocument();
    });

    // Check statistics cards
    expect(screen.getByText("Total Users")).toBeInTheDocument();
    expect(screen.getByText("150")).toBeInTheDocument();

    expect(screen.getByText("Total Submissions")).toBeInTheDocument();
    expect(screen.getByText("450")).toBeInTheDocument();

    expect(screen.getByText("Completed Evaluations")).toBeInTheDocument();
    expect(screen.getByText("380")).toBeInTheDocument();

    expect(screen.getByText("Average Score")).toBeInTheDocument();
    expect(screen.getByText("78.5%")).toBeInTheDocument();
  });

  test("renders all three report tabs", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("System Reports")).toBeInTheDocument();
    });

    expect(screen.getByText("Users Report")).toBeInTheDocument();
    expect(screen.getByText("Submissions Report")).toBeInTheDocument();
    expect(screen.getByText("Evaluations Report")).toBeInTheDocument();
  });

  test("displays users report by default", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("User Statistics")).toBeInTheDocument();
    });

    // Check user statistics details
    expect(screen.getByText("Students")).toBeInTheDocument();
    expect(screen.getByText("120")).toBeInTheDocument();

    expect(screen.getByText("Professors")).toBeInTheDocument();
    expect(screen.getByText("25")).toBeInTheDocument();

    expect(screen.getByText("Admins")).toBeInTheDocument();
    expect(screen.getByText("5")).toBeInTheDocument();

    // Check activity section
    expect(screen.getByText("Recent Activity")).toBeInTheDocument();
    expect(
      screen.getByText(/New registrations this week: 10/)
    ).toBeInTheDocument();
    expect(
      screen.getByText(/Active users \(last 7 days\): 85/)
    ).toBeInTheDocument();
    expect(screen.getByText(/Inactive users: 65/)).toBeInTheDocument();
  });

  test("switches to submissions report when tab is clicked", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("System Reports")).toBeInTheDocument();
    });

    // Click on Submissions Report tab
    fireEvent.click(screen.getByText("Submissions Report"));

    await waitFor(() => {
      expect(screen.getByText("Submission Statistics")).toBeInTheDocument();
    });

    // Check submission statistics
    expect(screen.getByText("Pending Evaluation")).toBeInTheDocument();
    expect(screen.getByText("Submissions This Week")).toBeInTheDocument();
    expect(screen.getByText("50")).toBeInTheDocument();

    // Check trends section
    expect(screen.getByText("Submission Trends")).toBeInTheDocument();
    expect(
      screen.getByText(/Average submissions per student: 3.75/)
    ).toBeInTheDocument();
    expect(
      screen.getByText(/Most active student: John Doe/)
    ).toBeInTheDocument();
    expect(screen.getByText(/Peak submission day: Monday/)).toBeInTheDocument();
  });

  test("switches to evaluations report when tab is clicked", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("System Reports")).toBeInTheDocument();
    });

    // Click on Evaluations Report tab
    fireEvent.click(screen.getByText("Evaluations Report"));

    await waitFor(() => {
      expect(screen.getByText("Evaluation Statistics")).toBeInTheDocument();
    });

    // Check evaluation statistics
    expect(screen.getByText("Highest Score")).toBeInTheDocument();
    expect(screen.getByText("98.0%")).toBeInTheDocument();

    // Check performance metrics
    expect(screen.getByText("Performance Metrics")).toBeInTheDocument();
    expect(
      screen.getByText(/Passing rate \(≥70%\): 82.5%/)
    ).toBeInTheDocument();
    expect(
      screen.getByText(/Average evaluation time: 2.5 minutes/)
    ).toBeInTheDocument();
    expect(
      screen.getByText(/Most common issues: Missing sections/)
    ).toBeInTheDocument();
  });

  test("refresh button calls fetchReports again", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("System Reports")).toBeInTheDocument();
    });

    // Clear mock to track new calls
    api.get.mockClear();
    setupMocks();

    // Click refresh button
    fireEvent.click(screen.getByRole("button", { name: /Refresh Reports/i }));

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith("/admin/reports/users");
      expect(api.get).toHaveBeenCalledWith("/admin/reports/submissions");
      expect(api.get).toHaveBeenCalledWith("/admin/reports/evaluations");
    });
  });

  test("renders date range filter inputs", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("System Reports")).toBeInTheDocument();
    });

    expect(screen.getByText("Start Date")).toBeInTheDocument();
    expect(screen.getByText("End Date")).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /Apply Filter/i })
    ).toBeInTheDocument();
  });

  test("allows changing date range filter values", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("System Reports")).toBeInTheDocument();
    });

    // Find date inputs by their labels
    const startDateLabel = screen.getByText("Start Date");
    const endDateLabel = screen.getByText("End Date");

    const startDateInput = startDateLabel.parentElement.querySelector("input");
    const endDateInput = endDateLabel.parentElement.querySelector("input");

    expect(startDateInput).toBeInTheDocument();
    expect(endDateInput).toBeInTheDocument();

    fireEvent.change(startDateInput, { target: { value: "2025-01-01" } });
    fireEvent.change(endDateInput, { target: { value: "2025-12-31" } });

    expect(startDateInput.value).toBe("2025-01-01");
    expect(endDateInput.value).toBe("2025-12-31");
  });

  test("export CSV button exists in users report", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("User Statistics")).toBeInTheDocument();
    });

    const exportButtons = screen.getAllByRole("button", {
      name: /Export CSV/i,
    });
    expect(exportButtons.length).toBeGreaterThan(0);
  });

  test("export CSV calls API with correct endpoint for users", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("User Statistics")).toBeInTheDocument();
    });

    const exportButton = screen.getAllByRole("button", {
      name: /Export CSV/i,
    })[0];
    fireEvent.click(exportButton);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith(
        expect.stringContaining("/admin/reports/users/export"),
        expect.objectContaining({ responseType: "blob" })
      );
    });
  });

  test("export CSV calls API with correct endpoint for submissions", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("System Reports")).toBeInTheDocument();
    });

    // Switch to submissions tab
    fireEvent.click(screen.getByText("Submissions Report"));

    await waitFor(() => {
      expect(screen.getByText("Submission Statistics")).toBeInTheDocument();
    });

    const exportButton = screen.getAllByRole("button", {
      name: /Export CSV/i,
    })[0];
    fireEvent.click(exportButton);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith(
        expect.stringContaining("/admin/reports/submissions/export"),
        expect.objectContaining({ responseType: "blob" })
      );
    });
  });

  test("handles API error gracefully", async () => {
    const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => {});

    api.get.mockRejectedValue(new Error("Network error"));

    render(<AdminReports />);

    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith(
        "Failed to fetch reports:",
        expect.any(Error)
      );
    });

    // Component should still render even on error
    expect(screen.getByText("System Reports")).toBeInTheDocument();

    consoleSpy.mockRestore();
  });

  test("handles export error gracefully", async () => {
    const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => {});
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});

    // Setup mocks where export fails
    api.get.mockImplementation((url) => {
      if (url.includes("/export")) {
        return Promise.reject(new Error("Export failed"));
      }
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

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("User Statistics")).toBeInTheDocument();
    });

    const exportButton = screen.getAllByRole("button", {
      name: /Export CSV/i,
    })[0];
    fireEvent.click(exportButton);

    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith(
        "Failed to export report:",
        expect.any(Error)
      );
    });

    consoleSpy.mockRestore();
    alertSpy.mockRestore();
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

    render(<AdminReports />);

    await waitFor(() => {
      expect(screen.getByText("System Reports")).toBeInTheDocument();
    });

    // Check that zeros are displayed for missing data
    const totalUsersCard = screen.getByText("Total Users").parentElement;
    expect(totalUsersCard).toHaveTextContent("0");
  });

  test("fetches all three reports on mount", async () => {
    setupMocks();

    render(<AdminReports />);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith("/admin/reports/users");
      expect(api.get).toHaveBeenCalledWith("/admin/reports/submissions");
      expect(api.get).toHaveBeenCalledWith("/admin/reports/evaluations");
    });

    expect(api.get).toHaveBeenCalledTimes(3);
  });
});
