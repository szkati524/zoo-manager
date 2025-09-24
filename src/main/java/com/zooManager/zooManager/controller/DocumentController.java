package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.repository.DocumentRepository;
import com.zooManager.zooManager.service.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController( DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/documents")
    public String getAllDocuments(Model model){
        List<Document> documents = documentService.getAllDocuments();
        model.addAttribute("documents",documents);
        return "document";

    }
}
