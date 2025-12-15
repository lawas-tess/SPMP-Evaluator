package com.team02.spmpevaluator.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility component for extracting text from various document formats.
 */
@Component
public class DocumentParser {

    /**
     * Extracts text from uploaded files (PDF or DOCX).
     */
    public String extractText(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        if (fileName.toLowerCase().endsWith(".pdf")) {
            return extractFromPDF(file.getBytes());
        } else if (fileName.toLowerCase().endsWith(".docx")) {
            return extractFromDOCX(file.getInputStream());
        } else {
            throw new IllegalArgumentException("Unsupported file format. Please use PDF or DOCX.");
        }
    }

    /**
     * Extracts text from PDF files.
     */
    private String extractFromPDF(byte[] fileContent) throws IOException {
        try (PDDocument document = Loader.loadPDF(fileContent)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Extracts text from DOCX files.
     */
    private String extractFromDOCX(java.io.InputStream inputStream) throws IOException {
        StringBuilder textContent = new StringBuilder();
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (!text.isBlank()) {
                    textContent.append(text).append("\n");
                }
            }
        }
        return textContent.toString();
    }

    /**
     * Extracts text from a file by path.
     */
    public String extractTextFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        if (filePath.toLowerCase().endsWith(".pdf")) {
            try (FileInputStream fis = new FileInputStream(file)) {
                return extractFromPDF(fis.readAllBytes());
            }
        } else if (filePath.toLowerCase().endsWith(".docx")) {
            try (FileInputStream fis = new FileInputStream(file)) {
                return extractFromDOCX(fis);
            }
        }

        throw new IllegalArgumentException("Unsupported file format");
    }

    /**
     * Splits text into lines and normalizes whitespace.
     */
    public List<String> normalizeAndSplit(String text) {
        List<String> lines = new ArrayList<>();
        for (String line : text.split("\\n")) {
            String normalized = line.trim().toLowerCase();
            if (!normalized.isEmpty()) {
                lines.add(normalized);
            }
        }
        return lines;
    }

    /**
     * Checks if any keyword from a set appears in the text (case-insensitive).
     */
    public boolean containsKeywords(String text, java.util.Set<String> keywords) {
        String lowerText = text.toLowerCase();
        return keywords.stream().anyMatch(lowerText::contains);
    }
}
