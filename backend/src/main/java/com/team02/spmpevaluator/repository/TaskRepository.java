package com.team02.spmpevaluator.repository;
import com.team02.spmpevaluator.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo_Id(Long userId);
}
