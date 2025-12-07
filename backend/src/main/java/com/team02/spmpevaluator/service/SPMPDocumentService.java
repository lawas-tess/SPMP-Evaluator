package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.ComplianceScore;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.ComplianceScoreRepository;
import com.team02.spmpevaluator.repository.SPMPDocumentRepository;
import com.team02.spmpevaluator.util.DocumentParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SPMPDocumentService {

    private final SPMPDocumentRepository repository;
    private final DocumentParser documentParser;
    private final ComplianceScoreRepository complianceScoreRepository;
    private final NotificationService notificationService;
    private static final String UPLOAD_DIR = "uploads/documents/";
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    /**
     * Uploads a document and saves it to the file system and database.
     */
    public SPMPDocument uploadDocument(MultipartFile file, User uploadedBy) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 50MB");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || (!originalFileName.endsWith(".pdf") && !originalFileName.endsWith(".docx"))) {
            throw new IllegalArgumentException("Only PDF and DOCX files are supported");
        }

        // Generate unique filename
        String fileName = UUID.randomUUID() + "_" + originalFileName;
        Path uploadPath = Paths.get(UPLOAD_DIR);

        // Create upload directory if not exists
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        // Create document entity
        SPMPDocument document = new SPMPDocument();
        document.setFileName(originalFileName);
        document.setFileUrl(filePath.toString());
        document.setFileSize(file.getSize());
        document.setFileType(getFileType(originalFileName));
        document.setUploadedBy(uploadedBy);
        document.setEvaluated(false);

        return repository.save(document);
    }

    /**
     * Retrieves a document by ID.
     */
    public Optional<SPMPDocument> getDocumentById(Long id) {
        return repository.findById(id);
    }

    /**
     * Retrieves all documents uploaded by a specific user.
     */
    public List<SPMPDocument> getDocumentsByUser(Long userId) {
        return repository.findByUploadedBy_Id(userId);
    }

    /**
     * Retrieves all documents with pagination.
     */
    public Page<SPMPDocument> getAllDocuments(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Retrieves evaluated documents.
     */
    public List<SPMPDocument> getEvaluatedDocuments(Long userId) {
        return repository.findByUploadedBy_IdAndEvaluated(userId, true);
    }

    /**
     * Retrieves unevaluated documents.
     */
    public List<SPMPDocument> getUnevaluatedDocuments(Long userId) {
        return repository.findByUploadedBy_IdAndEvaluated(userId, false);
    }

    /**
     * Updates document evaluation status and feedback.
     */
    public SPMPDocument updateDocumentEvaluation(Long documentId, String feedback, boolean evaluated) {
        SPMPDocument document = repository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        document.setEvaluated(evaluated);
        document.setFeedback(feedback);
        document.setEvaluatedAt(LocalDateTime.now());

        return repository.save(document);
    }

    /**
     * Deletes a document (only if uploaded by the current user).
     */
    public void deleteDocument(Long documentId, Long userId) throws IOException {
        SPMPDocument document = repository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        if (!document.getUploadedBy().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: You can only delete your own documents");
        }

        // Delete file from system
        Files.deleteIfExists(Paths.get(document.getFileUrl()));

        // Delete from database
        repository.delete(document);
    }

    /**
     * Adds notes to a document.
     */
    public SPMPDocument addNotes(Long documentId, String notes) {
        SPMPDocument document = repository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        document.setNotes(notes);
        document.setUpdatedAt(LocalDateTime.now());

        return repository.save(document);
    }

    /**
     * Gets file content as string (for processing).
     * Uses DocumentParser to properly extract text from PDF/DOCX files.
     */
    public String getDocumentContent(Long documentId) throws IOException {
        SPMPDocument document = repository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        String filePath = document.getFileUrl();
        String fileName = document.getFileName().toLowerCase();
        
        // Use DocumentParser for PDF and DOCX files
        if (fileName.endsWith(".pdf") || fileName.endsWith(".docx")) {
            return documentParser.extractTextFromFile(filePath);
        }
        
        // Fallback to raw read for plain text files
        return Files.readString(Paths.get(filePath));
    }

    /**
     * Extracts file type from filename.
     */
    private String getFileType(String fileName) {
        if (fileName.endsWith(".pdf")) {
            return "PDF";
        } else if (fileName.endsWith(".docx")) {
            return "DOCX";
        }
        return "UNKNOWN";
    }

    /**
     * Replace an existing document (Use Case 2.2 - File Edit).
     * Deletes old file and uploads new one while preserving document ID.
     */
    public SPMPDocument replaceDocument(Long documentId, MultipartFile file, User user) throws IOException {
        SPMPDocument existingDoc = repository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 50MB");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || (!originalFileName.endsWith(".pdf") && !originalFileName.endsWith(".docx"))) {
            throw new IllegalArgumentException("Only PDF and DOCX files are supported");
        }

        // Delete old file
        Files.deleteIfExists(Paths.get(existingDoc.getFileUrl()));

        // Generate unique filename for new file
        String fileName = UUID.randomUUID() + "_" + originalFileName;
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save new file
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        // Update document entity
        existingDoc.setFileName(originalFileName);
        existingDoc.setFileUrl(filePath.toString());
        existingDoc.setFileSize(file.getSize());
        existingDoc.setFileType(getFileType(originalFileName));
        existingDoc.setUpdatedAt(LocalDateTime.now());
        existingDoc.setEvaluated(false); // Reset evaluation status
        existingDoc.setFeedback(null);
        existingDoc.setComplianceScore(null); // Clear previous score

        return repository.save(existingDoc);
    }

    /**
     * Get all submissions with optional filters (Use Case 2.7 - Submission Tracker).
     * Professors can view all student submissions.
     */
    public List<SPMPDocument> getAllSubmissions(String status, Long studentId) {
        List<SPMPDocument> allDocs = repository.findAll();

        return allDocs.stream()
                .filter(doc -> {
                    // Filter by status if provided
                    if (status != null && !status.isEmpty()) {
                        if (status.equalsIgnoreCase("evaluated") && !doc.isEvaluated()) {
                            return false;
                        }
                        if (status.equalsIgnoreCase("pending") && doc.isEvaluated()) {
                            return false;
                        }
                    }
                    // Filter by student if provided
                    if (studentId != null && !doc.getUploadedBy().getId().equals(studentId)) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Override document evaluation score (Use Case 2.8 - Override AI Results).
     * Professors can review AI-generated evaluations and override if necessary.
     * Notifies student of score override (UC 2.8 Step 5).
     */
    public SPMPDocument overrideScore(Long documentId, Double newScore, String notes, User professor) {
        SPMPDocument document = repository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        if (!document.isEvaluated() || document.getComplianceScore() == null) {
            throw new IllegalArgumentException("Document has not been evaluated yet");
        }

        ComplianceScore complianceScore = document.getComplianceScore();
        complianceScore.setProfessorOverride(newScore);
        complianceScore.setProfessorNotes(notes);
        complianceScore.setReviewedBy(professor);
        complianceScore.setReviewedAt(LocalDateTime.now());

        complianceScoreRepository.save(complianceScore);
        
        // UC 2.8: Notify student of score override
        notificationService.notifyScoreOverride(
            document.getUploadedBy().getId(), 
            documentId, 
            newScore,
            notes
        );

        return document;
    }
}
