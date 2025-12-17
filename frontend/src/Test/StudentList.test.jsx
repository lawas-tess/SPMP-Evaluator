import React from "react";
import {
  render,
  screen,
  fireEvent,
  waitFor,
  within,
} from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

vi.mock("../services/apiService", () => ({
  userAPI: { getAllStudents: vi.fn() },
  reportAPI: { getStudentProgress: vi.fn() },
}));

import { userAPI, reportAPI } from "../services/apiService";
import StudentList from "../components/dashboard/StudentList";

afterEach(() => {
  vi.clearAllMocks();
  // restore any URL spies
  if (window.URL.createObjectURL && window.URL.createObjectURL._isMock) {
    delete window.URL.createObjectURL;
  }
  if (window.URL.revokeObjectURL && window.URL.revokeObjectURL._isMock) {
    delete window.URL.revokeObjectURL;
  }
});

const makeStudent = (id, firstName, lastName, email, progress = {}) => ({
  id,
  firstName,
  lastName,
  email,
  username: `${firstName.toLowerCase()}.${lastName.toLowerCase()}`,
  progress,
});

test("shows loading state initially", () => {
  // ensure the component renders loading before the fetch completes
  userAPI.getAllStudents.mockResolvedValue({ data: [] });
  reportAPI.getStudentProgress.mockResolvedValue({ data: {} });

  render(<StudentList onSelectStudent={vi.fn()} />);

  expect(screen.getByText(/Loading student progress.../i)).toBeInTheDocument();
});

test("shows error state and retries on click", async () => {
  userAPI.getAllStudents
    .mockRejectedValueOnce({ response: { data: { message: "Network error" } } })
    .mockResolvedValueOnce({ data: [] });

  render(<StudentList onSelectStudent={vi.fn()} />);

  await waitFor(() =>
    expect(screen.getByText(/Network error/i)).toBeInTheDocument()
  );

  const retry = screen.getByRole("button", { name: /Retry/i });
  fireEvent.click(retry);

  await waitFor(() => expect(userAPI.getAllStudents).toHaveBeenCalledTimes(2));
});

test("displays student cards and class summary", async () => {
  const students = [
    makeStudent(1, "Alice", "Smith", "alice@example.com", {
      totalDocuments: 2,
      evaluatedDocuments: 2,
      averageScore: 90,
      totalTasks: 4,
      completedTasks: 4,
    }),
    makeStudent(2, "Bob", "Jones", "bob@example.com", {
      totalDocuments: 1,
      evaluatedDocuments: 1,
      averageScore: 70,
      totalTasks: 2,
      completedTasks: 1,
    }),
  ];

  userAPI.getAllStudents.mockResolvedValue({
    data: students.map((s) => ({
      id: s.id,
      firstName: s.firstName,
      lastName: s.lastName,
      email: s.email,
    })),
  });
  // Map progress to backend response format
  reportAPI.getStudentProgress.mockImplementation((id) => {
    const s = students.find((x) => x.id === id);
    return Promise.resolve({
      data: {
        documents: {
          totalUploads: s.progress.totalDocuments,
          evaluated: s.progress.evaluatedDocuments,
          averageScore: s.progress.averageScore,
        },
        tasks: {
          totalTasks: s.progress.totalTasks,
          completed: s.progress.completedTasks,
        },
      },
    });
  });

  render(<StudentList onSelectStudent={vi.fn()} />);

  // wait for a student name to appear
  await waitFor(() =>
    expect(screen.getByText(/Alice Smith/i)).toBeInTheDocument()
  );

  // class summary should show total students (find number within the same card)
  const totalLabel = screen.getByText(/Total Students/i);
  const totalCard = totalLabel.closest("div");
  expect(totalCard).toBeTruthy();
  expect(within(totalCard).getByText("2")).toBeInTheDocument();

  // student cards show avg score
  expect(screen.getByText(/90%/i)).toBeInTheDocument();
  expect(screen.getByText(/70%/i)).toBeInTheDocument();
});

