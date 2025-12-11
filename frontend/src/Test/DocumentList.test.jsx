import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";
import DocumentList from "../components/dashboard/DocumentList";

// Mock the apiService
vi.mock("../services/apiService", () => ({
  documentAPI: {
    getMyDocuments: vi.fn(),
    evaluate: vi.fn(),
    reEvaluate: vi.fn(),
    delete: vi.fn(),
  },
}));

import { documentAPI } from "../services/apiService";

afterEach(() => {
  vi.clearAllMocks();
});

const mockDocuments = [
  {
    id: 1,
    fileName: "test.pdf",
    uploadedAt: "2023-10-01T10:00:00Z",
    evaluated: true,
    complianceScore: { overallScore: 85 },
  },
  {
    id: 2,
    fileName: "test2.docx",
    uploadedAt: "2023-10-02T10:00:00Z",
    evaluated: false,
  },
];

const renderComponent = (props = {}) => {
  return render(
    <DocumentList
      onViewReport={vi.fn()}
      onReplace={vi.fn()}
      refreshTrigger={0}
      {...props}
    />
  );
};

test("renders loading state initially", () => {
  documentAPI.getMyDocuments.mockResolvedValue({ data: [] });

  renderComponent();

  expect(screen.getByText("Loading documents...")).toBeInTheDocument();
});

test("renders error state when fetch fails", async () => {
  documentAPI.getMyDocuments.mockRejectedValue({
    response: { data: { message: "Failed to load" } },
  });

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("Failed to load")).toBeInTheDocument();
  });

  expect(screen.getByText("Retry")).toBeInTheDocument();
});

test("renders empty state when no documents", async () => {
  documentAPI.getMyDocuments.mockResolvedValue({ data: [] });

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("No documents uploaded yet")).toBeInTheDocument();
  });
});

test("renders documents list correctly", async () => {
  documentAPI.getMyDocuments.mockResolvedValue({ data: mockDocuments });

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("test.pdf")).toBeInTheDocument();
    expect(screen.getByText("test2.docx")).toBeInTheDocument();
  });

  expect(screen.getByText("Compliant (85%)")).toBeInTheDocument();
  expect(screen.getByText("Pending Evaluation")).toBeInTheDocument();
});

test("handles evaluate button click", async () => {
  documentAPI.getMyDocuments.mockResolvedValue({ data: mockDocuments });
  documentAPI.evaluate.mockResolvedValue({});

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("test2.docx")).toBeInTheDocument();
  });

  // Second evaluate button for test2.docx (id:2)
  const evaluateButton = screen.getAllByRole("button", {
    name: /Evaluate/i,
  })[1];
  fireEvent.click(evaluateButton);

  expect(documentAPI.evaluate).toHaveBeenCalledWith(2);

  await waitFor(() => {
    expect(screen.queryByText("Evaluating...")).not.toBeInTheDocument();
  });
});

test("handles re-evaluate button click", async () => {
  documentAPI.getMyDocuments.mockResolvedValue({ data: mockDocuments });
  documentAPI.reEvaluate.mockResolvedValue({});

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("test.pdf")).toBeInTheDocument();
  });

  const reEvaluateButton = screen.getByRole("button", { name: /Re-evaluate/i });
  fireEvent.click(reEvaluateButton);

  expect(documentAPI.reEvaluate).toHaveBeenCalledWith(1);

  await waitFor(() => {
    expect(screen.queryByText("Re-evaluating...")).not.toBeInTheDocument();
  });
});

test("handles delete button click with confirmation", async () => {
  documentAPI.getMyDocuments.mockResolvedValue({ data: mockDocuments });
  documentAPI.delete.mockResolvedValue({});
  window.confirm = vi.fn(() => true);

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("test.pdf")).toBeInTheDocument();
  });

  // First delete button for test.pdf (id:1)
  const deleteButton = screen.getAllByRole("button", { name: /Delete/i })[0];
  fireEvent.click(deleteButton);

  expect(window.confirm).toHaveBeenCalledWith(
    "Are you sure you want to delete this document?"
  );
  expect(documentAPI.delete).toHaveBeenCalledWith(1);
});

test("does not delete when confirmation is cancelled", async () => {
  documentAPI.getMyDocuments.mockResolvedValue({ data: mockDocuments });
  window.confirm = vi.fn(() => false);

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("test.pdf")).toBeInTheDocument();
  });

  // First delete button for test.pdf (id:1)
  const deleteButton = screen.getAllByRole("button", { name: /Delete/i })[0];
  fireEvent.click(deleteButton);

  expect(window.confirm).toHaveBeenCalled();
  expect(documentAPI.delete).not.toHaveBeenCalled();
});

