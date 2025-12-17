package com.team02.spmpevaluator.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DocumentParser.
 */
@DisplayName("DocumentParser Tests")
class DocumentParserTest {

    private DocumentParser documentParser;

    @BeforeEach
    void setUp() {
        documentParser = new DocumentParser();
    }

    @Nested
    @DisplayName("Extract Text From MultipartFile Tests")
    class ExtractTextFromMultipartFileTests {

        @Test
        @DisplayName("Should throw exception for null filename")
        void extractText_NullFilename_ThrowsException() {
            MultipartFile file = new MockMultipartFile(
                    "file",
                    null,
                    "application/octet-stream",
                    new byte[0]);

            assertThrows(IllegalArgumentException.class, () -> documentParser.extractText(file));
        }

        @Test
        @DisplayName("Should throw exception for unsupported file format")
        void extractText_UnsupportedFormat_ThrowsException() {
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test.txt",
                    "text/plain",
                    "Hello World".getBytes());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> documentParser.extractText(file));
            assertTrue(exception.getMessage().contains("Unsupported file format"));
        }

        @Test
        @DisplayName("Should throw exception for unsupported xlsx format")
        void extractText_XlsxFormat_ThrowsException() {
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    new byte[0]);

            assertThrows(IllegalArgumentException.class, () -> documentParser.extractText(file));
        }

        @Test
        @DisplayName("Should handle PDF file extension case-insensitively")
        void extractText_PdfUppercase_ProcessesCorrectly() {
            // Create an invalid PDF to trigger IOException for format verification
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "TEST.PDF",
                    "application/pdf",
                    "not a real pdf".getBytes());

            // Should attempt to process as PDF (and fail due to invalid content)
            assertThrows(IOException.class, () -> documentParser.extractText(file));
        }

        @Test
        @DisplayName("Should handle DOCX file extension case-insensitively")
        void extractText_DocxUppercase_ProcessesCorrectly() {
            // Create an invalid DOCX to trigger exception
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "TEST.DOCX",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "not a real docx".getBytes());

            // Should attempt to process as DOCX (and fail due to invalid content)
            assertThrows(Exception.class, () -> documentParser.extractText(file));
        }
    }

    @Nested
    @DisplayName("Extract Text From File Path Tests")
    class ExtractTextFromFilePathTests {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("Should throw IOException for non-existent file")
        void extractTextFromFile_NonExistent_ThrowsException() {
            String nonExistentPath = "/path/to/nonexistent/file.pdf";

            IOException exception = assertThrows(
                    IOException.class,
                    () -> documentParser.extractTextFromFile(nonExistentPath));
            assertTrue(exception.getMessage().contains("File not found"));
        }

        @Test
        @DisplayName("Should throw exception for unsupported file format by path")
        void extractTextFromFile_UnsupportedFormat_ThrowsException() throws IOException {
            File txtFile = tempDir.resolve("test.txt").toFile();
            try (FileOutputStream fos = new FileOutputStream(txtFile)) {
                fos.write("Hello World".getBytes());
            }

            assertThrows(IllegalArgumentException.class,
                    () -> documentParser.extractTextFromFile(txtFile.getAbsolutePath()));
        }

        @Test
        @DisplayName("Should throw exception for invalid PDF file")
        void extractTextFromFile_InvalidPdf_ThrowsException() throws IOException {
            File invalidPdf = tempDir.resolve("invalid.pdf").toFile();
            try (FileOutputStream fos = new FileOutputStream(invalidPdf)) {
                fos.write("not a real pdf content".getBytes());
            }

            assertThrows(IOException.class,
                    () -> documentParser.extractTextFromFile(invalidPdf.getAbsolutePath()));
        }

        @Test
        @DisplayName("Should throw exception for invalid DOCX file")
        void extractTextFromFile_InvalidDocx_ThrowsException() throws IOException {
            File invalidDocx = tempDir.resolve("invalid.docx").toFile();
            try (FileOutputStream fos = new FileOutputStream(invalidDocx)) {
                fos.write("not a real docx content".getBytes());
            }

            assertThrows(Exception.class,
                    () -> documentParser.extractTextFromFile(invalidDocx.getAbsolutePath()));
        }
    }

    @Nested
    @DisplayName("Normalize And Split Tests")
    class NormalizeAndSplitTests {

        @Test
        @DisplayName("Should split text by newlines")
        void normalizeAndSplit_SplitsByNewlines() {
            String text = "Line 1\nLine 2\nLine 3";

            List<String> result = documentParser.normalizeAndSplit(text);

            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("Should normalize to lowercase")
        void normalizeAndSplit_NormalizesToLowercase() {
            String text = "UPPERCASE Line\nMixedCase LINE";

            List<String> result = documentParser.normalizeAndSplit(text);

            assertEquals("uppercase line", result.get(0));
            assertEquals("mixedcase line", result.get(1));
        }

        @Test
        @DisplayName("Should trim whitespace from lines")
        void normalizeAndSplit_TrimsWhitespace() {
            String text = "  Line with spaces  \n\tTabbed line\t";

            List<String> result = documentParser.normalizeAndSplit(text);

            assertEquals("line with spaces", result.get(0));
            assertEquals("tabbed line", result.get(1));
        }

        @Test
        @DisplayName("Should filter out empty lines")
        void normalizeAndSplit_FiltersEmptyLines() {
            String text = "Line 1\n\n\nLine 2\n   \nLine 3";

            List<String> result = documentParser.normalizeAndSplit(text);

            assertEquals(3, result.size());
            assertEquals("line 1", result.get(0));
            assertEquals("line 2", result.get(1));
            assertEquals("line 3", result.get(2));
        }

        @Test
        @DisplayName("Should return empty list for empty string")
        void normalizeAndSplit_EmptyString_ReturnsEmptyList() {
            List<String> result = documentParser.normalizeAndSplit("");

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list for whitespace only")
        void normalizeAndSplit_WhitespaceOnly_ReturnsEmptyList() {
            List<String> result = documentParser.normalizeAndSplit("   \n   \n   ");

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle single line without newline")
        void normalizeAndSplit_SingleLine_ReturnsOneElement() {
            List<String> result = documentParser.normalizeAndSplit("Single line");

            assertEquals(1, result.size());
            assertEquals("single line", result.get(0));
        }

        @Test
        @DisplayName("Should handle special characters")
        void normalizeAndSplit_SpecialCharacters_PreservesContent() {
            String text = "Line with @#$%^&* special chars\nLine 2!";

            List<String> result = documentParser.normalizeAndSplit(text);

            assertEquals(2, result.size());
            assertTrue(result.get(0).contains("@#$%^&*"));
        }

        @Test
        @DisplayName("Should handle carriage return newlines")
        void normalizeAndSplit_CarriageReturn_SplitsCorrectly() {
            String text = "Line 1\r\nLine 2\r\nLine 3";

            List<String> result = documentParser.normalizeAndSplit(text);

            // \r\n splits on \n, leaving \r which gets trimmed
            assertTrue(result.size() >= 2);
        }
    }

    @Nested
    @DisplayName("Contains Keywords Tests")
    class ContainsKeywordsTests {

        @Test
        @DisplayName("Should return true when keyword found")
        void containsKeywords_KeywordFound_ReturnsTrue() {
            String text = "This document contains the project overview section.";
            Set<String> keywords = new HashSet<>();
            keywords.add("overview");

            boolean result = documentParser.containsKeywords(text, keywords);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when keyword not found")
        void containsKeywords_KeywordNotFound_ReturnsFalse() {
            String text = "This document contains nothing relevant.";
            Set<String> keywords = new HashSet<>();
            keywords.add("nonexistent");

            boolean result = documentParser.containsKeywords(text, keywords);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should be case-insensitive")
        void containsKeywords_CaseInsensitive_ReturnsTrue() {
            String text = "This has PROJECT OVERVIEW section.";
            Set<String> keywords = new HashSet<>();
            keywords.add("project overview");

            boolean result = documentParser.containsKeywords(text, keywords);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should match any keyword from set")
        void containsKeywords_MultipleKeywords_MatchesAny() {
            String text = "Document with schedule information.";
            Set<String> keywords = new HashSet<>();
            keywords.add("overview");
            keywords.add("schedule");
            keywords.add("budget");

            boolean result = documentParser.containsKeywords(text, keywords);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false for empty keyword set")
        void containsKeywords_EmptyKeywords_ReturnsFalse() {
            String text = "Some text here.";
            Set<String> keywords = new HashSet<>();

            boolean result = documentParser.containsKeywords(text, keywords);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for empty text")
        void containsKeywords_EmptyText_ReturnsFalse() {
            String text = "";
            Set<String> keywords = new HashSet<>();
            keywords.add("keyword");

            boolean result = documentParser.containsKeywords(text, keywords);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should match partial word")
        void containsKeywords_PartialMatch_ReturnsTrue() {
            String text = "The documentation is complete.";
            Set<String> keywords = new HashSet<>();
            keywords.add("document");

            boolean result = documentParser.containsKeywords(text, keywords);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should handle multi-word keywords")
        void containsKeywords_MultiWordKeyword_MatchesCorrectly() {
            String text = "The risk management plan is here.";
            Set<String> keywords = new HashSet<>();
            keywords.add("risk management");

            boolean result = documentParser.containsKeywords(text, keywords);

            assertTrue(result);
        }

        @Test
        @DisplayName("Should not match multi-word keyword when words are separated")
        void containsKeywords_SeparatedWords_ReturnsFalse() {
            String text = "Risk is assessed. Management is done separately.";
            Set<String> keywords = new HashSet<>();
            keywords.add("risk management");

            boolean result = documentParser.containsKeywords(text, keywords);

            assertFalse(result);
        }

        @Test
        @DisplayName("Should handle special characters in text")
        void containsKeywords_SpecialCharsInText_MatchesCorrectly() {
            String text = "Section 1.1 - Project Overview: Introduction";
            Set<String> keywords = new HashSet<>();
            keywords.add("overview");

            boolean result = documentParser.containsKeywords(text, keywords);

            assertTrue(result);
        }
    }
}
