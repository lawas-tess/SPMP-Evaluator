import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, test, expect, vi, beforeEach } from "vitest";
import SystemSettingsForm from "../components/dashboard/SystemSettingsForm";
import api from "../services/apiService";

// Mock the API service
vi.mock("../services/apiService", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

describe("SystemSettingsForm Tests", () => {
  const mockSettings = [
    {
      id: 1,
      key: "MAINTENANCE_MODE",
      value: "false",
      category: "SYSTEM",
      type: "BOOLEAN",
      description: "Prevents non-admin users from accessing the system",
      requiresRestart: false,
    },
    {
      id: 2,
      key: "ALLOW_STUDENT_REGISTRATION",
      value: "true",
      category: "REGISTRATION",
      type: "BOOLEAN",
      description: "Allow new student registrations",
      requiresRestart: false,
    },
    {
      id: 3,
      key: "ALLOW_PROFESSOR_REGISTRATION",
      value: "true",
      category: "REGISTRATION",
      type: "BOOLEAN",
      description: "Allow new professor registrations",
      requiresRestart: false,
    },
    {
      id: 4,
      key: "MAX_FILE_SIZE_MB",
      value: "50",
      category: "FILE_UPLOAD",
      type: "NUMBER",
      description: "Maximum allowed upload file size in MB",
      requiresRestart: false,
    },
    {
      id: 5,
      key: "SESSION_TIMEOUT_MINUTES",
      value: "60",
      category: "SECURITY",
      type: "NUMBER",
      description: "Auto-logout after specified minutes of inactivity",
      requiresRestart: true,
    },
    {
      id: 6,
      key: "SYSTEM_ANNOUNCEMENT",
      value: "",
      category: "SYSTEM",
      type: "TEXT",
      description: "Display announcement message to all users",
      requiresRestart: false,
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  const setupMocks = () => {
    api.get.mockResolvedValue({ data: mockSettings });
  };

  test("shows loading state initially", () => {
    api.get.mockImplementation(() => new Promise(() => {}));

    render(<SystemSettingsForm />);

    expect(screen.getByText("Loading settings...")).toBeInTheDocument();
  });

  test("renders page header", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });
  });

  test("renders Reset button", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(
        screen.getByRole("button", { name: /Reset/i })
      ).toBeInTheDocument();
    });
  });

  test("renders Save Changes button", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(
        screen.getByRole("button", { name: /Save Changes/i })
      ).toBeInTheDocument();
    });
  });

  test("renders settings grouped by category", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("SYSTEM")).toBeInTheDocument();
      expect(screen.getByText("REGISTRATION")).toBeInTheDocument();
      expect(screen.getByText("FILE_UPLOAD")).toBeInTheDocument();
      expect(screen.getByText("SECURITY")).toBeInTheDocument();
    });
  });

  test("displays setting descriptions", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Prevents non-admin users from accessing the system")
      ).toBeInTheDocument();
      expect(
        screen.getByText("Allow new student registrations")
      ).toBeInTheDocument();
      expect(
        screen.getByText("Maximum allowed upload file size in MB")
      ).toBeInTheDocument();
    });
  });

  test("displays setting type labels", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      const booleanLabels = screen.getAllByText("BOOLEAN");
      expect(booleanLabels.length).toBeGreaterThan(0);

      const numberLabels = screen.getAllByText("NUMBER");
      expect(numberLabels.length).toBeGreaterThan(0);
    });
  });

  test("renders checkbox for boolean settings", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      const checkboxes = screen.getAllByRole("checkbox");
      expect(checkboxes.length).toBeGreaterThanOrEqual(3); // At least 3 boolean settings
    });
  });

  test("renders number input for number settings", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      const numberInputs = screen.getAllByRole("spinbutton");
      expect(numberInputs.length).toBeGreaterThanOrEqual(2); // MAX_FILE_SIZE_MB and SESSION_TIMEOUT_MINUTES
    });
  });

  test("shows requires restart warning for applicable settings", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(
        screen.getByText(/Requires application restart/i)
      ).toBeInTheDocument();
    });
  });

  test("renders warning notice at bottom", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(
        screen.getByText(/Changing system settings may affect all users/i)
      ).toBeInTheDocument();
    });
  });

  test("toggles boolean setting when checkbox is clicked", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    // Find the Maintenance Mode checkbox (should be first unchecked boolean)
    const checkboxes = screen.getAllByRole("checkbox");
    const maintenanceCheckbox = checkboxes[0];

    expect(maintenanceCheckbox).not.toBeChecked();

    fireEvent.click(maintenanceCheckbox);

    expect(maintenanceCheckbox).toBeChecked();
  });

  test("changes number input value", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    const numberInputs = screen.getAllByRole("spinbutton");
    const maxFileSizeInput = numberInputs[0];

    expect(maxFileSizeInput.value).toBe("50");

    fireEvent.change(maxFileSizeInput, { target: { value: "100" } });

    expect(maxFileSizeInput.value).toBe("100");
  });

  test("calls API to save settings when Save Changes is clicked", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /Save Changes/i }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith(
        "/admin/settings",
        expect.objectContaining({
          settings: expect.any(Array),
        })
      );
    });
  });

  test("shows Saving... text while saving", async () => {
    setupMocks();
    api.post.mockImplementation(
      () => new Promise((resolve) => setTimeout(resolve, 100))
    );

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /Save Changes/i }));

    expect(screen.getByText("Saving...")).toBeInTheDocument();
  });

  test("shows success message after saving", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /Save Changes/i }));

    await waitFor(() => {
      expect(
        screen.getByText("Settings saved successfully")
      ).toBeInTheDocument();
    });
  });

  test("shows error message when save fails", async () => {
    setupMocks();
    api.post.mockRejectedValue({
      response: { data: { message: "Permission denied" } },
    });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /Save Changes/i }));

    await waitFor(() => {
      expect(screen.getByText("Permission denied")).toBeInTheDocument();
    });
  });

  test("resets settings to original values when Reset is clicked", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    // Change a number input
    const numberInputs = screen.getAllByRole("spinbutton");
    const maxFileSizeInput = numberInputs[0];
    fireEvent.change(maxFileSizeInput, { target: { value: "100" } });
    expect(maxFileSizeInput.value).toBe("100");

    // Click Reset
    fireEvent.click(screen.getByRole("button", { name: /Reset/i }));

    // Value should be reset to original
    expect(maxFileSizeInput.value).toBe("50");
  });

  test("clears message when Reset is clicked", async () => {
    setupMocks();
    api.post.mockRejectedValue({
      response: { data: { message: "Error occurred" } },
    });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    // Trigger an error message
    fireEvent.click(screen.getByRole("button", { name: /Save Changes/i }));

    await waitFor(() => {
      expect(screen.getByText("Error occurred")).toBeInTheDocument();
    });

    // Click Reset
    fireEvent.click(screen.getByRole("button", { name: /Reset/i }));

    expect(screen.queryByText("Error occurred")).not.toBeInTheDocument();
  });

  test("disables buttons while saving", async () => {
    setupMocks();
    api.post.mockImplementation(
      () => new Promise((resolve) => setTimeout(resolve, 500))
    );

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /Save Changes/i }));

    expect(screen.getByRole("button", { name: /Saving.../i })).toBeDisabled();
    expect(screen.getByRole("button", { name: /Reset/i })).toBeDisabled();
  });

  test("handles API error on initial load", async () => {
    api.get.mockRejectedValue(new Error("Network error"));

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("Failed to load settings")).toBeInTheDocument();
    });
  });

  test("fetches settings on component mount", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith("/admin/settings");
    });
  });

  test("shows default settings UI when no settings exist", async () => {
    api.get.mockResolvedValue({ data: [] });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(
        screen.getByText(
          "No settings configured yet. Here are some default settings:"
        )
      ).toBeInTheDocument();
    });
  });

  test("renders default maintenance mode checkbox when no settings", async () => {
    api.get.mockResolvedValue({ data: [] });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      // Multiple Maintenance Mode elements may exist from both sections
      const maintenanceElements = screen.getAllByText("Maintenance Mode");
      expect(maintenanceElements.length).toBeGreaterThanOrEqual(1);
      expect(screen.getByText("Enable maintenance mode")).toBeInTheDocument();
    });
  });

  test("renders default student registration checkbox when no settings", async () => {
    api.get.mockResolvedValue({ data: [] });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("Student Registration")).toBeInTheDocument();
      // Multiple elements may exist from both sections
      const elements = screen.getAllByText("Allow new student registrations");
      expect(elements.length).toBeGreaterThanOrEqual(1);
    });
  });

  test("renders default professor registration checkbox when no settings", async () => {
    api.get.mockResolvedValue({ data: [] });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("Professor Registration")).toBeInTheDocument();
      // Multiple elements may exist from both API-based and predefined UI
      const elements = screen.getAllByText("Allow new professor registrations");
      expect(elements.length).toBeGreaterThanOrEqual(1);
    });
  });

  test("renders default max file size input when no settings", async () => {
    api.get.mockResolvedValue({ data: [] });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("Maximum File Size (MB)")).toBeInTheDocument();
    });

    const numberInputs = screen.getAllByRole("spinbutton");
    // Find inputs with value 50 (default for max file size)
    const matchingInputs = numberInputs.filter((input) => input.value === "50");
    expect(matchingInputs.length).toBeGreaterThanOrEqual(1);
  });

  test("renders default session timeout input when no settings", async () => {
    api.get.mockResolvedValue({ data: [] });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("Session Timeout (minutes)")).toBeInTheDocument();
    });

    const numberInputs = screen.getAllByRole("spinbutton");
    // Find inputs with value 60 (default for session timeout)
    const matchingInputs = numberInputs.filter((input) => input.value === "60");
    expect(matchingInputs.length).toBeGreaterThanOrEqual(1);
  });

  test("renders default system announcement textarea when no settings", async () => {
    api.get.mockResolvedValue({ data: [] });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      // Multiple System Announcement elements may exist
      const elements = screen.getAllByText("System Announcement");
      expect(elements.length).toBeGreaterThanOrEqual(1);
      expect(
        screen.getByPlaceholderText(
          "Enter announcement message (leave blank for none)"
        )
      ).toBeInTheDocument();
    });
  });

  test("formats setting key names for display", async () => {
    setupMocks();

    render(<SystemSettingsForm />);

    await waitFor(() => {
      // MAINTENANCE_MODE should be formatted as "Maintenance Mode"
      expect(screen.getByText("Maintenance Mode")).toBeInTheDocument();
      // MAX_FILE_SIZE_MB should be formatted
      expect(screen.getByText("Max File Size Mb")).toBeInTheDocument();
    });
  });

  test("sends all settings in save request", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /Save Changes/i }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalled();
      const callArgs = api.post.mock.calls[0];
      expect(callArgs[1].settings.length).toBeGreaterThanOrEqual(6);
    });
  });

  test("refetches settings after successful save", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    // Initial fetch
    expect(api.get).toHaveBeenCalledTimes(1);

    fireEvent.click(screen.getByRole("button", { name: /Save Changes/i }));

    await waitFor(() => {
      // Should refetch after save
      expect(api.get).toHaveBeenCalledTimes(2);
    });
  });

  test("shows generic error message when save fails without response data", async () => {
    setupMocks();
    api.post.mockRejectedValue(new Error("Network error"));

    render(<SystemSettingsForm />);

    await waitFor(() => {
      expect(screen.getByText("System Settings")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /Save Changes/i }));

    await waitFor(() => {
      expect(screen.getByText("Failed to save settings")).toBeInTheDocument();
    });
  });
});
