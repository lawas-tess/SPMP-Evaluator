import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

// Mock the notificationAPI used by the component. Keep mock data inside
// the factory to avoid issues with hoisting when using `vi.mock`.
vi.mock("../services/apiService", () => {
  const mockUnread = [
    {
      id: 1,
      title: "Score overridden",
      message: "Your score was changed",
      type: "SCORE_OVERRIDE",
      createdAt: new Date().toISOString(),
    },
  ];

  const mockAll = [
    {
      id: 2,
      title: "Task updated",
      message: "Task updated by instructor",
      type: "TASK_UPDATED",
      createdAt: new Date().toISOString(),
      read: false,
    },
  ];

  return {
    notificationAPI: {
      getUnreadNotifications: vi.fn().mockResolvedValue({ data: mockUnread }),
      getUnreadCount: vi
        .fn()
        .mockResolvedValue({ data: { unreadCount: mockUnread.length } }),
      getMyNotifications: vi.fn().mockResolvedValue({ data: mockAll }),
      markAsRead: vi.fn().mockResolvedValue({}),
      markAllAsRead: vi.fn().mockResolvedValue({}),
    },
  };
});

import NotificationBell from "../components/NotificationBell";
import { notificationAPI } from "../services/apiService";

afterEach(() => {
  vi.clearAllMocks();
});

test("shows unread badge and opens dropdown with notifications", async () => {
  render(<NotificationBell />);

  // badge should show count (1)
  await waitFor(() => expect(screen.getByText("1")).toBeInTheDocument());

  // open dropdown
  const btn = screen.getByTitle("Notifications");
  fireEvent.click(btn);

  // notification title should be visible
  await waitFor(() =>
    expect(screen.getByText(/Score overridden/i)).toBeInTheDocument()
  );
});

test("clicking a notification marks it as read and updates state", async () => {
  render(<NotificationBell />);

  // open dropdown
  fireEvent.click(screen.getByTitle("Notifications"));

  // ensure notification is rendered
  await waitFor(() =>
    expect(screen.getByText(/Score overridden/i)).toBeInTheDocument()
  );

  // click the notification item (it has the title text)
  fireEvent.click(screen.getByText(/Score overridden/i));

  // markAsRead should be called with id
  await waitFor(() =>
    expect(notificationAPI.markAsRead).toHaveBeenCalledWith(1)
  );

  // unread badge should be removed (component updates state)
  await waitFor(() => expect(screen.queryByText("1")).not.toBeInTheDocument());
});

test("view history loads all notifications and shows history header", async () => {
  render(<NotificationBell />);

  // open dropdown
  fireEvent.click(screen.getByTitle("Notifications"));

  // click view history footer button
  const viewBtn = await waitFor(() =>
    screen.getByRole("button", { name: /View History/i })
  );
  fireEvent.click(viewBtn);

  // getMyNotifications should be called
  await waitFor(() =>
    expect(notificationAPI.getMyNotifications).toHaveBeenCalled()
  );

  // history header should be visible
  await waitFor(() =>
    expect(screen.getByText(/Notification History/i)).toBeInTheDocument()
  );

  // history item should render (match the message to avoid ambiguous matches)
  expect(screen.getByText(/Task updated by instructor/i)).toBeInTheDocument();
});

test("mark all as read calls API and clears notifications", async () => {
  render(<NotificationBell />);

  // open dropdown
  fireEvent.click(screen.getByTitle("Notifications"));

  // ensure the "Mark all read" button appears in header when unread exist
  const markAllBtn = await waitFor(() =>
    screen.getByRole("button", { name: /Mark all read/i })
  );
  fireEvent.click(markAllBtn);

  await waitFor(() => expect(notificationAPI.markAllAsRead).toHaveBeenCalled());

  // badge should be cleared
  await waitFor(() => expect(screen.queryByText("1")).not.toBeInTheDocument());
});
