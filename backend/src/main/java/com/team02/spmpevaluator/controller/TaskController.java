package com.team02.spmpevaluator.controller;
import com.team02.spmpevaluator.entity.Task;
import com.team02.spmpevaluator.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired private TaskService service;
    @GetMapping("/my")
    public List<Task> getMyTasks(@RequestParam Long userId) {
        return service.getTasksByUser(userId);
    }
}
