package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.dto.GradingCriteriaDTO;
import com.team02.spmpevaluator.entity.GradingCriteria;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.GradingCriteriaRepository;
import com.team02.spmpevaluator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for GradingCriteria operations.
 * UC 2.7 - Manages grading criteria for SPMP document evaluation.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GradingCriteriaService {

    private final GradingCriteriaRepository gradingCriteriaRepository;
    private final UserRepository userRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Create a new grading criteria configuration.
     * 
     * @param dto GradingCriteria data
     * @param professorId ID of the professor creating the criteria
     * @return Created GradingCriteriaDTO
     */
    public GradingCriteriaDTO createGradingCriteria(GradingCriteriaDTO dto, Long professorId) {
        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor not found"));

        // Validate weights sum to 100
        if (!dto.isValidWeights()) {
            throw new IllegalArgumentException("Section weights must sum to 100. Current total: " + dto.getTotalWeight());
        }

        // Check for duplicate name
        if (gradingCriteriaRepository.existsByNameAndCreatedBy(dto.getName(), professor)) {
            throw new IllegalArgumentException("A grading criteria with this name already exists");
        }

        GradingCriteria criteria = new GradingCriteria();
        criteria.setName(dto.getName());
        criteria.setDescription(dto.getDescription());
        criteria.setCreatedBy(professor);
        criteria.setActive(dto.isActive());
        setWeightsFromDTO(criteria, dto);

        // If setting as active, deactivate others first
        if (dto.isActive()) {
            gradingCriteriaRepository.deactivateAllCriteriaForProfessor(professorId);
        }

        GradingCriteria saved = gradingCriteriaRepository.save(criteria);
        return convertToDTO(saved);
    }

    /**
     * Update an existing grading criteria.
     * 
     * @param id Criteria ID
     * @param dto Updated data
     * @param professorId ID of the professor (must be the creator)
     * @return Updated GradingCriteriaDTO
     */
    public GradingCriteriaDTO updateGradingCriteria(Long id, GradingCriteriaDTO dto, Long professorId) {
        GradingCriteria criteria = gradingCriteriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grading criteria not found"));

        // Verify ownership
        if (!criteria.getCreatedBy().getId().equals(professorId)) {
            throw new IllegalArgumentException("You can only update your own grading criteria");
        }

        // Validate weights sum to 100
        if (!dto.isValidWeights()) {
            throw new IllegalArgumentException("Section weights must sum to 100. Current total: " + dto.getTotalWeight());
        }

        // Check for duplicate name (excluding current)
        Optional<GradingCriteria> existingWithName = gradingCriteriaRepository
                .findByNameAndCreatedBy(dto.getName(), criteria.getCreatedBy());
        if (existingWithName.isPresent() && !existingWithName.get().getId().equals(id)) {
            throw new IllegalArgumentException("A grading criteria with this name already exists");
        }

        criteria.setName(dto.getName());
        criteria.setDescription(dto.getDescription());
        setWeightsFromDTO(criteria, dto);

        // Handle active status change
        if (dto.isActive() && !criteria.isActive()) {
            gradingCriteriaRepository.deactivateAllCriteriaForProfessor(professorId);
        }
        criteria.setActive(dto.isActive());

        GradingCriteria saved = gradingCriteriaRepository.save(criteria);
        return convertToDTO(saved);
    }

    /**
     * Delete a grading criteria.
     * 
     * @param id Criteria ID
     * @param professorId ID of the professor (must be the creator)
     */
    public void deleteGradingCriteria(Long id, Long professorId) {
        GradingCriteria criteria = gradingCriteriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grading criteria not found"));

        // Verify ownership
        if (!criteria.getCreatedBy().getId().equals(professorId)) {
            throw new IllegalArgumentException("You can only delete your own grading criteria");
        }

        gradingCriteriaRepository.delete(criteria);
    }

    /**
     * Get all grading criteria for a professor.
     * 
     * @param professorId Professor ID
     * @return List of GradingCriteriaDTO
     */
    public List<GradingCriteriaDTO> getGradingCriteriaByProfessor(Long professorId) {
        return gradingCriteriaRepository.findByProfessorId(professorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get the active grading criteria for a professor.
     * 
     * @param professorId Professor ID
     * @return Active GradingCriteriaDTO or default criteria
     */
    public GradingCriteriaDTO getActiveCriteriaByProfessor(Long professorId) {
        return gradingCriteriaRepository.findActiveCriteriaByProfessorId(professorId)
                .map(this::convertToDTO)
                .orElse(getDefaultCriteria());
    }

    /**
     * Get a specific grading criteria by ID.
     * 
     * @param id Criteria ID
     * @return GradingCriteriaDTO
     */
    public GradingCriteriaDTO getGradingCriteriaById(Long id) {
        return gradingCriteriaRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Grading criteria not found"));
    }

    /**
     * Set a grading criteria as active.
     * 
     * @param id Criteria ID
     * @param professorId Professor ID
     * @return Updated GradingCriteriaDTO
     */
    public GradingCriteriaDTO setActiveGradingCriteria(Long id, Long professorId) {
        GradingCriteria criteria = gradingCriteriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grading criteria not found"));

        // Verify ownership
        if (!criteria.getCreatedBy().getId().equals(professorId)) {
            throw new IllegalArgumentException("You can only activate your own grading criteria");
        }

        // Deactivate all others and activate this one
        gradingCriteriaRepository.deactivateAllCriteriaForProfessor(professorId);
        criteria.setActive(true);
        
        GradingCriteria saved = gradingCriteriaRepository.save(criteria);
        return convertToDTO(saved);
    }

    /**
     * Get default grading criteria (when professor hasn't set custom weights).
     */
    public GradingCriteriaDTO getDefaultCriteria() {
        GradingCriteriaDTO defaultCriteria = new GradingCriteriaDTO();
        defaultCriteria.setName("Default IEEE 1058 Weights");
        defaultCriteria.setDescription("Standard IEEE 1058 section weights for SPMP evaluation");
        defaultCriteria.setOverviewWeight(10);
        defaultCriteria.setReferencesWeight(5);
        defaultCriteria.setDefinitionsWeight(5);
        defaultCriteria.setOrganizationWeight(15);
        defaultCriteria.setManagerialProcessWeight(20);
        defaultCriteria.setTechnicalProcessWeight(20);
        defaultCriteria.setSupportingProcessWeight(15);
        defaultCriteria.setAdditionalPlansWeight(10);
        defaultCriteria.setActive(true);
        return defaultCriteria;
    }

    /**
     * Convert entity to DTO.
     */
    private GradingCriteriaDTO convertToDTO(GradingCriteria criteria) {
        return new GradingCriteriaDTO(
                criteria.getId(),
                criteria.getName(),
                criteria.getDescription(),
                criteria.getCreatedBy().getId(),
                criteria.getCreatedBy().getFirstName() + " " + criteria.getCreatedBy().getLastName(),
                criteria.isActive(),
                criteria.getOverviewWeight(),
                criteria.getReferencesWeight(),
                criteria.getDefinitionsWeight(),
                criteria.getOrganizationWeight(),
                criteria.getManagerialProcessWeight(),
                criteria.getTechnicalProcessWeight(),
                criteria.getSupportingProcessWeight(),
                criteria.getAdditionalPlansWeight(),
                criteria.getCreatedAt() != null ? criteria.getCreatedAt().format(DATE_FORMATTER) : null,
                criteria.getUpdatedAt() != null ? criteria.getUpdatedAt().format(DATE_FORMATTER) : null
        );
    }

    /**
     * Set weights from DTO to entity.
     */
    private void setWeightsFromDTO(GradingCriteria criteria, GradingCriteriaDTO dto) {
        criteria.setOverviewWeight(dto.getOverviewWeight());
        criteria.setReferencesWeight(dto.getReferencesWeight());
        criteria.setDefinitionsWeight(dto.getDefinitionsWeight());
        criteria.setOrganizationWeight(dto.getOrganizationWeight());
        criteria.setManagerialProcessWeight(dto.getManagerialProcessWeight());
        criteria.setTechnicalProcessWeight(dto.getTechnicalProcessWeight());
        criteria.setSupportingProcessWeight(dto.getSupportingProcessWeight());
        criteria.setAdditionalPlansWeight(dto.getAdditionalPlansWeight());
    }
}
