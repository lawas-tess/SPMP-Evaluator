package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.SPMPDocumentRepository;
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

@Service
@RequiredArgsConstructor
@Transactional
public class SPMPDocumentService {

    private final SPMPDocumentRepository repository;
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
     */
    public String getDocumentContent(Long documentId) throws IOException {
        SPMPDocument document = repository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        return Files.readString(Paths.get(document.getFileUrl()));
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
}
