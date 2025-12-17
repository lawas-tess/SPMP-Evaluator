package com.team02.spmpevaluator.repository;

import com.team02.spmpevaluator.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo_Id(Long userId);
    List<Task> findByCreatedBy_Id(Long userId);
    List<Task> findByAssignedTo_IdAndCompletedFalse(Long userId);
    List<Task> findByStatus(Task.TaskStatus status);
    List<Task> findByDeadlineBeforeAndCompletedFalse(LocalDate deadline);
    void deleteByAssignedToId(Long userId);
    void deleteByCreatedById(Long userId);
}
