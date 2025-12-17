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
  taskAPI: {
    getMyTasks: vi.fn(),
    complete: vi.fn(),
    updateStatus: vi.fn(),
  },
}));

import { taskAPI } from "../services/apiService";
import TaskTracker from "../components/dashboard/TaskTracker";

afterEach(() => {
  vi.clearAllMocks();
});

const makeTask = (
  id,
  title,
  completed = false,
  dueDate = null,
  status = "PENDING",
  priority = "MEDIUM"
) => ({
  id,
  title,
  description: `desc-${id}`,
  completed,
  priority,
  dueDate,
  status,
});

test("shows loading state initially", () => {
  taskAPI.getMyTasks.mockResolvedValue({ data: [] });
  render(<TaskTracker />);
  expect(screen.getByText(/Loading tasks.../i)).toBeInTheDocument();
});

test("shows error and retry button", async () => {
  taskAPI.getMyTasks
    .mockRejectedValueOnce({ response: { data: { message: "Fetch failed" } } })
    .mockResolvedValueOnce({ data: [] });

  render(<TaskTracker />);

  await waitFor(() =>
    expect(screen.getByText(/Fetch failed/i)).toBeInTheDocument()
  );

  const retry = screen.getByRole("button", { name: /Retry|Refresh/i });
  fireEvent.click(retry);

  await waitFor(() => expect(taskAPI.getMyTasks).toHaveBeenCalledTimes(2));
});

test("empty state when no tasks", async () => {
  taskAPI.getMyTasks.mockResolvedValue({ data: [] });
  render(<TaskTracker />);
  await waitFor(() =>
    expect(screen.getByText(/No tasks found/i)).toBeInTheDocument()
  );
});

test("displays tasks, stats, filters, and toggles completion", async () => {
  const pastDate = "2020-01-01T00:00:00.000Z";
  const futureDate = new Date(
    Date.now() + 5 * 24 * 60 * 60 * 1000
  ).toISOString();

  const tasks = [
    makeTask(1, "Task One", true, pastDate, "COMPLETED", "LOW"),
    makeTask(2, "Task Two", false, futureDate, "IN_PROGRESS", "HIGH"),
  ];

  const updatedTasks = [
    makeTask(1, "Task One", true, pastDate, "COMPLETED", "LOW"),
    makeTask(2, "Task Two", true, futureDate, "COMPLETED", "HIGH"),
  ];

  // initial load -> tasks; after toggle -> updatedTasks
  taskAPI.getMyTasks
    .mockResolvedValueOnce({ data: tasks })
    .mockResolvedValueOnce({ data: updatedTasks });

  taskAPI.updateStatus.mockResolvedValue({});

  render(<TaskTracker />);

  // wait for tasks to render
  await waitFor(() =>
    expect(screen.getByText(/Task One/i)).toBeInTheDocument()
  );

  // stats cards
  const totalLabel = screen
    .getAllByText(/Total Tasks/i)
    .find((el) => el.tagName.toLowerCase() === "p");
  const totalCard = totalLabel.closest("div");
  expect(within(totalCard).getByText("2")).toBeInTheDocument();

  const completedLabel = screen
    .getAllByText(/Completed/i)
    .find((el) => el.tagName.toLowerCase() === "p");
  const completedCard = completedLabel.closest("div");
  expect(within(completedCard).getByText("1")).toBeInTheDocument();

  const pendingLabel = screen
    .getAllByText(/Pending/i)
    .find((el) => el.tagName.toLowerCase() === "p");
  const pendingCard = pendingLabel.closest("div");
  expect(within(pendingCard).getByText("1")).toBeInTheDocument();

  const overdueLabel = screen
    .getAllByText(/Overdue/i)
    .find((el) => el.tagName.toLowerCase() === "p");
  const overdueCard = overdueLabel.closest("div");
  // Completed tasks are not considered overdue, so expect 0
  expect(within(overdueCard).getByText("0")).toBeInTheDocument();

  // filter to pending -> should show only Task Two
  // pick the filter-style button (the one showing the count in parentheses)
  const pendingFilter = screen
    .getAllByRole("button", { name: /Pending/i })
    .find((btn) => /Pending\s*\(/i.test(btn.textContent));
  fireEvent.click(pendingFilter);
  await waitFor(() =>
    expect(screen.getByText(/Task Two/i)).toBeInTheDocument()
  );
  expect(screen.queryByText(/Task One/i)).not.toBeInTheDocument();

  // Toggle completion on Task Two while it's visible in pending filter
  const titleEl = screen.getByText(/Task Two/i);
  let taskTwoCard = titleEl.closest("div");
  while (taskTwoCard && !taskTwoCard.className.includes("p-4"))
    taskTwoCard = taskTwoCard.parentElement;
  expect(taskTwoCard).toBeTruthy();

  const toggleBtn = within(taskTwoCard).getByTitle(
    /Click to mark as completed/i
  );
  fireEvent.click(toggleBtn);

  await waitFor(() =>
    expect(taskAPI.updateStatus).toHaveBeenCalledWith(2, "COMPLETED", true)
  );
  await waitFor(() => expect(taskAPI.getMyTasks).toHaveBeenCalled());

  // filter to completed -> should show completed tasks including Task Two
  const completedFilter = screen
    .getAllByRole("button", { name: /Completed/i })
    .find((btn) => /Completed\s*\(/i.test(btn.textContent));
  fireEvent.click(completedFilter);
  await waitFor(() =>
    expect(screen.getByText(/Task Two/i)).toBeInTheDocument()
  );
  expect(screen.getByText(/Task One/i)).toBeInTheDocument();
});
