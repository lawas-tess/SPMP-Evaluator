package com.team02.spmpevaluator.service;
import com.team02.spmpevaluator.entity.Task;
import com.team02.spmpevaluator.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {
    @Autowired private TaskRepository repo;
    public List<Task> getTasksByUser(Long userId) {
        return repo.findByAssignedTo_Id(userId);
    }
}
