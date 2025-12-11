import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";

// Mock apiService
vi.mock("../services/apiService", () => ({
  documentAPI: {
    replace: vi.fn(),
  },
}));

import { documentAPI } from "../services/apiService";
import FileReplaceModal from "../components/dashboard/FileReplaceModal";

afterEach(() => {
  vi.clearAllMocks();
});

const mockDocument = {
  id: 42,
  fileName: "original.docx",
};

test("renders modal and shows current document info", () => {
  render(
    <FileReplaceModal
      document={mockDocument}
      onClose={vi.fn()}
      onSuccess={vi.fn()}
    />
  );

  expect(screen.getByText(/Replace Document/i)).toBeInTheDocument();
  expect(screen.getByText(/Current Document:/i)).toBeInTheDocument();
  expect(screen.getByText(/original.docx/i)).toBeInTheDocument();
});

test("validates DOCX file and calls replace on confirm", async () => {
  documentAPI.replace.mockResolvedValue({});

  const onClose = vi.fn();
  const onSuccess = vi.fn();

  const { container } = render(
    <FileReplaceModal
      document={mockDocument}
      onClose={onClose}
      onSuccess={onSuccess}
    />
  );

  const file = new File(["dummy content"], "newfile.docx", {
    type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  });

  const input = container.querySelector('input[type="file"]');
  fireEvent.change(input, { target: { files: [file] } });

  await waitFor(() =>
    expect(screen.getByText(/File validated successfully/i)).toBeInTheDocument()
  );

  const confirmBtn = screen.getByRole("button", { name: /Confirm Replace/i });
  expect(confirmBtn).toBeEnabled();

  fireEvent.click(confirmBtn);

  await waitFor(() => {
    expect(documentAPI.replace).toHaveBeenCalledWith(mockDocument.id, file);
    expect(onSuccess).toHaveBeenCalled();
    expect(onClose).toHaveBeenCalled();
  });
});

test("shows validation error for invalid type", async () => {
  const { container } = render(
    <FileReplaceModal
      document={mockDocument}
      onClose={vi.fn()}
      onSuccess={vi.fn()}
    />
  );

  const file = new File(["text"], "notes.txt", { type: "text/plain" });
  const input = container.querySelector('input[type="file"]');
  fireEvent.change(input, { target: { files: [file] } });

  await waitFor(() =>
    expect(screen.getByText(/Invalid file type/i)).toBeInTheDocument()
  );

  const confirmBtn = screen.getByRole("button", { name: /Confirm Replace/i });
  expect(confirmBtn).toBeDisabled();
});

test("shows validation error for oversized file", async () => {
  const { container } = render(
    <FileReplaceModal
      document={mockDocument}
      onClose={vi.fn()}
      onSuccess={vi.fn()}
    />
  );

  // create a file larger than 50MB
  const largeFile = new File([new ArrayBuffer(51 * 1024 * 1024)], "big.pdf", {
    type: "application/pdf",
  });
  const input = container.querySelector('input[type="file"]');
  fireEvent.change(input, { target: { files: [largeFile] } });

  await waitFor(() =>
    expect(screen.getByText(/File too large/i)).toBeInTheDocument()
  );

  const confirmBtn = screen.getByRole("button", { name: /Confirm Replace/i });
  expect(confirmBtn).toBeDisabled();
});

test("handles replace API error and shows message", async () => {
  documentAPI.replace.mockRejectedValue({
    response: { data: { message: "Replace failed" } },
  });

  const { container } = render(
    <FileReplaceModal
      document={mockDocument}
      onClose={vi.fn()}
      onSuccess={vi.fn()}
    />
  );

  const file = new File(["dummy content"], "newfile.pdf", {
    type: "application/pdf",
  });
  const input = container.querySelector('input[type="file"]');
  fireEvent.change(input, { target: { files: [file] } });

  await waitFor(() =>
    expect(screen.getByText(/File validated successfully/i)).toBeInTheDocument()
  );

  const confirmBtn = screen.getByRole("button", { name: /Confirm Replace/i });
  fireEvent.click(confirmBtn);

  await waitFor(() =>
    expect(screen.getByText(/Replace failed/i)).toBeInTheDocument()
  );
});
