package com.team02.spmpevaluator.service;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.SPMPDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SPMPDocumentService {
    @Autowired private SPMPDocumentRepository repo;
    public SPMPDocument upload(String filename, String fileUrl, User uploader) {
        SPMPDocument doc = new SPMPDocument();
        doc.setFileName(filename);
        doc.setFileUrl(fileUrl);
        doc.setUploadedBy(uploader);
        return repo.save(doc);
    }
}
