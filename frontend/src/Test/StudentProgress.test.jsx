import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

vi.mock("../services/apiService", () => ({
  reportAPI: { getStudentProgress: vi.fn() },
}));

import { reportAPI } from "../services/apiService";
import StudentProgress from "../components/dashboard/StudentProgress";

afterEach(() => {
  vi.clearAllMocks();
});

// Backend response format - component maps this to internal format
const sampleProgress = {
  studentName: "Test Student",
  studentEmail: "test@student.edu",
  documents: {
    totalUploads: 3,
    evaluated: 2,
    averageScore: 78.5,
  },
  tasks: {
    totalTasks: 4,
    completed: 2,
  },
  recentDocuments: [
    { fileName: "a.pdf", evaluated: true, score: 80 },
    { fileName: "b.docx", evaluated: false },
  ],
  recentTasks: [
    { title: "Complete survey", dueDate: new Date().toISOString() },
  ],
};

test("shows loading state initially", () => {
  reportAPI.getStudentProgress.mockResolvedValue({ data: {} });
  render(<StudentProgress userId={1} />);
  expect(screen.getByText(/Loading student progress.../i)).toBeInTheDocument();
});

test("shows error state and retry/back buttons", async () => {
  reportAPI.getStudentProgress
    .mockRejectedValueOnce({ response: { data: { message: "Server down" } } })
    .mockResolvedValueOnce({ data: {} });

  const onClose = vi.fn();
  render(<StudentProgress userId={1} onClose={onClose} />);

  await waitFor(() =>
    expect(screen.getByText(/Server down/i)).toBeInTheDocument()
  );

  const backBtn = screen.getByRole("button", { name: /Back/i });
  fireEvent.click(backBtn);
  expect(onClose).toHaveBeenCalled();

  const retryBtn = screen.getByRole("button", { name: /Retry/i });
  fireEvent.click(retryBtn);
  await waitFor(() =>
    expect(reportAPI.getStudentProgress).toHaveBeenCalledTimes(2)
  );
});

test("displays progress overview and lists recent documents", async () => {
  reportAPI.getStudentProgress.mockResolvedValue({ data: sampleProgress });

  render(<StudentProgress userId={42} studentName="Fallback Name" />);

  // wait for progress to render
  await waitFor(() =>
    expect(screen.getByText(/Progress Overview/i)).toBeInTheDocument()
  );

  // overview numbers - use getAllByText since "3" and "2" may appear multiple times
  const threeElements = screen.getAllByText("3");
  expect(threeElements.length).toBeGreaterThan(0); // total documents
  const twoElements = screen.getAllByText("2");
  expect(twoElements.length).toBeGreaterThan(0); // evaluated
  expect(screen.getByText(/78.5%/i)).toBeInTheDocument(); // avg score

  // recent documents
  expect(screen.getByText(/a.pdf/i)).toBeInTheDocument();
  expect(screen.getByText(/b.docx/i)).toBeInTheDocument();
});

test("refresh button calls API again", async () => {
  reportAPI.getStudentProgress.mockResolvedValue({ data: sampleProgress });

  render(<StudentProgress userId={99} />);
  await waitFor(() =>
    expect(screen.getByText(/Progress Overview/i)).toBeInTheDocument()
  );

  const refresh = screen.getByRole("button", { name: /Refresh/i });
  fireEvent.click(refresh);
  await waitFor(() =>
    expect(reportAPI.getStudentProgress).toHaveBeenCalledTimes(2)
  );
});

test("shows empty state when no documents and no tasks", async () => {
  reportAPI.getStudentProgress.mockResolvedValue({
    data: {
      documents: { totalUploads: 0, evaluated: 0, averageScore: 0 },
      tasks: { totalTasks: 0, completed: 0 },
    },
  });
  render(<StudentProgress userId={5} />);
  await waitFor(() =>
    expect(screen.getByText(/No activity data available/i)).toBeInTheDocument()
  );
});
