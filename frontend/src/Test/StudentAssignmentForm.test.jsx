import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, test, expect, vi, beforeEach } from "vitest";
import StudentAssignmentForm from "../components/dashboard/StudentAssignmentForm";
import api from "../services/apiService";

// Mock the API service
vi.mock("../services/apiService", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    delete: vi.fn(),
  },
}));

describe("StudentAssignmentForm Tests", () => {
  const mockProfessors = [
    { id: 1, name: "Dr. John Smith", email: "jsmith@university.edu" },
    { id: 2, name: "Dr. Alice Brown", email: "abrown@university.edu" },
  ];

  const mockStudents = [
    { id: 10, name: "Student One", email: "student1@edu.com" },
    { id: 11, name: "Student Two", email: "student2@edu.com" },
    { id: 12, name: "Student Three", email: "student3@edu.com" },
  ];

  const mockAssignments = [
    {
      id: 100,
      professorId: 1,
      studentId: 10,
      createdAt: "2025-12-15T10:00:00Z",
    },
    {
      id: 101,
      professorId: 2,
      studentId: 11,
      createdAt: "2025-12-16T14:30:00Z",
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    vi.spyOn(window, "confirm").mockImplementation(() => true);
  });

  const setupMocks = () => {
    api.get.mockImplementation((url) => {
      if (url === "/admin/users?role=PROFESSOR") {
        return Promise.resolve({ data: mockProfessors });
      }
      if (url === "/admin/users?role=STUDENT") {
        return Promise.resolve({ data: mockStudents });
      }
      if (url === "/admin/assignments") {
        return Promise.resolve({ data: mockAssignments });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });
  };

  // Helper to find select by label text
  const getSelectByLabelText = (labelText) => {
    const label = screen.getByText(labelText);
    return label.parentElement.querySelector("select");
  };

  test("shows loading state initially", () => {
    api.get.mockImplementation(() => new Promise(() => {}));

    render(<StudentAssignmentForm />);

    expect(screen.getByText("Loading...")).toBeInTheDocument();
  });

  test("renders page header", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });
  });

  test("renders New Assignment button", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByRole("button", { name: /New Assignment/i })
      ).toBeInTheDocument();
    });
  });

  test("shows assignment form when New Assignment is clicked", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(
        screen.getByText("Assign Students to Professor")
      ).toBeInTheDocument();
    });
  });

  test("button text changes to Cancel when form is shown", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    const button = screen.getByRole("button", { name: /New Assignment/i });
    fireEvent.click(button);

    await waitFor(() => {
      expect(
        screen.getByRole("button", { name: /Cancel/i })
      ).toBeInTheDocument();
    });
  });

  test("hides form when Cancel is clicked", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    // Show form
    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));
    await waitFor(() => {
      expect(
        screen.getByText("Assign Students to Professor")
      ).toBeInTheDocument();
    });

    // Hide form
    fireEvent.click(screen.getByRole("button", { name: /Cancel/i }));

    await waitFor(() => {
      expect(
        screen.queryByText("Assign Students to Professor")
      ).not.toBeInTheDocument();
    });
  });

  test("renders professor dropdown in form", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(screen.getByText("Select Professor")).toBeInTheDocument();
      expect(screen.getByText("-- Select Professor --")).toBeInTheDocument();
    });
  });

  test("renders professor options with student counts", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      // Dr. John Smith has 1 student assigned
      expect(
        screen.getByText(/Dr. John Smith.*jsmith@university.edu.*1 students/i)
      ).toBeInTheDocument();
      // Dr. Alice Brown has 1 student assigned
      expect(
        screen.getByText(/Dr. Alice Brown.*abrown@university.edu.*1 students/i)
      ).toBeInTheDocument();
    });
  });

  test("renders student checkboxes in form", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(
        screen.getByText(/Student One.*student1@edu.com/)
      ).toBeInTheDocument();
      expect(
        screen.getByText(/Student Two.*student2@edu.com/)
      ).toBeInTheDocument();
      expect(
        screen.getByText(/Student Three.*student3@edu.com/)
      ).toBeInTheDocument();
    });
  });

  test("shows already assigned indicator for assigned students", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(
        screen.getByText(/Already assigned to Dr. John Smith/i)
      ).toBeInTheDocument();
      expect(
        screen.getByText(/Already assigned to Dr. Alice Brown/i)
      ).toBeInTheDocument();
    });
  });

  test("displays selected students count", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(
        screen.getByText(/Select Students \(0 selected\)/)
      ).toBeInTheDocument();
    });
  });

  test("toggles student selection when checkbox is clicked", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(
        screen.getByText(/Select Students \(0 selected\)/)
      ).toBeInTheDocument();
    });

    // Select a student
    const checkbox = screen.getByRole("checkbox", { name: /Student Three/i });
    fireEvent.click(checkbox);

    expect(
      screen.getByText(/Select Students \(1 selected\)/)
    ).toBeInTheDocument();
    expect(checkbox).toBeChecked();

    // Deselect the student
    fireEvent.click(checkbox);

    expect(
      screen.getByText(/Select Students \(0 selected\)/)
    ).toBeInTheDocument();
    expect(checkbox).not.toBeChecked();
  });

  test("renders Assign Students submit button", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(
        screen.getByRole("button", { name: /Assign Students/i })
      ).toBeInTheDocument();
    });
  });

  test("professor select has required attribute", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(
        screen.getByText("Assign Students to Professor")
      ).toBeInTheDocument();
    });

    // Verify the professor select has required attribute
    const professorSelect = getSelectByLabelText("Select Professor");
    expect(professorSelect).toHaveAttribute("required");
  });

  test("renders Current Assignments table", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(screen.getByText("Current Assignments")).toBeInTheDocument();
    });
  });

  test("displays table headers correctly", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(screen.getByText("Professor")).toBeInTheDocument();
      expect(screen.getByText("Student")).toBeInTheDocument();
      expect(screen.getByText("Assigned Date")).toBeInTheDocument();
      expect(screen.getByText("Actions")).toBeInTheDocument();
    });
  });

  test("displays assignments in table", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(screen.getByText("Dr. John Smith")).toBeInTheDocument();
      expect(screen.getByText("jsmith@university.edu")).toBeInTheDocument();
      expect(screen.getByText("Student One")).toBeInTheDocument();
      expect(screen.getByText("student1@edu.com")).toBeInTheDocument();
    });
  });

  test("displays Remove buttons for each assignment", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      const removeButtons = screen.getAllByRole("button", { name: /Remove/i });
      expect(removeButtons.length).toBe(2);
    });
  });

  test("shows empty state when no assignments", async () => {
    api.get.mockImplementation((url) => {
      if (url === "/admin/users?role=PROFESSOR") {
        return Promise.resolve({ data: mockProfessors });
      }
      if (url === "/admin/users?role=STUDENT") {
        return Promise.resolve({ data: mockStudents });
      }
      if (url === "/admin/assignments") {
        return Promise.resolve({ data: [] });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(screen.getByText("No assignments yet")).toBeInTheDocument();
    });
  });

  test("renders statistics summary cards", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(screen.getByText("Total Professors")).toBeInTheDocument();
      expect(screen.getByText("Total Students")).toBeInTheDocument();
      expect(screen.getByText("Active Assignments")).toBeInTheDocument();
    });
  });

  test("displays correct statistics values", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      // 2 professors
      const professorsCard = screen.getByText("Total Professors").parentElement;
      expect(professorsCard).toHaveTextContent("2");

      // 3 students
      const studentsCard = screen.getByText("Total Students").parentElement;
      expect(studentsCard).toHaveTextContent("3");

      // 2 assignments
      const assignmentsCard =
        screen.getByText("Active Assignments").parentElement;
      expect(assignmentsCard).toHaveTextContent("2");
    });
  });

  test("submits assignment form with correct data", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(
        screen.getByText("Assign Students to Professor")
      ).toBeInTheDocument();
    });

    // Select professor
    const professorSelect = getSelectByLabelText("Select Professor");
    fireEvent.change(professorSelect, { target: { value: "1" } });

    // Select a student
    const studentCheckbox = screen.getByRole("checkbox", {
      name: /Student Three/i,
    });
    fireEvent.click(studentCheckbox);

    // Submit form
    fireEvent.click(screen.getByRole("button", { name: /Assign Students/i }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith("/admin/assignments", {
        professorId: "1",
        studentIds: [12],
      });
    });
  });

  test("shows success message after successful assignment", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(
        screen.getByText("Assign Students to Professor")
      ).toBeInTheDocument();
    });

    // Select professor
    const professorSelect = getSelectByLabelText("Select Professor");
    fireEvent.change(professorSelect, { target: { value: "1" } });

    // Select a student
    fireEvent.click(screen.getByRole("checkbox", { name: /Student Three/i }));

    // Submit form
    fireEvent.click(screen.getByRole("button", { name: /Assign Students/i }));

    await waitFor(() => {
      expect(
        screen.getByText(/Successfully assigned 1 student\(s\)/)
      ).toBeInTheDocument();
    });
  });

  test("hides form after successful assignment", async () => {
    setupMocks();
    api.post.mockResolvedValue({ data: { success: true } });

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(
        screen.getByText("Assign Students to Professor")
      ).toBeInTheDocument();
    });

    // Select professor and student
    const professorSelect = getSelectByLabelText("Select Professor");
    fireEvent.change(professorSelect, { target: { value: "1" } });
    fireEvent.click(screen.getByRole("checkbox", { name: /Student Three/i }));

    // Submit form
    fireEvent.click(screen.getByRole("button", { name: /Assign Students/i }));

    await waitFor(() => {
      expect(
        screen.queryByText("Assign Students to Professor")
      ).not.toBeInTheDocument();
    });
  });

  test("shows error message when assignment fails", async () => {
    setupMocks();
    api.post.mockRejectedValue({
      response: { data: { message: "Assignment already exists" } },
    });

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(
        screen.getByText("Assign Students to Professor")
      ).toBeInTheDocument();
    });

    // Select professor and student
    const professorSelect = getSelectByLabelText("Select Professor");
    fireEvent.change(professorSelect, { target: { value: "1" } });
    fireEvent.click(screen.getByRole("checkbox", { name: /Student Three/i }));

    // Submit form
    fireEvent.click(screen.getByRole("button", { name: /Assign Students/i }));

    await waitFor(() => {
      expect(screen.getByText("Assignment already exists")).toBeInTheDocument();
    });
  });

  test("Remove button prompts for confirmation", async () => {
    setupMocks();
    api.delete.mockResolvedValue({});

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(screen.getByText("Dr. John Smith")).toBeInTheDocument();
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

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(screen.getByText("Dr. John Smith")).toBeInTheDocument();
    });

    const removeButtons = screen.getAllByRole("button", { name: /Remove/i });
    fireEvent.click(removeButtons[0]);

    await waitFor(() => {
      expect(api.delete).toHaveBeenCalledWith("/admin/assignments/100");
    });
  });

  test("shows success message after successful removal", async () => {
    setupMocks();
    api.delete.mockResolvedValue({});

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(screen.getByText("Dr. John Smith")).toBeInTheDocument();
    });

    const removeButtons = screen.getAllByRole("button", { name: /Remove/i });
    fireEvent.click(removeButtons[0]);

    await waitFor(() => {
      expect(
        screen.getByText("Assignment removed successfully")
      ).toBeInTheDocument();
    });
  });

  test("does not call delete API when remove is cancelled", async () => {
    setupMocks();
    window.confirm.mockReturnValue(false);

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(screen.getByText("Dr. John Smith")).toBeInTheDocument();
    });

    const removeButtons = screen.getAllByRole("button", { name: /Remove/i });
    fireEvent.click(removeButtons[0]);

    expect(api.delete).not.toHaveBeenCalled();
  });

  test("shows error message when removal fails", async () => {
    setupMocks();
    api.delete.mockRejectedValue(new Error("Delete failed"));

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(screen.getByText("Dr. John Smith")).toBeInTheDocument();
    });

    const removeButtons = screen.getAllByRole("button", { name: /Remove/i });
    fireEvent.click(removeButtons[0]);

    await waitFor(() => {
      expect(
        screen.getByText("Failed to remove assignment")
      ).toBeInTheDocument();
    });
  });

  test("handles API error on initial load", async () => {
    api.get.mockRejectedValue(new Error("Network error"));

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(screen.getByText("Failed to load data")).toBeInTheDocument();
    });
  });

  test("fetches all data on component mount", async () => {
    setupMocks();

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledWith("/admin/users?role=PROFESSOR");
      expect(api.get).toHaveBeenCalledWith("/admin/users?role=STUDENT");
      expect(api.get).toHaveBeenCalledWith("/admin/assignments");
    });
  });

  test("shows No students available when student list is empty", async () => {
    api.get.mockImplementation((url) => {
      if (url === "/admin/users?role=PROFESSOR") {
        return Promise.resolve({ data: mockProfessors });
      }
      if (url === "/admin/users?role=STUDENT") {
        return Promise.resolve({ data: [] });
      }
      if (url === "/admin/assignments") {
        return Promise.resolve({ data: [] });
      }
      return Promise.reject(new Error("Unknown endpoint"));
    });

    render(<StudentAssignmentForm />);

    await waitFor(() => {
      expect(
        screen.getByText("Student-Professor Assignments")
      ).toBeInTheDocument();
    });

    fireEvent.click(screen.getByRole("button", { name: /New Assignment/i }));

    await waitFor(() => {
      expect(screen.getByText("No students available")).toBeInTheDocument();
    });
  });
});
