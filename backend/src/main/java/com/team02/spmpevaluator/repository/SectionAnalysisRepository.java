package com.team02.spmpevaluator.repository;

import com.team02.spmpevaluator.entity.SectionAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionAnalysisRepository extends JpaRepository<SectionAnalysis, Long> {
    List<SectionAnalysis> findByComplianceScoreId(Long complianceScoreId);
}
