import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, test, expect, vi, beforeEach } from "vitest";
import StudentAssignment from "../components/dashboard/StudentAssignment";
import api from "../services/apiService";

// Mock the API service
vi.mock("../services/apiService", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    delete: vi.fn(),
  },
}));

describe("StudentAssignment Tests", () => {
  const mockStudents = [
    {
      id: 1,
      firstName: "John",
      lastName: "Doe",
      username: "johndoe",
    },
    {
      id: 2,
      firstName: "Jane",
      lastName: "Smith",
      username: "janesmith",
    },
  ];

  const mockProfessors = [
    {
      id: 10,
      firstName: "Dr. Robert",
      lastName: "Brown",
      username: "rbrown",
    },
    {
      id: 11,
      firstName: "Dr. Alice",
      lastName: "Johnson",
      username: "ajohnson",
    },
  ];

  const mockAssignments = [
    {
      id: 100,
      studentName: "John Doe",
      professorName: "Dr. Robert Brown",
      notes: "Capstone project supervision",
      assignedAt: "2025-12-15T10:00:00Z",
    },
    {
      id: 101,
      studentName: "Jane Smith",
      professorName: "Dr. Alice Johnson",
      notes: null,
      assignedAt: "2025-12-16T14:30:00Z",
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    // Reset window methods
    vi.spyOn(window, "alert").mockImplementation(() => {});
    vi.spyOn(window, "confirm").mockImplementation(() => true);
  });

  const setupMocks = () => {
    api.get.mockImplementation((url) => {
      if (url === "/admin/users?role=STUDENT") {
        return Promise.resolve({ data: mockStudents });
      }
      if (url === "/admin/users?role=PROFESSOR") {
        return Promise.resolve({ data: mockProfessors });
      }
      if (url === "/admin/assignments") {
        return Promise.resolve({ data: mockAssignments });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });
  };

  // Helper to find select by label text (since labels aren't associated via htmlFor)
  const getSelectByLabelText = (labelText) => {
    const label = screen.getByText(labelText);
    return label.parentElement.querySelector("select");
  };

  test("shows loading state initially", () => {
    api.get.mockImplementation(() => new Promise(() => {}));

    render(<StudentAssignment />);

    expect(screen.getByText("Loading...")).toBeInTheDocument();
  });

  test("renders page header", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignment")
      ).toBeInTheDocument();
    });
  });

  test("renders Create Assignment form section", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Create Assignment")).toBeInTheDocument();
    });
  });

  test("renders student dropdown with options", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Select Student")).toBeInTheDocument();
      expect(screen.getByText("John Doe (johndoe)")).toBeInTheDocument();
      expect(screen.getByText("Jane Smith (janesmith)")).toBeInTheDocument();
    });
  });

  test("renders professor dropdown with options", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Select Professor")).toBeInTheDocument();
      expect(screen.getByText("Dr. Robert Brown (rbrown)")).toBeInTheDocument();
      expect(
        screen.getByText("Dr. Alice Johnson (ajohnson)")
      ).toBeInTheDocument();
    });
  });

  test("renders notes textarea", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Notes (Optional)")).toBeInTheDocument();
      expect(
        screen.getByPlaceholderText("Add any notes about this assignment...")
      ).toBeInTheDocument();
    });
  });

  test("renders Assign Student button", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(
        screen.getByRole("button", { name: /Assign Student/i })
      ).toBeInTheDocument();
    });
  });

  test("renders Current Assignments section with count", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Current Assignments (2)")).toBeInTheDocument();
    });
  });

  test("displays existing assignments", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("John Doe")).toBeInTheDocument();
      expect(
        screen.getByText("Assigned to: Dr. Robert Brown")
      ).toBeInTheDocument();
      expect(screen.getByText("Jane Smith")).toBeInTheDocument();
      expect(
        screen.getByText("Assigned to: Dr. Alice Johnson")
      ).toBeInTheDocument();
    });
  });

  test("displays assignment notes when present", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(
        screen.getByText("Note: Capstone project supervision")
      ).toBeInTheDocument();
    });
  });

  test("displays Remove buttons for each assignment", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      const removeButtons = screen.getAllByRole("button", { name: /Remove/i });
      expect(removeButtons.length).toBe(2);
    });
  });

  test("shows empty state when no assignments", async () => {
    api.get.mockImplementation((url) => {
      if (url === "/admin/users?role=STUDENT") {
        return Promise.resolve({ data: mockStudents });
      }
      if (url === "/admin/users?role=PROFESSOR") {
        return Promise.resolve({ data: mockProfessors });
      }
      if (url === "/admin/assignments") {
        return Promise.resolve({ data: [] });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("No assignments yet")).toBeInTheDocument();
      expect(screen.getByText("Current Assignments (0)")).toBeInTheDocument();
    });
  });

  test("allows selecting a student", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Create Assignment")).toBeInTheDocument();
    });

    const studentSelect = getSelectByLabelText("Student");
    fireEvent.change(studentSelect, { target: { value: "1" } });

    expect(studentSelect.value).toBe("1");
  });

  test("allows selecting a professor", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Create Assignment")).toBeInTheDocument();
    });

    const professorSelect = getSelectByLabelText("Professor");
    fireEvent.change(professorSelect, { target: { value: "10" } });

    expect(professorSelect.value).toBe("10");
  });

  test("allows entering notes", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Create Assignment")).toBeInTheDocument();
    });

    const notesTextarea = screen.getByPlaceholderText(
      "Add any notes about this assignment..."
    );
    fireEvent.change(notesTextarea, {
      target: { value: "Test assignment notes" },
    });

    expect(notesTextarea.value).toBe("Test assignment notes");
  });

  test("select elements have required attribute", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Create Assignment")).toBeInTheDocument();
    });

    // Both selects should have required attribute for HTML5 validation
    const studentSelect = getSelectByLabelText("Student");
    const professorSelect = getSelectByLabelText("Professor");

    expect(studentSelect).toHaveAttribute("required");
    expect(professorSelect).toHaveAttribute("required");
  });

  test("submits assignment form with correct data", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { id: 102 } });

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Create Assignment")).toBeInTheDocument();
    });

    // Select student
    const studentSelect = getSelectByLabelText("Student");
    fireEvent.change(studentSelect, { target: { value: "1" } });

    // Select professor
    const professorSelect = getSelectByLabelText("Professor");
    fireEvent.change(professorSelect, { target: { value: "10" } });

    // Enter notes
    const notesTextarea = screen.getByPlaceholderText(
      "Add any notes about this assignment..."
    );
    fireEvent.change(notesTextarea, { target: { value: "Test notes" } });

    // Submit form
    fireEvent.click(screen.getByRole("button", { name: /Assign Student/i }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith("/admin/assignments", {
        studentId: 1,
        professorId: 10,
        notes: "Test notes",
      });
    });
  });

  test("shows success alert after successful assignment", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { id: 102 } });

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Create Assignment")).toBeInTheDocument();
    });

    const studentSelect = getSelectByLabelText("Student");
    fireEvent.change(studentSelect, { target: { value: "1" } });

    const professorSelect = getSelectByLabelText("Professor");
    fireEvent.change(professorSelect, { target: { value: "10" } });

    fireEvent.click(screen.getByRole("button", { name: /Assign Student/i }));

    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith(
        "Student assigned successfully"
      );
    });
  });

  test("resets form after successful assignment", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { id: 102 } });

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Create Assignment")).toBeInTheDocument();
    });

    const studentSelect = getSelectByLabelText("Student");
    const professorSelect = getSelectByLabelText("Professor");
    const notesTextarea = screen.getByPlaceholderText(
      "Add any notes about this assignment..."
    );

    fireEvent.change(studentSelect, { target: { value: "1" } });
    fireEvent.change(professorSelect, { target: { value: "10" } });
    fireEvent.change(notesTextarea, { target: { value: "Test notes" } });

    fireEvent.click(screen.getByRole("button", { name: /Assign Student/i }));

    await waitFor(() => {
      expect(studentSelect.value).toBe("");
      expect(professorSelect.value).toBe("");
      expect(notesTextarea.value).toBe("");
    });
  });

  test("shows error alert when assignment fails", async () => {
    setupMocks();
    api.post.mockRejectedValue({
      response: { data: "Student already assigned" },
    });

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Create Assignment")).toBeInTheDocument();
    });

    const studentSelect = getSelectByLabelText("Student");
    fireEvent.change(studentSelect, { target: { value: "1" } });

    const professorSelect = getSelectByLabelText("Professor");
    fireEvent.change(professorSelect, { target: { value: "10" } });

    fireEvent.click(screen.getByRole("button", { name: /Assign Student/i }));

    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith(
        "Failed to assign student: Student already assigned"
      );
    });
  });

  test("remove button prompts for confirmation", async () => {
    setupMocks();
    api.delete.mockResolvedValue({});

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("John Doe")).toBeInTheDocument();
    });

    const removeButtons = screen.getAllByRole("button", { name: /Remove/i });
    fireEvent.click(removeButtons[0]);

    expect(window.confirm).toHaveBeenCalledWith(
      "Are you sure you want to remove this assignment?"
    );
  });

  test("calls delete API when remove is confirmed", async () => {
    setupMocks();
    api.delete.mockResolvedValue({});

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("John Doe")).toBeInTheDocument();
    });

    const removeButtons = screen.getAllByRole("button", { name: /Remove/i });
    fireEvent.click(removeButtons[0]);

    await waitFor(() => {
      expect(api.delete).toHaveBeenCalledWith("/admin/assignments/100");
    });
  });

  test("does not call delete API when remove is cancelled", async () => {
    setupMocks();
    window.confirm.mockReturnValue(false);

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("John Doe")).toBeInTheDocument();
    });

    const removeButtons = screen.getAllByRole("button", { name: /Remove/i });
    fireEvent.click(removeButtons[0]);

    expect(api.delete).not.toHaveBeenCalled();
  });

  test("shows error alert when remove fails", async () => {
    setupMocks();
    api.delete.mockRejectedValue(new Error("Delete failed"));

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("John Doe")).toBeInTheDocument();
    });

    const removeButtons = screen.getAllByRole("button", { name: /Remove/i });
    fireEvent.click(removeButtons[0]);

    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith("Failed to remove assignment");
    });
  });

  test("handles API error gracefully on initial load", async () => {
    const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => {});
    api.get.mockRejectedValue(new Error("Network error"));

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith(
        "Failed to fetch data:",
        expect.any(Error)
      );
    });

    expect(
      screen.getByText("Student-Professor Assignment")
    ).toBeInTheDocument();

    consoleSpy.mockRestore();
  });

  test("fetches all data on component mount", async () => {
    setupMocks();

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith("/admin/users?role=STUDENT");
      expect(api.get).toHaveBeenCalledWith("/admin/users?role=PROFESSOR");
      expect(api.get).toHaveBeenCalledWith("/admin/assignments");
    });
  });

  test("refreshes data after successful assignment", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { id: 102 } });

    render(<StudentAssignment />);

    await waitFor(() => {
      expect(screen.getByText("Create Assignment")).toBeInTheDocument();
    });

    // Clear initial calls
    api.get.mockClear();

    const studentSelect = getSelectByLabelText("Student");
    fireEvent.change(studentSelect, { target: { value: "1" } });

    const professorSelect = getSelectByLabelText("Professor");
    fireEvent.change(professorSelect, { target: { value: "10" } });

    fireEvent.click(screen.getByRole("button", { name: /Assign Student/i }));

    await waitFor(() => {
      // Should refetch data after assignment
      expect(api.get).toHaveBeenCalledWith("/admin/users?role=STUDENT");
      expect(api.get).toHaveBeenCalledWith("/admin/users?role=PROFESSOR");
      expect(api.get).toHaveBeenCalledWith("/admin/assignments");
    });
  });
});
