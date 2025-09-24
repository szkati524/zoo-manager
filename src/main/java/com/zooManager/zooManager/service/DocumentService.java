package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.DocumentCategory;
import com.zooManager.zooManager.repository.DocumentRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.zooManager.zooManager.specification.DocumentSpecification.*;

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
    public List<Document> searchDocuments(String title, DocumentCategory category, String employeeName, String employeeSurname, LocalDateTime createdAfter) {
        Specification<Document> spec = Specification.where(titleContains(title))
                .and(categoryEquals(category))
                .and(employeeNameContains(employeeName)
                        .and(employeeSurnameContains(employeeSurname)
                                .and(createdAfter(createdAfter))));
       return documentRepository.findAll(spec);
    }


}
