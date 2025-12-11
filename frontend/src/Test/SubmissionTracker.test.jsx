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
  documentAPI: { getAllSubmissions: vi.fn() },
}));

import { documentAPI } from "../services/apiService";
import SubmissionTracker from "../components/dashboard/SubmissionTracker";

afterEach(() => {
  vi.clearAllMocks();
});

const makeDoc = (
  id,
  fileName,
  firstName,
  lastName,
  email,
  evaluated = false,
  score = null,
  uploadedAt = new Date().toISOString()
) => ({
  id,
  fileName,
  uploadedBy: {
    firstName,
    lastName,
    email,
    username: `${firstName}.${lastName}`,
  },
  uploadedAt,
  evaluated,
  complianceScore: evaluated ? { overallScore: score } : null,
});

test("shows loading state initially", () => {
  documentAPI.getAllSubmissions.mockResolvedValue({ data: [] });
  render(<SubmissionTracker />);
  expect(screen.getByText(/Loading submissions.../i)).toBeInTheDocument();
});

test("shows error and retry button", async () => {
  documentAPI.getAllSubmissions
    .mockRejectedValueOnce({ response: { data: { message: "Fetch failed" } } })
    .mockResolvedValueOnce({ data: [] });

  render(<SubmissionTracker />);

  await waitFor(() =>
    expect(screen.getByText(/Fetch failed/i)).toBeInTheDocument()
  );

  const retry = screen.getByRole("button", { name: /Retry/i });
  fireEvent.click(retry);
  await waitFor(() =>
    expect(documentAPI.getAllSubmissions).toHaveBeenCalledTimes(2)
  );
});

test("displays submissions, stats, and status badges", async () => {
  const docs = [
    makeDoc(1, "report1.pdf", "Alice", "A", "alice@example.com", true, 92),
    makeDoc(2, "report2.pdf", "Bob", "B", "bob@example.com", false, null),
  ];

  documentAPI.getAllSubmissions.mockResolvedValue({ data: docs });

  render(<SubmissionTracker />);

  // wait for rows
  await waitFor(() =>
    expect(screen.getByText(/report1.pdf/i)).toBeInTheDocument()
  );

  // stats - scope to cards
  const totalLabel = screen.getByText(/Total Submissions/i);
  const totalCard = totalLabel.closest("div");
  expect(within(totalCard).getByText("2")).toBeInTheDocument();

  const evalLabels = screen.getAllByText(/Evaluated/i);
  const evalLabel = evalLabels.find((el) => el.tagName.toLowerCase() === "p");
  const evalCard = evalLabel.closest("div");
  expect(within(evalCard).getByText("1")).toBeInTheDocument();

  // pending
  const pendingLabels = screen.getAllByText(/Pending/i);
  const pendingLabel = pendingLabels.find(
    (el) => el.tagName.toLowerCase() === "p"
  );
  const pendingCard = pendingLabel.closest("div");
  expect(within(pendingCard).getByText("1")).toBeInTheDocument();

  // status badges scoped to rows
  const row1 = screen.getByText(/report1.pdf/i).closest("tr");
  expect(within(row1).getByText(/Compliant/i)).toBeInTheDocument();

  const row2 = screen.getByText(/report2.pdf/i).closest("tr");
  expect(within(row2).getByText(/Pending/i)).toBeInTheDocument();
});

test("search filters rows and status filter updates API call", async () => {
  const docs = [
    makeDoc(1, "alpha.pdf", "Charlie", "C", "c@example.com", true, 60),
    makeDoc(2, "beta.pdf", "Delta", "D", "d@example.com", true, 85),
  ];

  documentAPI.getAllSubmissions.mockResolvedValue({ data: docs });
  render(<SubmissionTracker />);
  await waitFor(() =>
    expect(screen.getByText(/alpha.pdf/i)).toBeInTheDocument()
  );

  const search = screen.getByPlaceholderText(/Search by student name or file/i);
  fireEvent.change(search, { target: { value: "delta" } });
  expect(screen.queryByText(/alpha.pdf/i)).not.toBeInTheDocument();
  expect(screen.getByText(/beta.pdf/i)).toBeInTheDocument();

  // change status filter to EVALUATED -> triggers a fetch
  const select = screen.getByRole("combobox");
  fireEvent.change(select, { target: { value: "EVALUATED" } });
  await waitFor(() => expect(documentAPI.getAllSubmissions).toHaveBeenCalled());
});

test("action buttons call callbacks for evaluated docs", async () => {
  const docs = [makeDoc(1, "x.pdf", "Eva", "E", "e@example.com", true, 75)];
  documentAPI.getAllSubmissions.mockResolvedValue({ data: docs });

  const onView = vi.fn();
  const onOverride = vi.fn();
  render(
    <SubmissionTracker onViewReport={onView} onOverrideScore={onOverride} />
  );

  await waitFor(() => expect(screen.getByText(/x.pdf/i)).toBeInTheDocument());

  const viewBtn = screen.getByRole("button", { name: /View/i });
  fireEvent.click(viewBtn);
  expect(onView).toHaveBeenCalledWith(expect.objectContaining({ id: 1 }));

  const overrideBtn = screen.getByRole("button", { name: /Override/i });
  fireEvent.click(overrideBtn);
  expect(onOverride).toHaveBeenCalledWith(expect.objectContaining({ id: 1 }));
});

test("empty state when no submissions", async () => {
  documentAPI.getAllSubmissions.mockResolvedValue({ data: [] });
  render(<SubmissionTracker />);
  await waitFor(() =>
    expect(screen.getByText(/No submissions found/i)).toBeInTheDocument()
  );
});
