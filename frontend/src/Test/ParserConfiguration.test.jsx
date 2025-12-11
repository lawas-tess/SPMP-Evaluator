import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

// Mock the api service (default export)
vi.mock("../services/apiService", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

import api from "../services/apiService";
import ParserConfiguration from "../components/dashboard/ParserConfiguration";

afterEach(() => {
  vi.clearAllMocks();
});

test("shows empty state and Create Default button when no configs", async () => {
  api.get.mockResolvedValueOnce({ data: [] });

  render(<ParserConfiguration />);

  await waitFor(() => {
    expect(
      screen.getByText(/No parser configurations found/i)
    ).toBeInTheDocument();
  });

  const createBtn = screen.getByRole("button", {
    name: /Create IEEE 1058 Default/i,
  });
  expect(createBtn).toBeInTheDocument();
});

test("create default posts and reloads list", async () => {
  // initial load: no configs
  api.get.mockResolvedValueOnce({ data: [] });
  // after create-default, loadConfigurations called again — return one config
  const mockConfig = {
    id: 1,
    name: "Default IEEE",
    description: "",
    isActive: true,
    isDefault: true,
    createdAt: new Date().toISOString(),
  };
  api.get.mockResolvedValueOnce({ data: [mockConfig] });
  api.post.mockResolvedValueOnce({});

  render(<ParserConfiguration />);

  // initial empty message
  await waitFor(() =>
    expect(
      screen.getByText(/No parser configurations found/i)
    ).toBeInTheDocument()
  );

  const createBtn = screen.getByRole("button", {
    name: /Create IEEE 1058 Default/i,
  });
  fireEvent.click(createBtn);

  await waitFor(() => {
    expect(api.post).toHaveBeenCalledWith("/parser/config/create-default");
    expect(screen.getByText(/Default IEEE/i)).toBeInTheDocument();
  });
});

test("create new configuration via form calls post", async () => {
  api.get.mockResolvedValueOnce({ data: [] });
  api.post.mockResolvedValueOnce({});
  api.get.mockResolvedValueOnce({ data: [] });

  const { container } = render(<ParserConfiguration />);

  await waitFor(() =>
    expect(
      screen.getByText(/No parser configurations found/i)
    ).toBeInTheDocument()
  );

  const newBtn = screen.getByRole("button", { name: /\+ New Configuration/i });
  fireEvent.click(newBtn);

  // fill form
  const nameInput = container.querySelector('input[name="name"]');
  fireEvent.change(nameInput, { target: { value: "My Config" } });

  const saveBtn = screen.getByRole("button", { name: /Save Configuration/i });
  fireEvent.click(saveBtn);

  await waitFor(() => {
    expect(api.post).toHaveBeenCalledWith(
      "/parser/config",
      expect.objectContaining({ name: "My Config" })
    );
  });
});

test("edit configuration populates form and calls put", async () => {
  const mockConfig = {
    id: 5,
    name: "Custom",
    description: "desc",
    clauseMappings: "[]",
    customRules: "[]",
    isActive: true,
    isDefault: false,
    createdAt: new Date().toISOString(),
  };
  api.get.mockResolvedValueOnce({ data: [mockConfig] });
  api.put.mockResolvedValueOnce({});
  api.get.mockResolvedValueOnce({ data: [mockConfig] });

  const { container } = render(<ParserConfiguration />);

  // wait for row
  await waitFor(() => expect(screen.getByText(/Custom/i)).toBeInTheDocument());

  const editBtn = screen.getByRole("button", { name: /Edit/i });
  fireEvent.click(editBtn);

  // name input should be populated
  const nameInput = container.querySelector('input[name="name"]');
  expect(nameInput.value).toBe("Custom");

  fireEvent.change(nameInput, { target: { value: "Custom Updated" } });

  const saveBtn = screen.getByRole("button", { name: /Save Configuration/i });
  fireEvent.click(saveBtn);

  await waitFor(() => {
    expect(api.put).toHaveBeenCalledWith(
      `/parser/config/5`,
      expect.objectContaining({ name: "Custom Updated" })
    );
  });
});

test("set default and delete call respective APIs", async () => {
  const mockConfig = {
    id: 7,
    name: "ToManage",
    description: "",
    isActive: true,
    isDefault: false,
    createdAt: new Date().toISOString(),
  };
  api.get.mockResolvedValue({ data: [mockConfig] });
  api.put.mockResolvedValue({});
  api.delete.mockResolvedValue({});

  // ensure confirm returns true
  window.confirm = vi.fn(() => true);

  render(<ParserConfiguration />);

  await waitFor(() =>
    expect(screen.getByText(/ToManage/i)).toBeInTheDocument()
  );

  const setDefaultBtn = screen.getByRole("button", { name: /Set Default/i });
  fireEvent.click(setDefaultBtn);

  await waitFor(() =>
    expect(api.put).toHaveBeenCalledWith("/parser/config/7/set-default")
  );

  const deleteBtn = screen.getByRole("button", { name: /Delete/i });
  fireEvent.click(deleteBtn);

  await waitFor(() =>
    expect(api.delete).toHaveBeenCalledWith("/parser/config/7")
  );
});
