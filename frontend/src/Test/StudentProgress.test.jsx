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

const sampleProgress = {
  studentName: "Test Student",
  studentEmail: "test@student.edu",
  totalDocuments: 3,
  evaluatedDocuments: 2,
  averageScore: 78.5,
  totalTasks: 4,
  completedTasks: 2,
  recentDocuments: [
    { fileName: "a.pdf", evaluated: true, score: 80 },
    { fileName: "b.docx", evaluated: false },
  ],
  pendingTasks: [
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

test("displays progress overview and lists recent documents and pending tasks", async () => {
  reportAPI.getStudentProgress.mockResolvedValue({ data: sampleProgress });

  render(<StudentProgress userId={42} studentName="Fallback Name" />);

  // wait for progress to render
  await waitFor(() =>
    expect(screen.getByText(/Progress Overview/i)).toBeInTheDocument()
  );

  // overview numbers
  expect(screen.getByText("3")).toBeInTheDocument(); // total documents
  expect(screen.getByText("2")).toBeInTheDocument(); // evaluated
  expect(screen.getByText(/78.5%/i)).toBeInTheDocument(); // avg score

  // recent documents
  expect(screen.getByText(/a.pdf/i)).toBeInTheDocument();
  expect(screen.getByText(/b.docx/i)).toBeInTheDocument();

  // pending task title
  expect(screen.getByText(/Complete survey/i)).toBeInTheDocument();
  expect(screen.getByText(/Due:/i)).toBeInTheDocument();
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
    data: { totalDocuments: 0, totalTasks: 0 },
  });
  render(<StudentProgress userId={5} />);
  await waitFor(() =>
    expect(screen.getByText(/No activity data available/i)).toBeInTheDocument()
  );
});
