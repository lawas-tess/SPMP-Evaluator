import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

// Mock the apiService used by the component
vi.mock("../services/apiService", () => ({
  documentAPI: {
    getReport: vi.fn(),
    getHistory: vi.fn(),
    exportPdf: vi.fn(),
    exportExcel: vi.fn(),
  },
}));

import { documentAPI } from "../services/apiService";
import EvaluationResults from "../components/dashboard/EvaluationResults";

afterEach(() => {
  vi.clearAllMocks();
});

const defaultDocument = {
  id: 123,
  fileName: "sample-spmp.pdf",
  complianceScore: { overallScore: 60 },
};

test("renders report and can expand section details", async () => {
  documentAPI.getReport.mockResolvedValue({
    data: {
      overallScore: 75,
      sectionAnalyses: [
        {
          id: "sec1",
          sectionName: "Overview",
          sectionScore: 75,
          coverage: 70,
          findings: "Some findings",
          recommendations: "Some recommendations",
          missingSubclauses: ["1.1", "1.2"],
          evidenceSnippet: "evidence",
          severity: "MEDIUM",
        },
      ],
    },
  });

  documentAPI.getHistory.mockResolvedValue({ data: [] });

  render(<EvaluationResults document={defaultDocument} onClose={vi.fn()} />);

  // wait for overall score to appear (there may be multiple % elements)
  await waitFor(() => {
    const matches = screen.getAllByText(/75%/);
    expect(matches.length).toBeGreaterThan(0);
  });

  // section title present
  expect(screen.getByText("Overview")).toBeInTheDocument();

  // expand the section
  const sectionButton = screen.getByText("Overview");
  fireEvent.click(sectionButton);

  // details should show after expanding
  await waitFor(() => {
    expect(screen.getByText(/Findings:/i)).toBeInTheDocument();
    expect(screen.getByText(/Recommendations:/i)).toBeInTheDocument();
    expect(screen.getByText(/Missing \/ Weak Subclauses/i)).toBeInTheDocument();
  });
});

test("shows error state and Back button calls onClose", async () => {
  documentAPI.getReport.mockRejectedValue({
    response: { data: { message: "Failed to load" } },
  });
  documentAPI.getHistory.mockResolvedValue({ data: [] });

  const mockOnClose = vi.fn();
  render(
    <EvaluationResults document={defaultDocument} onClose={mockOnClose} />
  );

  await waitFor(() => {
    expect(screen.getByText("Failed to load")).toBeInTheDocument();
  });

  const backButton = screen.getByText(/Back to Documents/i);
  fireEvent.click(backButton);

  expect(mockOnClose).toHaveBeenCalled();
});

test("export PDF calls API and uses URL.createObjectURL", async () => {
  documentAPI.getReport.mockResolvedValue({
    data: {
      overallScore: 90,
      sectionAnalyses: [],
    },
  });
  documentAPI.getHistory.mockResolvedValue({ data: [] });

  // mock export response and URL helpers
  documentAPI.exportPdf.mockResolvedValue({ data: new Uint8Array([1, 2, 3]) });
  const originalCreate = window.URL.createObjectURL;
  const originalRevoke = window.URL.revokeObjectURL;
  window.URL.createObjectURL = vi.fn(() => "blob:mock");
  window.URL.revokeObjectURL = vi.fn(() => {});

  render(<EvaluationResults document={defaultDocument} onClose={vi.fn()} />);

  await waitFor(() => {
    expect(screen.getByText(/90%/)).toBeInTheDocument();
  });

  const pdfButton = screen.getByRole("button", { name: /PDF/i });
  fireEvent.click(pdfButton);

  await waitFor(() => {
    expect(documentAPI.exportPdf).toHaveBeenCalledWith(defaultDocument.id);
    expect(window.URL.createObjectURL).toHaveBeenCalled();
  });

  // cleanup mocks
  window.URL.createObjectURL = originalCreate;
  window.URL.revokeObjectURL = originalRevoke;
});
