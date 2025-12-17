import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, test, expect, vi, beforeEach } from "vitest";
import UserManagement from "../components/dashboard/UserManagement";
import api from "../services/apiService";

// Mock the API service
vi.mock("../services/apiService", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    delete: vi.fn(),
  },
}));

describe("UserManagement Tests", () => {
  const mockUsers = [
    {
      id: 1,
      username: "admin1",
      firstName: "Admin",
      lastName: "User",
      email: "admin@example.com",
      role: "ADMIN",
      enabled: true,
    },
    {
      id: 2,
      username: "prof1",
      firstName: "John",
      lastName: "Doe",
      email: "john.doe@example.com",
      role: "PROFESSOR",
      enabled: true,
    },
    {
      id: 3,
      username: "student1",
      firstName: "Jane",
      lastName: "Smith",
      email: "jane.smith@example.com",
      role: "STUDENT",
      enabled: false,
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  const setupMocks = () => {
    api.get.mockResolvedValue({ data: mockUsers });
  };

  test("shows loading state initially", () => {
    api.get.mockImplementation(() => new Promise(() => {}));

    render(<UserManagement />);

    expect(screen.getByText("Loading...")).toBeInTheDocument();
  });

  test("renders page header", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });
  });

  test("renders Create User button", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(
        screen.getByRole("button", { name: "Create User" })
      ).toBeInTheDocument();
    });
  });

  test("renders users table with headers", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("ID")).toBeInTheDocument();
      expect(screen.getByText("Username")).toBeInTheDocument();
      expect(screen.getByText("Name")).toBeInTheDocument();
      expect(screen.getByText("Email")).toBeInTheDocument();
      expect(screen.getByText("Role")).toBeInTheDocument();
      expect(screen.getByText("Status")).toBeInTheDocument();
      expect(screen.getByText("Actions")).toBeInTheDocument();
    });
  });

  test("fetches users on mount", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith("/admin/users");
    });
  });

  test("displays user data in table", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("admin1")).toBeInTheDocument();
      expect(screen.getByText("Admin User")).toBeInTheDocument();
      expect(screen.getByText("admin@example.com")).toBeInTheDocument();
    });
  });

  test("displays professor user data", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("prof1")).toBeInTheDocument();
      expect(screen.getByText("John Doe")).toBeInTheDocument();
      expect(screen.getByText("john.doe@example.com")).toBeInTheDocument();
    });
  });

  test("displays student user data", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("student1")).toBeInTheDocument();
      expect(screen.getByText("Jane Smith")).toBeInTheDocument();
      expect(screen.getByText("jane.smith@example.com")).toBeInTheDocument();
    });
  });

  test("displays role badges", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("ADMIN")).toBeInTheDocument();
      expect(screen.getByText("PROFESSOR")).toBeInTheDocument();
      expect(screen.getByText("STUDENT")).toBeInTheDocument();
    });
  });

  test("displays Active status for enabled users", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      const activeStatuses = screen.getAllByText("Active");
      expect(activeStatuses.length).toBe(2); // admin1 and prof1 are enabled
    });
  });

  test("displays Locked status for disabled users", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("Locked")).toBeInTheDocument();
    });
  });

  test("displays action buttons for each user", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      const resetButtons = screen.getAllByText("Reset Password");
      expect(resetButtons.length).toBe(3);

      const deleteButtons = screen.getAllByText("Delete");
      expect(deleteButtons.length).toBe(3);
    });
  });

  test("displays Lock button for enabled users", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      const lockButtons = screen.getAllByText("Lock");
      expect(lockButtons.length).toBe(2); // admin1 and prof1
    });
  });

  test("displays Unlock button for disabled users", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("Unlock")).toBeInTheDocument();
    });
  });

  test("opens create modal when Create User button is clicked", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    expect(screen.getByText("Create New User")).toBeInTheDocument();
  });

  test("renders create modal form fields", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    expect(screen.getByPlaceholderText("Username")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Email")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Password")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("First Name")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Last Name")).toBeInTheDocument();
  });

  test("renders role select with options in create modal", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    const select = screen.getByRole("combobox");
    expect(select).toBeInTheDocument();

    // Check options
    expect(screen.getByRole("option", { name: "Student" })).toBeInTheDocument();
    expect(
      screen.getByRole("option", { name: "Professor" })
    ).toBeInTheDocument();
    expect(screen.getByRole("option", { name: "Admin" })).toBeInTheDocument();
  });

  test("default role is STUDENT in create modal", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    const select = screen.getByRole("combobox");
    expect(select.value).toBe("STUDENT");
  });

  test("renders Cancel and Create buttons in modal", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    expect(screen.getByRole("button", { name: "Cancel" })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Create" })).toBeInTheDocument();
  });

  test("closes modal when Cancel is clicked", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));
    expect(screen.getByText("Create New User")).toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Cancel" }));

    expect(screen.queryByText("Create New User")).not.toBeInTheDocument();
  });

  test("updates form fields when typing", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    const usernameInput = screen.getByPlaceholderText("Username");
    const emailInput = screen.getByPlaceholderText("Email");

    fireEvent.change(usernameInput, { target: { value: "newuser" } });
    fireEvent.change(emailInput, { target: { value: "newuser@test.com" } });

    expect(usernameInput.value).toBe("newuser");
    expect(emailInput.value).toBe("newuser@test.com");
  });

  test("submits create form and calls API", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    fireEvent.change(screen.getByPlaceholderText("Username"), {
      target: { value: "newuser" },
    });
    fireEvent.change(screen.getByPlaceholderText("Email"), {
      target: { value: "newuser@test.com" },
    });
    fireEvent.change(screen.getByPlaceholderText("Password"), {
      target: { value: "password123" },
    });
    fireEvent.change(screen.getByPlaceholderText("First Name"), {
      target: { value: "New" },
    });
    fireEvent.change(screen.getByPlaceholderText("Last Name"), {
      target: { value: "User" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Create" }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith("/admin/users", {
        username: "newuser",
        email: "newuser@test.com",
        password: "password123",
        firstName: "New",
        lastName: "User",
        role: "STUDENT",
      });
    });
  });

  test("closes modal and refetches users after successful create", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    fireEvent.change(screen.getByPlaceholderText("Username"), {
      target: { value: "newuser" },
    });
    fireEvent.change(screen.getByPlaceholderText("Email"), {
      target: { value: "newuser@test.com" },
    });
    fireEvent.change(screen.getByPlaceholderText("Password"), {
      target: { value: "password123" },
    });
    fireEvent.change(screen.getByPlaceholderText("First Name"), {
      target: { value: "New" },
    });
    fireEvent.change(screen.getByPlaceholderText("Last Name"), {
      target: { value: "User" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Create" }));

    await waitFor(() => {
      // Modal should close
      expect(screen.queryByText("Create New User")).not.toBeInTheDocument();
      // Should refetch users (called twice: initial + after create)
      expect(api.get).toHaveBeenCalledTimes(2);
    });
  });

  test("shows error alert when create fails", async () => {
    setupMocks();
    api.post.mockRejectedValue({
      response: { data: "Username already exists" },
    });
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    fireEvent.change(screen.getByPlaceholderText("Username"), {
      target: { value: "existinguser" },
    });
    fireEvent.change(screen.getByPlaceholderText("Email"), {
      target: { value: "existing@test.com" },
    });
    fireEvent.change(screen.getByPlaceholderText("Password"), {
      target: { value: "password123" },
    });
    fireEvent.change(screen.getByPlaceholderText("First Name"), {
      target: { value: "Existing" },
    });
    fireEvent.change(screen.getByPlaceholderText("Last Name"), {
      target: { value: "User" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Create" }));

    await waitFor(() => {
      expect(alertSpy).toHaveBeenCalledWith(
        "Failed to create user: Username already exists"
      );
    });

    alertSpy.mockRestore();
  });

  test("calls delete API when Delete is clicked and confirmed", async () => {
    setupMocks();
    api.delete.mockResolvedValue({ data: { success: true } });
    const confirmSpy = vi
      .spyOn(window, "confirm")
      .mockImplementation(() => true);

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    const deleteButtons = screen.getAllByText("Delete");
    fireEvent.click(deleteButtons[0]);

    await waitFor(() => {
      expect(confirmSpy).toHaveBeenCalledWith(
        "Are you sure you want to delete this user?"
      );
      expect(api.delete).toHaveBeenCalledWith("/admin/users/1");
    });

    confirmSpy.mockRestore();
  });

  test("does not call delete API when cancelled", async () => {
    setupMocks();
    const confirmSpy = vi
      .spyOn(window, "confirm")
      .mockImplementation(() => false);

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    const deleteButtons = screen.getAllByText("Delete");
    fireEvent.click(deleteButtons[0]);

    expect(confirmSpy).toHaveBeenCalled();
    expect(api.delete).not.toHaveBeenCalled();

    confirmSpy.mockRestore();
  });

  test("shows error alert when delete fails", async () => {
    setupMocks();
    api.delete.mockRejectedValue(new Error("Delete failed"));
    const confirmSpy = vi
      .spyOn(window, "confirm")
      .mockImplementation(() => true);
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    const deleteButtons = screen.getAllByText("Delete");
    fireEvent.click(deleteButtons[0]);

    await waitFor(() => {
      expect(alertSpy).toHaveBeenCalledWith("Failed to delete user");
    });

    confirmSpy.mockRestore();
    alertSpy.mockRestore();
  });

  test("calls reset password API with new password", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });
    const promptSpy = vi
      .spyOn(window, "prompt")
      .mockImplementation(() => "newpassword123");
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    const resetButtons = screen.getAllByText("Reset Password");
    fireEvent.click(resetButtons[0]);

    await waitFor(() => {
      expect(promptSpy).toHaveBeenCalledWith("Enter new password:");
      expect(api.post).toHaveBeenCalledWith("/admin/users/1/reset-password", {
        newPassword: "newpassword123",
      });
      expect(alertSpy).toHaveBeenCalledWith("Password reset successfully");
    });

    promptSpy.mockRestore();
    alertSpy.mockRestore();
  });

  test("does not call reset password API when prompt is cancelled", async () => {
    setupMocks();
    const promptSpy = vi.spyOn(window, "prompt").mockImplementation(() => null);

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    const resetButtons = screen.getAllByText("Reset Password");
    fireEvent.click(resetButtons[0]);

    expect(promptSpy).toHaveBeenCalled();
    expect(api.post).not.toHaveBeenCalled();

    promptSpy.mockRestore();
  });

  test("shows error alert when reset password fails", async () => {
    setupMocks();
    api.post.mockRejectedValue(new Error("Reset failed"));
    const promptSpy = vi
      .spyOn(window, "prompt")
      .mockImplementation(() => "newpassword");
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    const resetButtons = screen.getAllByText("Reset Password");
    fireEvent.click(resetButtons[0]);

    await waitFor(() => {
      expect(alertSpy).toHaveBeenCalledWith("Failed to reset password");
    });

    promptSpy.mockRestore();
    alertSpy.mockRestore();
  });

  test("calls lock API when Lock button is clicked", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    const lockButtons = screen.getAllByText("Lock");
    fireEvent.click(lockButtons[0]);

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith("/admin/users/1/lock");
    });
  });

  test("calls unlock API when Unlock button is clicked", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText("Unlock"));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith("/admin/users/3/unlock");
    });
  });

  test("refetches users after lock toggle", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    // Initial fetch
    expect(api.get).toHaveBeenCalledTimes(1);

    const lockButtons = screen.getAllByText("Lock");
    fireEvent.click(lockButtons[0]);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledTimes(2);
    });
  });

  test("shows error alert when lock toggle fails", async () => {
    setupMocks();
    api.post.mockRejectedValue(new Error("Lock failed"));
    const alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    const lockButtons = screen.getAllByText("Lock");
    fireEvent.click(lockButtons[0]);

    await waitFor(() => {
      expect(alertSpy).toHaveBeenCalledWith("Failed to toggle lock status");
    });

    alertSpy.mockRestore();
  });

  test("changes role in create form", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    const select = screen.getByRole("combobox");
    fireEvent.change(select, { target: { value: "PROFESSOR" } });

    expect(select.value).toBe("PROFESSOR");
  });

  test("handles API error on initial fetch", async () => {
    api.get.mockRejectedValue(new Error("Network error"));
    const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => {});

    render(<UserManagement />);

    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith(
        "Failed to fetch users:",
        expect.any(Error)
      );
    });

    consoleSpy.mockRestore();
  });

  test("renders empty table when no users", async () => {
    api.get.mockResolvedValue({ data: [] });

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
      // Table headers should still be present
      expect(screen.getByText("Username")).toBeInTheDocument();
    });

    // No user rows
    expect(screen.queryByText("admin1")).not.toBeInTheDocument();
  });

  test("all form fields have required attribute", async () => {
    setupMocks();

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    expect(screen.getByPlaceholderText("Username")).toHaveAttribute("required");
    expect(screen.getByPlaceholderText("Email")).toHaveAttribute("required");
    expect(screen.getByPlaceholderText("Password")).toHaveAttribute("required");
    expect(screen.getByPlaceholderText("First Name")).toHaveAttribute(
      "required"
    );
    expect(screen.getByPlaceholderText("Last Name")).toHaveAttribute(
      "required"
    );
  });

  test("submits form with different role selected", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    fireEvent.change(screen.getByPlaceholderText("Username"), {
      target: { value: "profuser" },
    });
    fireEvent.change(screen.getByPlaceholderText("Email"), {
      target: { value: "prof@test.com" },
    });
    fireEvent.change(screen.getByPlaceholderText("Password"), {
      target: { value: "password123" },
    });
    fireEvent.change(screen.getByPlaceholderText("First Name"), {
      target: { value: "Prof" },
    });
    fireEvent.change(screen.getByPlaceholderText("Last Name"), {
      target: { value: "User" },
    });

    // Change role to PROFESSOR
    fireEvent.change(screen.getByRole("combobox"), {
      target: { value: "PROFESSOR" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Create" }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith(
        "/admin/users",
        expect.objectContaining({
          role: "PROFESSOR",
        })
      );
    });
  });

  test("resets form data after successful create", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<UserManagement />);

    await waitFor(() => {
      expect(screen.getByText("User Management")).toBeInTheDocument();
    });

    // Open modal and fill form
    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    fireEvent.change(screen.getByPlaceholderText("Username"), {
      target: { value: "newuser" },
    });
    fireEvent.change(screen.getByPlaceholderText("Email"), {
      target: { value: "newuser@test.com" },
    });
    fireEvent.change(screen.getByPlaceholderText("Password"), {
      target: { value: "password123" },
    });
    fireEvent.change(screen.getByPlaceholderText("First Name"), {
      target: { value: "New" },
    });
    fireEvent.change(screen.getByPlaceholderText("Last Name"), {
      target: { value: "User" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Create" }));

    await waitFor(() => {
      expect(screen.queryByText("Create New User")).not.toBeInTheDocument();
    });

    // Reopen modal - form should be reset
    fireEvent.click(screen.getByRole("button", { name: "Create User" }));

    expect(screen.getByPlaceholderText("Username").value).toBe("");
    expect(screen.getByPlaceholderText("Email").value).toBe("");
    expect(screen.getByPlaceholderText("Password").value).toBe("");
    expect(screen.getByPlaceholderText("First Name").value).toBe("");
    expect(screen.getByPlaceholderText("Last Name").value).toBe("");
    expect(screen.getByRole("combobox").value).toBe("STUDENT");
  });
});
