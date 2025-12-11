import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

// Mock default api export
vi.mock("../services/apiService", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

import api from "../services/apiService";
import ParserFeedback from "../components/dashboard/ParserFeedback";

afterEach(() => {
  vi.clearAllMocks();
});

const docId = 555;

test("shows empty state and can generate mock feedback", async () => {
  api.get.mockResolvedValueOnce({ data: [] });

  const mockResponse = {
    complianceScore: 88,
    parserVersion: "v1.0",
    analyzedAt: new Date().toISOString(),
    detectedClauses: JSON.stringify([
      { clauseId: "1", clauseName: "Overview", score: 80, location: "page 1" },
    ]),
    missingClauses: JSON.stringify([
      {
        clauseId: "2",
        clauseName: "Scope",
        reason: "missing section",
        severity: "high",
      },
    ]),
    recommendations: JSON.stringify([
      { priority: "high", recommendation: "Add scope section", clauseRef: "2" },
    ]),
    analysisReport: "Detailed analysis here",
  };

  api.post.mockResolvedValueOnce({ data: mockResponse });

  render(<ParserFeedback documentId={docId} />);

  // Empty state shown
  await waitFor(() =>
    expect(
      screen.getByText(/No parser feedback available/i)
    ).toBeInTheDocument()
  );

  const genBtn = screen.getByRole("button", {
    name: /Generate Mock Feedback/i,
  });
  fireEvent.click(genBtn);

  await waitFor(() => {
    expect(api.post).toHaveBeenCalledWith(
      `/parser/feedback/${docId}/generate-mock`
    );
  });

  // After generation, compliance score and detected clause should show
  await waitFor(() => expect(screen.getByText(/88%/)).toBeInTheDocument());
  expect(screen.getByText(/Overview/)).toBeInTheDocument();
  expect(screen.getByText(/Missing or Incomplete Clauses/)).toBeInTheDocument();
  expect(screen.getByText(/AI Recommendations/)).toBeInTheDocument();
  expect(screen.getByText(/Detailed analysis here/)).toBeInTheDocument();
});

test("loads existing feedback and displays sections", async () => {
  const feedback = {
    complianceScore: 75,
    parserVersion: "v0.9",
    analyzedAt: new Date().toISOString(),
    detectedClauses: JSON.stringify([
      { clauseId: "1", clauseName: "Overview", score: 75 },
    ]),
    missingClauses: JSON.stringify([]),
    recommendations: JSON.stringify([]),
    analysisReport: "Report text",
  };

  api.get.mockResolvedValueOnce({ data: [feedback] });

  render(<ParserFeedback documentId={docId} />);

  await waitFor(() => {
    const matches = screen.getAllByText(/75%/);
    expect(matches.length).toBeGreaterThan(0);
  });
  expect(screen.getByText(/Parser Version:/)).toBeInTheDocument();
  expect(screen.getByText(/Overview/)).toBeInTheDocument();
  expect(screen.getByText(/Detailed Analysis/)).toBeInTheDocument();
});

test("shows empty state when load fails", async () => {
  api.get.mockRejectedValueOnce(new Error("network"));

  render(<ParserFeedback documentId={docId} />);

  // component returns the empty-state UI when feedback load fails
  await waitFor(() =>
    expect(
      screen.getByText(/No parser feedback available/i)
    ).toBeInTheDocument()
  );
});
