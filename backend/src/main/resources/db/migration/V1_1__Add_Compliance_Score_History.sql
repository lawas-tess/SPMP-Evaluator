-- Migration: Add ComplianceScoreHistory table for tracking score changes over time
-- Author: John Patrick G. Pepito
-- Date: December 8, 2025
-- Module: Module 4 - Score History Tracking (UC 4.4)

CREATE TABLE IF NOT EXISTS compliance_score_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    overall_score DOUBLE NOT NULL,
    structure_score DOUBLE NOT NULL,
    completeness_score DOUBLE NOT NULL,
    sections_found INT,
    total_sections_required INT,
    is_compliant BOOLEAN DEFAULT FALSE,
    professor_override DOUBLE,
    professor_notes LONGTEXT,
    summary LONGTEXT,
    evaluated_at DATETIME,
    recorded_at DATETIME NOT NULL,
    recorded_by_user_id BIGINT,
    version_number INT,
    source VARCHAR(32),
    CONSTRAINT fk_history_document FOREIGN KEY (document_id) REFERENCES spmp_documents(id) ON DELETE CASCADE,
    INDEX idx_document_recorded (document_id, recorded_at DESC)
);

-- Add comment for documentation
ALTER TABLE compliance_score_history COMMENT = 'Archives compliance score snapshots for audit trail and re-evaluation history';
