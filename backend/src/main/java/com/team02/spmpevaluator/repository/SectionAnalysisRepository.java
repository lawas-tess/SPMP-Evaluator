package com.team02.spmpevaluator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.team02.spmpevaluator.entity.SectionAnalysis;

@Repository
public interface SectionAnalysisRepository extends JpaRepository<SectionAnalysis, Long> {
    List<SectionAnalysis> findByComplianceScoreId(Long complianceScoreId);
    
    @Modifying
    void deleteByComplianceScoreId(Long complianceScoreId);
}
