package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.dto.ComplianceReportDTO;
import com.team02.spmpevaluator.entity.ComplianceScore;
import com.team02.spmpevaluator.repository.ComplianceScoreRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportExportService {

    private final ComplianceScoreRepository complianceScoreRepository;
    private final ComplianceEvaluationService complianceEvaluationService;

    public byte[] exportPdf(Long documentId) throws IOException {
        ComplianceScore score = complianceScoreRepository.findByDocumentIdWithSectionAnalyses(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Compliance score not found"));
        ComplianceReportDTO report = complianceEvaluationService.convertToDTO(
                score,
                score.getDocument().getId(),
                score.getDocument().getFileName()
        );

        try (PDDocument pdf = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            pdf.addPage(page);

            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;
            int pageNumber = 1;
            PDPageContentStream content = new PDPageContentStream(pdf, page);

            try {
                // Header - Document Title
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                content.newLineAtOffset(margin, y);
                content.showText("SPMP Compliance Report");
                content.endText();
                
                y -= 20;
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                content.newLineAtOffset(margin, y);
                content.showText(report.getDocumentName());
                content.endText();

                // Draw separator line
                y -= 15;
                content.setStrokingColor(0.7f, 0.7f, 0.7f);
                content.setLineWidth(1f);
                content.moveTo(margin, y);
                content.lineTo(page.getMediaBox().getWidth() - margin, y);
                content.stroke();

                // Overall Score Box with compliance badge
                y -= 30;
                String complianceStatus = report.isCompliant() ? "COMPLIANT" : "NON-COMPLIANT";
                float badgeColor = report.isCompliant() ? 0.2f : 0.8f; // green or red
                
                content.setNonStrokingColor(badgeColor, report.isCompliant() ? 0.8f : 0.2f, 0.2f);
                content.addRect(margin, y - 15, 100, 20);
                content.fill();
                
                content.setNonStrokingColor(1f, 1f, 1f); // white text
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                content.newLineAtOffset(margin + 10, y - 10);
                content.showText(complianceStatus);
                content.endText();
                
                content.setNonStrokingColor(0f, 0f, 0f); // reset to black
                y -= 30;
                
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                content.newLineAtOffset(margin, y);
                content.showText("Overall Score: " + Math.round(report.getOverallScore()) + "%");
                content.endText();

                y -= 20;
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                content.newLineAtOffset(margin, y);
                content.showText("Structure: " + formatScore(report.getStructureScore()) + "% | Completeness: " + formatScore(report.getCompletenessScore()) + "%");
                content.endText();

                y -= 30;
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                content.newLineAtOffset(margin, y);
                content.showText("Summary:");
                content.endText();

                y -= 16;
                y = writeWrappedText(content, report.getSummary(), margin, y, 11, 14, page.getMediaBox().getWidth() - (2 * margin));

                // Section Analyses
                if (report.getSectionAnalyses() != null && !report.getSectionAnalyses().isEmpty()) {
                    y -= 20;
                    content.beginText();
                    content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                    content.newLineAtOffset(margin, y);
                    content.showText("Section Breakdown:");
                    content.endText();
                    
                    y -= 16;
                    for (var section : report.getSectionAnalyses()) {
                        if (y < 120) {
                            // Add page number footer
                            content.beginText();
                            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                            content.newLineAtOffset(page.getMediaBox().getWidth() / 2 - 10, 30);
                            content.showText("Page " + pageNumber);
                            content.endText();
                            
                            content.close();
                            page = new PDPage(PDRectangle.LETTER);
                            pdf.addPage(page);
                            content = new PDPageContentStream(pdf, page);
                            y = page.getMediaBox().getHeight() - margin;
                            pageNumber++;
                        }
                        
                        // Section header with score badge
                        double sectionScore = section.getSectionScore();
                        float boxColor = sectionScore >= 80 ? 0.2f : sectionScore >= 60 ? 0.9f : 0.8f;
                        
                        content.setNonStrokingColor(boxColor, sectionScore >= 80 ? 0.8f : sectionScore >= 60 ? 0.7f : 0.2f, 0.2f);
                        content.addRect(page.getMediaBox().getWidth() - margin - 60, y - 12, 60, 18);
                        content.fill();
                        
                        content.setNonStrokingColor(1f, 1f, 1f);
                        content.beginText();
                        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
                        content.newLineAtOffset(page.getMediaBox().getWidth() - margin - 50, y - 8);
                        content.showText(formatScore(sectionScore) + "%");
                        content.endText();
                        
                        content.setNonStrokingColor(0f, 0f, 0f);
                        content.beginText();
                        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 11);
                        content.newLineAtOffset(margin, y);
                        content.showText(section.getSectionName());
                        content.endText();
                        
                        y -= 16;
                        y = writeWrappedText(content, "Findings: " + safe(section.getFindings()), margin + 5, y, 10, 12, page.getMediaBox().getWidth() - (2 * margin) - 10);
                        y = writeWrappedText(content, "Recommendations: " + safe(section.getRecommendations()), margin + 5, y, 10, 12, page.getMediaBox().getWidth() - (2 * margin) - 10);
                        y -= 12;
                    }
                }
                
                // Final page number
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                content.newLineAtOffset(page.getMediaBox().getWidth() / 2 - 10, 30);
                content.showText("Page " + pageNumber);
                content.endText();
                
            } finally {
                content.close();
            }

            pdf.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exportExcel(Long documentId) throws IOException {
        ComplianceScore score = complianceScoreRepository.findByDocumentIdWithSectionAnalyses(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Compliance score not found"));
        ComplianceReportDTO report = complianceEvaluationService.convertToDTO(
                score,
                score.getDocument().getId(),
                score.getDocument().getFileName()
        );

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Compliance Report");
            int rowIdx = 0;

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Field");
            header.createCell(1).setCellValue("Value");

            rowIdx = writeRow(sheet, rowIdx, "Document", report.getDocumentName());
            rowIdx = writeRow(sheet, rowIdx, "Overall Score", Math.round(report.getOverallScore()) + "%");
            rowIdx = writeRow(sheet, rowIdx, "Structure Score", formatScore(report.getStructureScore()) + "%");
            rowIdx = writeRow(sheet, rowIdx, "Completeness Score", formatScore(report.getCompletenessScore()) + "%");
            rowIdx = writeRow(sheet, rowIdx, "Compliant", report.isCompliant() ? "Yes" : "No");
            rowIdx = writeRow(sheet, rowIdx, "Summary", safe(report.getSummary()));

            if (report.getSectionAnalyses() != null && !report.getSectionAnalyses().isEmpty()) {
                rowIdx += 1;
                Row sectionHeader = sheet.createRow(rowIdx++);
                sectionHeader.createCell(0).setCellValue("Section");
                sectionHeader.createCell(1).setCellValue("Score");
                sectionHeader.createCell(2).setCellValue("Findings");
                sectionHeader.createCell(3).setCellValue("Recommendations");

                for (var section : report.getSectionAnalyses()) {
                    Row r = sheet.createRow(rowIdx++);
                    r.createCell(0).setCellValue(section.getSectionName());
                    r.createCell(1).setCellValue(formatScore(section.getSectionScore()));
                    r.createCell(2).setCellValue(safe(section.getFindings()));
                    r.createCell(3).setCellValue(safe(section.getRecommendations()));
                }
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    private int writeRow(XSSFSheet sheet, int rowIdx, String label, String value) {
        Row row = sheet.createRow(rowIdx++);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        return rowIdx;
    }

    private float writeWrappedText(PDPageContentStream content,
                                   String text,
                                   float startX,
                                   float startY,
                                   int fontSize,
                                   float leading,
                                   float maxWidth) throws IOException {
        if (text == null) {
            return startY;
        }
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float y = startY;
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        content.setFont(font, fontSize);
        content.beginText();
        content.newLineAtOffset(startX, y);
        for (String word : words) {
            String testLine = line + word + " ";
            float size = font.getStringWidth(testLine) / 1000 * fontSize;
            if (size > maxWidth) {
                content.showText(line.toString());
                content.endText();
                y -= leading;
                content.beginText();
                content.newLineAtOffset(startX, y);
                line = new StringBuilder(word + " ");
            } else {
                line.append(word).append(" ");
            }
        }
        content.showText(line.toString());
        content.endText();
        return y - leading;
    }

    private String formatScore(Double score) {
        if (score == null) {
            return "0";
        }
        return String.format("%.2f", score);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
