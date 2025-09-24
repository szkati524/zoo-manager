package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.DocumentCategory;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.DocumentRepository;
import com.zooManager.zooManager.service.DocumentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController( DocumentService documentService) {
        this.documentService = documentService;
    }



    @GetMapping("/documents")
    public String searchDocuments(@RequestParam(required = false) String title,
                                  @RequestParam(required = false) DocumentCategory category,
                                  @RequestParam(required = false) String employeeName,
                                  @RequestParam(required = false) String employeeSurname,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                  LocalDateTime createdAfter,
                                  Model model){
        List<Document> documents = documentService.searchDocuments(title,category,employeeName,employeeSurname,createdAfter);
        model.addAttribute("documents",documents);
        return "document";

    }
}
