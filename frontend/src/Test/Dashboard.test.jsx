import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

afterEach(() => {
  vi.resetModules();
  vi.restoreAllMocks();
});

test("renders student overview and switches to upload tab", async () => {
  vi.resetModules();

  // Mock auth context for a student user
  vi.doMock("../context/AuthContext.jsx", () => ({
    useAuth: () => ({
      user: { username: "alice", role: "STUDENT" },
    }),
  }));

  // Stub Navbar
  vi.doMock("../components/Navbar.jsx", () => ({
    __esModule: true,
    default: () => <div data-testid="navbar">Navbar</div>,
  }));

  // Stub dashboard child components
  vi.doMock("../components/dashboard", () => ({
    __esModule: true,
    DocumentUpload: (props) => <div data-testid="document-upload">Upload</div>,
    DocumentList: (props) => <div data-testid="document-list">Docs</div>,
    EvaluationResults: (props) => (
      <div data-testid="evaluation-results">Results</div>
    ),
    TaskTracker: (props) => <div data-testid="task-tracker">Tasks</div>,
    SubmissionTracker: (props) => (
      <div data-testid="submission-tracker">Submissions</div>
    ),
    TaskManager: (props) => <div data-testid="task-manager">TaskMgr</div>,
    ScoreOverride: (props) => <div data-testid="score-override">SO</div>,
    StudentProgress: (props) => (
      <div data-testid="student-progress">Progress</div>
    ),
    GradingCriteria: (props) => <div data-testid="grading-criteria">GC</div>,
    StudentList: (props) => <div data-testid="student-list">Students</div>,
    FileReplaceModal: (props) => <div data-testid="file-replace">Replace</div>,
    ParserConfiguration: (props) => (
      <div data-testid="parser-config">Parser</div>
    ),
    ParserFeedback: (props) => <div data-testid="parser-feedback">FB</div>,
  }));

  const Dashboard = (await import("../pages/Dashboard.jsx")).default;

  render(<Dashboard />);

  // Overview should show welcome message with username
  expect(screen.getByText(/Welcome back, alice!/i)).toBeInTheDocument();

  // Student quick action buttons exist
  expect(screen.getAllByText(/Upload Document/i).length > 0).toBe(true);
  expect(screen.getAllByText(/View Documents/i).length > 0).toBe(true);
  expect(screen.getAllByText(/My Tasks/i).length > 0).toBe(true);

  // Click Upload Document tab and expect DocumentUpload to render
  fireEvent.click(screen.getAllByText(/Upload Document/i)[0]);
  expect(screen.getByTestId("document-upload")).toBeInTheDocument();
});

test("renders professor tabs and navigates to submissions", async () => {
  vi.resetModules();

  // Mock auth context for a professor user
  vi.doMock("../context/AuthContext.jsx", () => ({
    useAuth: () => ({
      user: { username: "drbob", role: "PROFESSOR" },
    }),
  }));

  // Stub Navbar
  vi.doMock("../components/Navbar.jsx", () => ({
    __esModule: true,
    default: () => <div data-testid="navbar">Navbar</div>,
  }));

  // Stub dashboard child components (only what's needed)
  vi.doMock("../components/dashboard", () => ({
    __esModule: true,
    SubmissionTracker: (props) => (
      <div data-testid="submission-tracker">Submissions</div>
    ),
    TaskManager: (props) => <div data-testid="task-manager">TaskMgr</div>,
    StudentProgress: (props) => (
      <div data-testid="student-progress">Progress</div>
    ),
    StudentList: (props) => <div data-testid="student-list">Students</div>,
    GradingCriteria: (props) => <div data-testid="grading-criteria">GC</div>,
    ParserConfiguration: (props) => (
      <div data-testid="parser-config">Parser</div>
    ),
  }));

  const Dashboard = (await import("../pages/Dashboard.jsx")).default;

  render(<Dashboard />);

  // Professor quick actions
  expect(screen.getAllByText(/View Submissions/i).length > 0).toBe(true);
  expect(screen.getAllByText(/Manage Tasks/i).length > 0).toBe(true);
  expect(screen.getAllByText(/Student Progress/i).length > 0).toBe(true);

  // Click View Submissions tab and expect SubmissionTracker to render
  fireEvent.click(screen.getAllByText(/View Submissions/i)[0]);
  expect(screen.getByTestId("submission-tracker")).toBeInTheDocument();
});
