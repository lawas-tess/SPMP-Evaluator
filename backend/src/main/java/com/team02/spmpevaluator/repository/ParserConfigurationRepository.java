package com.team02.spmpevaluator.repository;

import com.team02.spmpevaluator.entity.ParserConfiguration;
import com.team02.spmpevaluator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParserConfigurationRepository extends JpaRepository<ParserConfiguration, Long> {
    
    List<ParserConfiguration> findByCreatedBy(User user);
    
    List<ParserConfiguration> findByIsActiveTrue();
    
    Optional<ParserConfiguration> findByIsDefaultTrue();
    
    List<ParserConfiguration> findByCreatedByOrderByCreatedAtDesc(User user);
}