test("handles refresh button click", async () => {
  documentAPI.getMyDocuments.mockResolvedValue({ data: mockDocuments });

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("test.pdf")).toBeInTheDocument();
  });

  const refreshButton = screen.getByRole("button", { name: /Refresh/i });
  fireEvent.click(refreshButton);

  expect(documentAPI.getMyDocuments).toHaveBeenCalledTimes(2);
});

test("handles retry button click on error", async () => {
  documentAPI.getMyDocuments
    .mockRejectedValueOnce({ response: { data: { message: "Error" } } })
    .mockResolvedValueOnce({ data: mockDocuments });

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("Error")).toBeInTheDocument();
  });

  const retryButton = screen.getByRole("button", { name: /Retry/i });
  fireEvent.click(retryButton);

  await waitFor(() => {
    expect(screen.getByText("test.pdf")).toBeInTheDocument();
  });
});

test("shows progress modal during evaluation", async () => {
  documentAPI.getMyDocuments.mockResolvedValue({ data: mockDocuments });
  documentAPI.evaluate.mockImplementation(
    () => new Promise((resolve) => setTimeout(resolve, 100))
  );

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("test2.docx")).toBeInTheDocument();
  });

  // Second evaluate button for test2.docx (id:2)
  const evaluateButton = screen.getAllByRole("button", {
    name: /Evaluate/i,
  })[1];
  fireEvent.click(evaluateButton);

  await waitFor(() => {
    expect(screen.getByText("Evaluation in progress")).toBeInTheDocument();
  });

  await waitFor(() => {
    expect(
      screen.queryByText("Evaluation in progress")
    ).not.toBeInTheDocument();
  });
});

test("calls onViewReport when view report button is clicked", async () => {
  const mockOnViewReport = vi.fn();
  documentAPI.getMyDocuments.mockResolvedValue({ data: mockDocuments });

  renderComponent({ onViewReport: mockOnViewReport });

  await waitFor(() => {
    expect(screen.getByText("test.pdf")).toBeInTheDocument();
  });

  const viewReportButton = screen.getByRole("button", { name: /View Report/i });
  fireEvent.click(viewReportButton);

  expect(mockOnViewReport).toHaveBeenCalledWith(mockDocuments[0]);
});

test("calls onReplace when edit button is clicked", async () => {
  const mockOnReplace = vi.fn();
  documentAPI.getMyDocuments.mockResolvedValue({ data: mockDocuments });

  renderComponent({ onReplace: mockOnReplace });

  await waitFor(() => {
    expect(screen.getByText("test.pdf")).toBeInTheDocument();
  });

  // First edit button for test.pdf (id:1)
  const editButton = screen.getAllByRole("button", { name: /Edit/i })[0];
  fireEvent.click(editButton);

  expect(mockOnReplace).toHaveBeenCalledWith(mockDocuments[0]);
});

test("handles evaluation error", async () => {
  documentAPI.getMyDocuments.mockResolvedValue({ data: mockDocuments });
  documentAPI.evaluate.mockRejectedValue({
    response: { data: { message: "Evaluation failed" } },
  });
  window.alert = vi.fn();

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("test2.docx")).toBeInTheDocument();
  });

  // Second evaluate button for test2.docx (id:2)
  const evaluateButton = screen.getAllByRole("button", {
    name: /Evaluate/i,
  })[1];
  fireEvent.click(evaluateButton);

  await waitFor(() => {
    expect(window.alert).toHaveBeenCalledWith("Evaluation failed");
  });
});

test("handles re-evaluation error", async () => {
  documentAPI.getMyDocuments.mockResolvedValue({ data: mockDocuments });
  documentAPI.reEvaluate.mockRejectedValue({
    response: { data: { message: "Re-evaluation failed" } },
  });
  window.alert = vi.fn();

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("test.pdf")).toBeInTheDocument();
  });

  const reEvaluateButton = screen.getByRole("button", { name: /Re-evaluate/i });
  fireEvent.click(reEvaluateButton);

  await waitFor(() => {
    expect(window.alert).toHaveBeenCalledWith("Re-evaluation failed");
  });
});

test("handles delete error", async () => {
  documentAPI.getMyDocuments.mockResolvedValue({ data: mockDocuments });
  documentAPI.delete.mockRejectedValue({
    response: { data: { message: "Delete failed" } },
  });
  window.confirm = vi.fn(() => true);
  window.alert = vi.fn();

  renderComponent();

  await waitFor(() => {
    expect(screen.getByText("test.pdf")).toBeInTheDocument();
  });

  // First delete button for test.pdf (id:1)
  const deleteButton = screen.getAllByRole("button", { name: /Delete/i })[0];
  fireEvent.click(deleteButton);

  await waitFor(() => {
    expect(window.alert).toHaveBeenCalledWith("Delete failed");
  });
});
