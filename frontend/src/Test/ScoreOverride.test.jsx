import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

// Mock the apiService
vi.mock("../services/apiService", () => ({
  documentAPI: {
    overrideScore: vi.fn(),
  },
}));

import { documentAPI } from "../services/apiService";
import ScoreOverride from "../components/dashboard/ScoreOverride";

afterEach(() => {
  vi.clearAllMocks();
});

const mockDocument = {
  id: 1,
  fileName: "sample.pdf",
  complianceScore: { overallScore: 70, notes: "Initial" },
};

test("renders modal with current score and populated input", () => {
  render(
    <ScoreOverride
      document={mockDocument}
      onClose={vi.fn()}
      onSuccess={vi.fn()}
    />
  );

  expect(screen.getByText(/Override Compliance Score/i)).toBeInTheDocument();
  expect(screen.getByText(/sample.pdf/i)).toBeInTheDocument();
  // current score displayed
  const current = screen.getByText(/Current Score:/i);
  expect(current).toBeInTheDocument();

  // input has initial value
  const input = screen.getByRole("spinbutton");
  expect(input.value).toBe("70");
});

test("shows validation error for out-of-range score and does not call API", async () => {
  render(
    <ScoreOverride
      document={mockDocument}
      onClose={vi.fn()}
      onSuccess={vi.fn()}
    />
  );

  const input = screen.getByRole("spinbutton");
  fireEvent.change(input, { target: { value: "150" } });

  // wait for controlled input to reflect the change before submitting
  await waitFor(() => expect(input.value).toBe("150"));

  const saveBtn = screen.getByRole("button", { name: /Save Override/i });
  fireEvent.click(saveBtn);
  // validation should prevent the API call when the score is out of range
  await waitFor(() => expect(documentAPI.overrideScore).not.toHaveBeenCalled());
});

test("submits valid override, calls API, and triggers callbacks", async () => {
  documentAPI.overrideScore.mockResolvedValue({});

  const onClose = vi.fn();
  const onSuccess = vi.fn();

  render(
    <ScoreOverride
      document={mockDocument}
      onClose={onClose}
      onSuccess={onSuccess}
    />
  );

  const input = screen.getByRole("spinbutton");
  // set a valid score and justification notes
  fireEvent.change(input, { target: { value: "85" } });
  await waitFor(() => expect(input.value).toBe("85"));

  const notes = screen.getByPlaceholderText(
    /Explain why you're overriding the AI-generated score.../i
  );
  fireEvent.change(notes, { target: { value: "Manual adjustment" } });

  const saveBtn = screen.getByRole("button", { name: /Save Override/i });
  fireEvent.click(saveBtn);

  await waitFor(() => {
    expect(documentAPI.overrideScore).toHaveBeenCalledWith(
      1,
      85,
      "Manual adjustment"
    );
    expect(onSuccess).toHaveBeenCalled();
    expect(onClose).toHaveBeenCalled();
  });
});

test("shows API error message when override fails", async () => {
  documentAPI.overrideScore.mockRejectedValue({
    response: { data: { message: "Override failed" } },
  });

  render(
    <ScoreOverride
      document={mockDocument}
      onClose={vi.fn()}
      onSuccess={vi.fn()}
    />
  );

  const input = screen.getByRole("spinbutton");
  fireEvent.change(input, { target: { value: "50" } });
  await waitFor(() => expect(input.value).toBe("50"));

  const saveBtn = screen.getByRole("button", { name: /Save Override/i });
  fireEvent.click(saveBtn);

  await waitFor(() =>
    expect(screen.getByText(/Override failed/i)).toBeInTheDocument()
  );
});