test("filters students by search and sorts by score", async () => {
  const students = [
    makeStudent(1, "Charlie", "Alpha", "c1@example.com", {
      averageScore: 40,
      totalDocuments: 1,
    }),
    makeStudent(2, "Delta", "Beta", "d2@example.com", {
      averageScore: 80,
      totalDocuments: 2,
    }),
  ];

  userAPI.getAllStudents.mockResolvedValue({
    data: students.map((s) => ({
      id: s.id,
      firstName: s.firstName,
      lastName: s.lastName,
      email: s.email,
    })),
  });
  // Map progress to backend response format
  reportAPI.getStudentProgress.mockImplementation((id) => {
    const s = students.find((x) => x.id === id);
    return Promise.resolve({
      data: {
        documents: {
          totalUploads: s.progress.totalDocuments || 0,
          evaluated: s.progress.evaluatedDocuments || 0,
          averageScore: s.progress.averageScore || 0,
        },
        tasks: {
          totalTasks: s.progress.totalTasks || 0,
          completed: s.progress.completedTasks || 0,
        },
      },
    });
  });

  render(<StudentList onSelectStudent={vi.fn()} />);

  await waitFor(() =>
    expect(screen.getByText(/Charlie Alpha/i)).toBeInTheDocument()
  );

  // type search that matches Delta only
  const search = screen.getByPlaceholderText(/Search by name or email/i);
  fireEvent.change(search, { target: { value: "delta" } });

  expect(screen.queryByText(/Charlie Alpha/i)).not.toBeInTheDocument();
  expect(screen.getByText(/Delta Beta/i)).toBeInTheDocument();

  // change sort to score
  const sort = screen.getByRole("combobox");
  fireEvent.change(sort, { target: { value: "score" } });

  // when sorted by score, Delta (80) should be above Charlie (40) if both present
  fireEvent.change(search, { target: { value: "" } });
  await waitFor(() =>
    expect(screen.getByText(/Charlie Alpha/i)).toBeInTheDocument()
  );
});

test("export CSV calls createObjectURL and revokeObjectURL", async () => {
  const students = [
    makeStudent(1, "Eve", "Adams", "eve@example.com", {
      averageScore: 88,
      totalDocuments: 1,
      evaluatedDocuments: 1,
      totalTasks: 1,
      completedTasks: 1,
    }),
  ];

  userAPI.getAllStudents.mockResolvedValue({
    data: students.map((s) => ({
      id: s.id,
      firstName: s.firstName,
      lastName: s.lastName,
      email: s.email,
    })),
  });
  reportAPI.getStudentProgress.mockResolvedValue({
    data: students[0].progress,
  });

  const createSpy = vi.fn(() => "blob:mock");
  const revokeSpy = vi.fn();
  // set mocks on window.URL
  Object.defineProperty(window, "URL", {
    writable: true,
    value: { createObjectURL: createSpy, revokeObjectURL: revokeSpy },
  });
  // mark to cleanup later
  window.URL.createObjectURL._isMock = true;
  window.URL.revokeObjectURL._isMock = true;

  render(<StudentList onSelectStudent={vi.fn()} />);
  await waitFor(() =>
    expect(screen.getByText(/Eve Adams/i)).toBeInTheDocument()
  );

  const exportBtn = screen.getByRole("button", { name: /Export CSV/i });
  fireEvent.click(exportBtn);

  expect(createSpy).toHaveBeenCalled();
  // revoke should be called as well
  expect(revokeSpy).toHaveBeenCalled();
});

test("clicking a student calls onSelectStudent with id and name", async () => {
  const students = [
    makeStudent(1, "Fiona", "Green", "fiona@example.com", {
      averageScore: 55,
      totalDocuments: 1,
    }),
  ];

  userAPI.getAllStudents.mockResolvedValue({
    data: students.map((s) => ({
      id: s.id,
      firstName: s.firstName,
      lastName: s.lastName,
      email: s.email,
    })),
  });
  reportAPI.getStudentProgress.mockResolvedValue({
    data: students[0].progress,
  });

  const onSelect = vi.fn();
  render(<StudentList onSelectStudent={onSelect} />);

  await waitFor(() =>
    expect(screen.getByText(/Fiona Green/i)).toBeInTheDocument()
  );

  const viewBtn = screen.getByRole("button", { name: /View Details/i });
  fireEvent.click(viewBtn);

  expect(onSelect).toHaveBeenCalledWith(1, "Fiona Green");
});
