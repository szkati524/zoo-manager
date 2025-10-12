package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.DocumentCategory;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.DocumentRepository;
import com.zooManager.zooManager.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Controller
public class DocumentController {
    private final static Logger log = LoggerFactory.getLogger(DocumentController.class);
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
    @GetMapping("add-document")
    public String showAddDocument(Model model){
        model.addAttribute("document",new Document());
        return "add-document";
    }
    @PostMapping("add-document")
    public String addDocument(@ModelAttribute Document document,Model model){
        try{
            documentService.addDocument(document);
            model.addAttribute("success",true);
        } catch (Exception e){
            model.addAttribute("error",true);
            log.error("Błąd podczas dodawania dokumentu",e);
        }
        model.addAttribute("document",new Document());
        return "add-document";
    }
    @PostMapping("/documents/delete/{id}")
    public String deleteDocument(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try{
            documentService.deleteDocumentById(id);
            redirectAttributes.addFlashAttribute("message","Document deleted successfully!");
        } catch (EntityNotFoundException e){
            redirectAttributes.addFlashAttribute("error","Document not found!");
        }

        return "redirect:/documents";

    }
    @GetMapping("/documents/{id}")
    public String viewDocument(@PathVariable Long id,Model model){
        Document document = documentService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Document not found"));
        model.addAttribute("document",document);
        return "document-details";


    }

}
