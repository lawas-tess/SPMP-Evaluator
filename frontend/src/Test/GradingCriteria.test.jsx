import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

// Mock the apiService used by the component
vi.mock("../services/apiService", () => ({
  reportAPI: {
    getGradingCriteria: vi.fn(),
    saveGradingCriteria: vi.fn(),
  },
}));

import { reportAPI } from "../services/apiService";
import GradingCriteria from "../components/dashboard/GradingCriteria";

afterEach(() => {
  vi.clearAllMocks();
});

test("renders default criteria and shows total 100%", async () => {
  // return empty so component uses defaults
  reportAPI.getGradingCriteria.mockResolvedValue({ data: null });

  render(<GradingCriteria refreshTrigger={0} />);

  await waitFor(() => {
    expect(screen.getByText(/Grading Criteria/i)).toBeInTheDocument();
  });

  // Total weight should show 100%
  expect(screen.getByText(/100%/)).toBeInTheDocument();
  // Save button should be enabled (total is valid)
  const saveBtn = screen.getByRole("button", {
    name: /Save Grading Criteria/i,
  });
  expect(saveBtn).toBeEnabled();
});

test("editing a weight updates total and disables save when invalid", async () => {
  reportAPI.getGradingCriteria.mockResolvedValue({ data: null });

  const { container } = render(<GradingCriteria refreshTrigger={0} />);

  await waitFor(() => expect(screen.getByText(/100%/)).toBeInTheDocument());

  // find first numeric input (Scope = 8)
  const spinbuttons = screen.getAllByRole("spinbutton");
  expect(spinbuttons.length).toBeGreaterThan(0);

  // change first weight from 8 to 10 (total becomes 102)
  fireEvent.change(spinbuttons[0], { target: { value: "10" } });

  await waitFor(() => {
    expect(screen.getByText(/102%/)).toBeInTheDocument();
  });

  // Save should be disabled and error message visible
  const saveBtn = screen.getByRole("button", {
    name: /Save Grading Criteria/i,
  });
  expect(saveBtn).toBeDisabled();
  expect(screen.getByText(/Total must equal 100%/i)).toBeInTheDocument();
});

test("add custom criterion and reset to defaults", async () => {
  reportAPI.getGradingCriteria.mockResolvedValue({ data: null });

  render(<GradingCriteria refreshTrigger={0} />);

  await waitFor(() => expect(screen.getByText(/100%/)).toBeInTheDocument());

  const addBtn = screen.getByRole("button", { name: /Add Criterion/i });
  fireEvent.click(addBtn);

  // a custom input for name should appear
  await waitFor(() => {
    expect(screen.getByPlaceholderText(/Criterion name/i)).toBeInTheDocument();
  });

  // Reset to defaults
  const resetBtn = screen.getByRole("button", { name: /Reset/i });
  fireEvent.click(resetBtn);

  await waitFor(() => {
    expect(
      screen.getByText(/Reset to default IEEE 1058 weights./i)
    ).toBeInTheDocument();
  });

  // total back to 100
  expect(screen.getByText(/100%/)).toBeInTheDocument();
});

test("save criteria calls API and shows success, and handles API error", async () => {
  reportAPI.getGradingCriteria.mockResolvedValue({ data: null });

  // success path
  reportAPI.saveGradingCriteria.mockResolvedValue({});
  render(<GradingCriteria refreshTrigger={0} />);

  await waitFor(() => expect(screen.getByText(/100%/)).toBeInTheDocument());

  const saveBtn = screen.getByRole("button", {
    name: /Save Grading Criteria/i,
  });
  fireEvent.click(saveBtn);

  await waitFor(() => {
    expect(reportAPI.saveGradingCriteria).toHaveBeenCalled();
    expect(
      screen.getByText(/Grading criteria saved successfully/i)
    ).toBeInTheDocument();
  });

  // now test error path
  reportAPI.saveGradingCriteria.mockRejectedValue({
    response: { data: { message: "Save failed" } },
  });
  const saveBtn2 = screen.getByRole("button", {
    name: /Save Grading Criteria/i,
  });
  fireEvent.click(saveBtn2);

  await waitFor(() =>
    expect(screen.getByText(/Save failed/i)).toBeInTheDocument()
  );
});
