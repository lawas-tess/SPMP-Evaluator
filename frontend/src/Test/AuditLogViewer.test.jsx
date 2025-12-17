import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, test, expect, vi, beforeEach } from "vitest";
import AuditLogViewer from "../components/dashboard/AuditLogViewer";
import api from "../services/apiService";

// Mock the API service
vi.mock("../services/apiService", () => ({
  default: {
    get: vi.fn(),
  },
}));

describe("AuditLogViewer Tests", () => {
  const mockLogs = [
    {
      id: 1,
      createdAt: "2025-12-18T10:30:00Z",
      user: { username: "admin" },
      action: "CREATE_USER",
      resourceType: "User",
      resourceId: "123",
      details: "Created new user account",
    },
    {
      id: 2,
      createdAt: "2025-12-18T11:00:00Z",
      user: { username: "professor1" },
      action: "UPDATE_DOCUMENT",
      resourceType: "Document",
      resourceId: "456",
      details: "Updated document content",
    },
    {
      id: 3,
      createdAt: "2025-12-18T11:30:00Z",
      user: { username: "admin" },
      action: "DELETE_SUBMISSION",
      resourceType: "Submission",
      resourceId: "789",
      details: "Removed invalid submission",
    },
    {
      id: 4,
      createdAt: "2025-12-18T12:00:00Z",
      user: null,
      action: "LOGIN",
      resourceType: "Session",
      resourceId: null,
      details: null,
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  const setupMocks = () => {
    api.get.mockResolvedValue({ data: mockLogs });
  };

  test("shows loading state initially", () => {
    api.get.mockImplementation(() => new Promise(() => {})); // Never resolves

    render(<AuditLogViewer />);

    expect(screen.getByText("Loading...")).toBeInTheDocument();
  });

  test("renders audit log viewer header", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("Audit Log Viewer")).toBeInTheDocument();
    });
  });

  test("displays filter inputs", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(
        screen.getByPlaceholderText("Filter by username")
      ).toBeInTheDocument();
      expect(
        screen.getByPlaceholderText("Filter by action")
      ).toBeInTheDocument();
      expect(
        screen.getByPlaceholderText("Filter by resource")
      ).toBeInTheDocument();
    });
  });

  test("displays Apply and Clear filter buttons", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(
        screen.getByRole("button", { name: /Apply/i })
      ).toBeInTheDocument();
      expect(
        screen.getByRole("button", { name: /Clear/i })
      ).toBeInTheDocument();
    });
  });

  test("displays table headers correctly", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("Timestamp")).toBeInTheDocument();
      // User appears both as header and in data, use columnheader role
      expect(
        screen.getByRole("columnheader", { name: "User" })
      ).toBeInTheDocument();
      expect(
        screen.getByRole("columnheader", { name: "Action" })
      ).toBeInTheDocument();
      expect(
        screen.getByRole("columnheader", { name: "Resource" })
      ).toBeInTheDocument();
      expect(screen.getByText("Resource ID")).toBeInTheDocument();
      expect(
        screen.getByRole("columnheader", { name: "Details" })
      ).toBeInTheDocument();
    });
  });

  test("displays audit logs in table", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      // admin appears twice in data
      const adminElements = screen.getAllByText("admin");
      expect(adminElements.length).toBe(2);
      expect(screen.getByText("professor1")).toBeInTheDocument();
      expect(screen.getByText("CREATE_USER")).toBeInTheDocument();
      expect(screen.getByText("UPDATE_DOCUMENT")).toBeInTheDocument();
      expect(screen.getByText("DELETE_SUBMISSION")).toBeInTheDocument();
    });
  });

  test("displays resource types correctly", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      // User appears both as header and data, check all instances
      const userElements = screen.getAllByText("User");
      expect(userElements.length).toBeGreaterThanOrEqual(2); // header + data
      expect(screen.getByText("Document")).toBeInTheDocument();
      expect(screen.getByText("Submission")).toBeInTheDocument();
      expect(screen.getByText("Session")).toBeInTheDocument();
    });
  });

  test("displays 'System' for logs without user", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("System")).toBeInTheDocument();
    });
  });

  test("displays '-' for missing resourceId", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      const dashElements = screen.getAllByText("-");
      expect(dashElements.length).toBeGreaterThan(0);
    });
  });

  test("displays total logs count", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("Total logs: 4")).toBeInTheDocument();
    });
  });

  test("shows empty state when no logs found", async () => {
    api.get.mockResolvedValue({ data: [] });

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("No audit logs found")).toBeInTheDocument();
      expect(screen.getByText("Total logs: 0")).toBeInTheDocument();
    });
  });

  test("allows entering username filter", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("Audit Log Viewer")).toBeInTheDocument();
    });

    const usernameInput = screen.getByPlaceholderText("Filter by username");
    fireEvent.change(usernameInput, { target: { value: "admin" } });

    expect(usernameInput.value).toBe("admin");
  });

  test("allows entering action filter", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("Audit Log Viewer")).toBeInTheDocument();
    });

    const actionInput = screen.getByPlaceholderText("Filter by action");
    fireEvent.change(actionInput, { target: { value: "CREATE" } });

    expect(actionInput.value).toBe("CREATE");
  });

  test("allows entering resource filter", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("Audit Log Viewer")).toBeInTheDocument();
    });

    const resourceInput = screen.getByPlaceholderText("Filter by resource");
    fireEvent.change(resourceInput, { target: { value: "Document" } });

    expect(resourceInput.value).toBe("Document");
  });

  test("Apply button submits filter form", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("Audit Log Viewer")).toBeInTheDocument();
    });

    // Enter filter value
    const usernameInput = screen.getByPlaceholderText("Filter by username");
    fireEvent.change(usernameInput, { target: { value: "admin" } });

    // Click Apply
    const applyButton = screen.getByRole("button", { name: /Apply/i });
    fireEvent.click(applyButton);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith(
        expect.stringContaining("/admin/audit-logs")
      );
    });
  });

  test("filters are included in API request", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("Audit Log Viewer")).toBeInTheDocument();
    });

    // Enter all filters
    fireEvent.change(screen.getByPlaceholderText("Filter by username"), {
      target: { value: "admin" },
    });
    fireEvent.change(screen.getByPlaceholderText("Filter by action"), {
      target: { value: "CREATE" },
    });
    fireEvent.change(screen.getByPlaceholderText("Filter by resource"), {
      target: { value: "User" },
    });

    // Submit form
    fireEvent.click(screen.getByRole("button", { name: /Apply/i }));

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith(
        expect.stringMatching(/username=admin/)
      );
      expect(api.get).toHaveBeenCalledWith(
        expect.stringMatching(/action=CREATE/)
      );
      expect(api.get).toHaveBeenCalledWith(
        expect.stringMatching(/resource=User/)
      );
    });
  });

  test("Clear button resets all filters", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("Audit Log Viewer")).toBeInTheDocument();
    });

    // Enter filters
    const usernameInput = screen.getByPlaceholderText("Filter by username");
    const actionInput = screen.getByPlaceholderText("Filter by action");
    const resourceInput = screen.getByPlaceholderText("Filter by resource");

    fireEvent.change(usernameInput, { target: { value: "admin" } });
    fireEvent.change(actionInput, { target: { value: "CREATE" } });
    fireEvent.change(resourceInput, { target: { value: "User" } });

    // Click Clear
    fireEvent.click(screen.getByRole("button", { name: /Clear/i }));

    expect(usernameInput.value).toBe("");
    expect(actionInput.value).toBe("");
    expect(resourceInput.value).toBe("");
  });

  test("action badges have appropriate colors", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      // CREATE action should have green styling
      const createBadge = screen.getByText("CREATE_USER");
      expect(createBadge.className).toContain("bg-green-100");

      // UPDATE action should have yellow styling
      const updateBadge = screen.getByText("UPDATE_DOCUMENT");
      expect(updateBadge.className).toContain("bg-yellow-100");

      // DELETE action should have red styling
      const deleteBadge = screen.getByText("DELETE_SUBMISSION");
      expect(deleteBadge.className).toContain("bg-red-100");
    });
  });

  test("handles API error gracefully", async () => {
    const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => {});

    api.get.mockRejectedValue(new Error("Network error"));

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith(
        "Failed to fetch audit logs:",
        expect.any(Error)
      );
    });

    // Component should still render
    expect(screen.getByText("Audit Log Viewer")).toBeInTheDocument();

    consoleSpy.mockRestore();
  });

  test("fetches logs on component mount", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith("/admin/audit-logs");
    });
  });

  test("displays log details correctly", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("Created new user account")).toBeInTheDocument();
      expect(screen.getByText("Updated document content")).toBeInTheDocument();
      expect(
        screen.getByText("Removed invalid submission")
      ).toBeInTheDocument();
    });
  });

  test("displays resource IDs correctly", async () => {
    setupMocks();

    render(<AuditLogViewer />);

    await waitFor(() => {
      expect(screen.getByText("123")).toBeInTheDocument();
      expect(screen.getByText("456")).toBeInTheDocument();
      expect(screen.getByText("789")).toBeInTheDocument();
    });
  });
});
