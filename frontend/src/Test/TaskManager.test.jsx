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
    getCreatedTasks: vi.fn(),
    delete: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
  },
  userAPI: {
    getAllStudents: vi.fn(),
  },
}));

import { taskAPI, userAPI } from "../services/apiService";
import TaskManager from "../components/dashboard/TaskManager";

afterEach(() => {
  vi.clearAllMocks();
});

const makeTask = (id, title, completed = false, priority = "MEDIUM") => ({
  id,
  title,
  description: `desc-${id}`,
  completed,
  priority,
  assignedToFirstName: "Test",
  assignedToLastName: `User${id}`,
  assignedToUsername: `test.user${id}`,
  deadline: "2025-12-31T12:00:00.000Z",
});

test("shows loading state initially", () => {
  taskAPI.getCreatedTasks.mockResolvedValue({ data: [] });
  render(<TaskManager />);
  expect(screen.getByText(/Loading tasks.../i)).toBeInTheDocument();
});

test("empty state when no tasks", async () => {
  taskAPI.getCreatedTasks.mockResolvedValue({ data: [] });
  render(<TaskManager />);
  await waitFor(() =>
    expect(screen.getByText(/No tasks created yet/i)).toBeInTheDocument()
  );
});

test("displays tasks, stats, and action buttons work (delete/edit/create)", async () => {
  const tasks = [
    makeTask(1, "Task One", true, "LOW"),
    makeTask(2, "Task Two", false, "HIGH"),
  ];
  taskAPI.getCreatedTasks.mockResolvedValue({ data: tasks });
  taskAPI.delete.mockResolvedValue({});
  userAPI.getAllStudents.mockResolvedValue({ data: [] });

  render(<TaskManager />);

  // wait for tasks to render
  await waitFor(() =>
    expect(screen.getByText(/Task One/i)).toBeInTheDocument()
  );

  // stats
  const totalLabel = screen.getByText(/Total Tasks/i);
  const totalCard = totalLabel.closest("div");
  expect(within(totalCard).getByText("2")).toBeInTheDocument();

  const completedLabel = screen.getByText(/Completed/i);
  const completedCard = completedLabel.closest("div");
  expect(within(completedCard).getByText("1")).toBeInTheDocument();

  const pendingLabel = screen.getByText(/Pending/i);
  const pendingCard = pendingLabel.closest("div");
  expect(within(pendingCard).getByText("1")).toBeInTheDocument();

  // rows and badges: scope to each task card and check description + priority
  // find the card container by climbing ancestors until we hit the card wrapper
  const titleEl = screen.getByText(/Task One/i);
  let row1 = titleEl.closest("div");
  while (row1 && !row1.className.includes("p-4")) {
    row1 = row1.parentElement;
  }
  expect(row1).toBeTruthy();
  expect(within(row1).getByText(/desc-1/i)).toBeInTheDocument();
  expect(within(row1).getByText(/LOW/i)).toBeInTheDocument();

  // Edit: clicking edit should open modal (Edit Task)
  const taskTwoHeading = screen.getByText(/Task Two/i);
  let taskTwoCard = taskTwoHeading.closest("div");
  while (taskTwoCard && !taskTwoCard.className.includes("p-4")) {
    taskTwoCard = taskTwoCard.parentElement;
  }
  const taskTwoButtons = within(taskTwoCard).getAllByRole("button");
  const editBtn = taskTwoButtons[0];
  fireEvent.click(editBtn);
  await waitFor(() =>
    expect(screen.getByText(/Edit Task/i)).toBeInTheDocument()
  );

  // close modal by clicking Cancel
  fireEvent.click(screen.getByRole("button", { name: /Cancel/i }));

  // Create Task modal
  const createBtn = screen.getByRole("button", { name: /Create Task/i });
  fireEvent.click(createBtn);
  await waitFor(() =>
    expect(screen.getByText(/Create New Task/i)).toBeInTheDocument()
  );

  // Close create modal
  fireEvent.click(screen.getByRole("button", { name: /Cancel/i }));

  // Delete flow: mock confirm to true and click delete on row1
  vi.spyOn(window, "confirm").mockImplementation(() => true);
  taskAPI.getCreatedTasks
    .mockResolvedValueOnce({ data: tasks })
    .mockResolvedValueOnce({ data: [tasks[1]] });
  const delBtn = within(row1).getAllByRole("button")[1];
  fireEvent.click(delBtn);

  await waitFor(() => expect(taskAPI.delete).toHaveBeenCalledWith(1));
  // getCreatedTasks should be called at least once (initial load); after delete it's called again
  await waitFor(() => expect(taskAPI.getCreatedTasks).toHaveBeenCalled());
});
