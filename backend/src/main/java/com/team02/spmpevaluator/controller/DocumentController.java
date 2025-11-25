package com.team02.spmpevaluator.controller;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.SPMPDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired private SPMPDocumentService service;
    @PostMapping("/upload")
    public SPMPDocument upload(@RequestParam String fileName, @RequestParam String fileUrl, @RequestParam Long userId) {
        User user = new User();
        user.setId(userId);
        return service.upload(fileName, fileUrl, user);
    }
}
