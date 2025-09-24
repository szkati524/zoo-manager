package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    public List<Document> getAllDocuments(){
        return documentRepository.findAll();
    }
    public Document addDocument(Document document){
        return documentRepository.save(document);
    }
    public List<Document> findByEmployeeNameAndEmployeeSurname(String name,String surname){
        return documentRepository.findByEmployeeNameAndEmployeeSurname(name,surname);
    }
    public List<Document> findByEmployeeProfession(String profession){
        return documentRepository.findByEmployeeProfession(profession);
    }
}
