import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { vi, afterEach, test, expect } from "vitest";
import DocumentUpload from "../components/dashboard/DocumentUpload";

// Mock the apiService
vi.mock("../services/apiService", () => ({
  documentAPI: {
    upload: vi.fn(),
  },
}));

import { documentAPI } from "../services/apiService";

afterEach(() => {
  vi.clearAllMocks();
});

const renderComponent = (props = {}) => {
  return render(<DocumentUpload onUploadSuccess={vi.fn()} {...props} />);
};

test("renders upload component correctly", () => {
  renderComponent();

  expect(screen.getByText("Upload SPMP Document")).toBeInTheDocument();
  expect(
    screen.getByText("Drop your file here or click to browse")
  ).toBeInTheDocument();
  expect(
    screen.getByText("Supports PDF and DOCX files up to 50MB")
  ).toBeInTheDocument();
});

test("handles file selection via click", async () => {
  renderComponent();

  const fileInput =
    screen.getByTestId("file-input") ||
    document.querySelector('input[type="file"]');
  const file = new File(["test content"], "test.pdf", {
    type: "application/pdf",
  });

  fireEvent.change(fileInput, { target: { files: [file] } });

  await waitFor(() => {
    expect(screen.getByText("test.pdf")).toBeInTheDocument();
  });
});

test("validates file type - accepts PDF", () => {
  renderComponent();

  const fileInput =
    screen.getByTestId("file-input") ||
    document.querySelector('input[type="file"]');
  const file = new File(["test content"], "test.pdf", {
    type: "application/pdf",
  });

  fireEvent.change(fileInput, { target: { files: [file] } });

  expect(screen.getByText("test.pdf")).toBeInTheDocument();
});

test("validates file type - accepts DOCX", () => {
  renderComponent();

  const fileInput =
    screen.getByTestId("file-input") ||
    document.querySelector('input[type="file"]');
  const file = new File(["test content"], "test.docx", {
    type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  });

  fireEvent.change(fileInput, { target: { files: [file] } });

  expect(screen.getByText("test.docx")).toBeInTheDocument();
});

test("rejects invalid file type", () => {
  renderComponent();

  const fileInput =
    screen.getByTestId("file-input") ||
    document.querySelector('input[type="file"]');
  const file = new File(["test content"], "test.txt", { type: "text/plain" });

  fireEvent.change(fileInput, { target: { files: [file] } });

  expect(
    screen.getByText("Invalid file type. Please upload PDF or DOCX files only.")
  ).toBeInTheDocument();
});

test("rejects file too large", () => {
  renderComponent();

  const fileInput =
    screen.getByTestId("file-input") ||
    document.querySelector('input[type="file"]');
  const largeFile = new File(["x".repeat(51 * 1024 * 1024)], "large.pdf", {
    type: "application/pdf",
  });

  fireEvent.change(fileInput, { target: { files: [largeFile] } });

  expect(
    screen.getByText("File too large. Maximum size is 50MB.")
  ).toBeInTheDocument();
});

test("handles drag and drop", async () => {
  renderComponent();

  const dropZone = screen.getByTestId("drop-zone");
  const file = new File(["test content"], "test.pdf", {
    type: "application/pdf",
  });

  // Drag over
  fireEvent.dragOver(dropZone);
  await waitFor(() => {
    expect(dropZone).toHaveClass("border-purple-500");
    expect(dropZone).toHaveClass("bg-purple-50");
  });

  // Drop
  fireEvent.drop(dropZone, {
    dataTransfer: {
      files: [file],
      items: [{ kind: "file", type: file.type, getAsFile: () => file }],
    },
  });

  await waitFor(() => {
    expect(screen.getByText("test.pdf")).toBeInTheDocument();
  });
});

test("removes selected file", () => {
  renderComponent();

  const fileInput =
    screen.getByTestId("file-input") ||
    document.querySelector('input[type="file"]');
  const file = new File(["test content"], "test.pdf", {
    type: "application/pdf",
  });

  fireEvent.change(fileInput, { target: { files: [file] } });

  expect(screen.getByText("test.pdf")).toBeInTheDocument();

  const removeButton = screen.getByText("Remove");
  fireEvent.click(removeButton);

  expect(screen.queryByText("test.pdf")).not.toBeInTheDocument();
});

test("handles successful upload", async () => {
  const mockOnUploadSuccess = vi.fn();
  documentAPI.upload.mockResolvedValue({
    data: { id: 1, fileName: "test.pdf" },
  });

  renderComponent({ onUploadSuccess: mockOnUploadSuccess });

  const fileInput =
    screen.getByTestId("file-input") ||
    document.querySelector('input[type="file"]');
  const file = new File(["test content"], "test.pdf", {
    type: "application/pdf",
  });

  fireEvent.change(fileInput, { target: { files: [file] } });

  const uploadButton = screen.getByText("Upload Document");
  fireEvent.click(uploadButton);

  expect(documentAPI.upload).toHaveBeenCalledWith(file);

  await waitFor(() => {
    expect(
      screen.getByText(
        'Document uploaded. Go to "My Documents" and click "Evaluate" to run the analysis.'
      )
    ).toBeInTheDocument();
  });

  expect(mockOnUploadSuccess).toHaveBeenCalledWith({
    id: 1,
    fileName: "test.pdf",
  });
});

test("handles upload error", async () => {
  documentAPI.upload.mockRejectedValue({
    response: { data: { message: "Upload failed" } },
  });

  renderComponent();

  const fileInput =
    screen.getByTestId("file-input") ||
    document.querySelector('input[type="file"]');
  const file = new File(["test content"], "test.pdf", {
    type: "application/pdf",
  });

  fireEvent.change(fileInput, { target: { files: [file] } });

  const uploadButton = screen.getByText("Upload Document");
  fireEvent.click(uploadButton);

  await waitFor(() => {
    expect(screen.getByText("Upload failed")).toBeInTheDocument();
  });
});

test("shows uploading state", async () => {
  documentAPI.upload.mockImplementation(
    () => new Promise((resolve) => setTimeout(resolve, 100))
  );

  renderComponent();

  const fileInput =
    screen.getByTestId("file-input") ||
    document.querySelector('input[type="file"]');
  const file = new File(["test content"], "test.pdf", {
    type: "application/pdf",
  });

  fireEvent.change(fileInput, { target: { files: [file] } });

  const uploadButton = screen.getByText("Upload Document");
  fireEvent.click(uploadButton);

  expect(screen.getByText("Uploading...")).toBeInTheDocument();
});

test("disables upload button during upload", async () => {
  documentAPI.upload.mockImplementation(
    () => new Promise((resolve) => setTimeout(resolve, 100))
  );

  renderComponent();

  const fileInput =
    screen.getByTestId("file-input") ||
    document.querySelector('input[type="file"]');
  const file = new File(["test content"], "test.pdf", {
    type: "application/pdf",
  });

  fireEvent.change(fileInput, { target: { files: [file] } });

  const uploadButton = screen.getByText("Upload Document");
  fireEvent.click(uploadButton);

  expect(uploadButton).toBeDisabled();
});
