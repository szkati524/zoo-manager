package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.DocumentCategory;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.DocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import static com.zooManager.zooManager.specification.DocumentSpecification.*;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
@Transactional
    public Document addDocument(Document document,Employee employee) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
        document.setEmployee(employee);
        if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))){

        } else if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_VET"))) {
            if (document.getDocumentCategory() != DocumentCategory.MEDICAL) {
                throw new AccessDeniedException("VET może dodawać tylko dokumenty dla weterynarza");
            }
        } else if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_LEADER_SHIFT"))) {
            if (document.getDocumentCategory() != DocumentCategory.SHIFT_REPORT){
                throw new AccessDeniedException("LEADER_SHIFT może dodawać tylko dokumenty dla leaderów");
            }
        } else if (roles.stream().anyMatch(r -> r.getAuthority().equals("ZOOKEEPER"))){
            if (document.getDocumentCategory() != DocumentCategory.EMPLOYEE_INFO){
                throw new AccessDeniedException("ZooKeeper może dodawać tylko dokumenty dla ZooKeeperów");
            } else {
                throw new AccessDeniedException("Brak uprawnień do dodawania dokumentów");
            }

        }
        return documentRepository.save(document);
    }

    public List<Document> searchDocuments(String title, DocumentCategory category, String employeeName, String employeeSurname, LocalDateTime createdAfter) {
        Specification<Document> spec = Specification.where(titleContains(title))
                .and(categoryEquals(category))
                .and(employeeNameContains(employeeName))
                .and(employeeSurnameContains(employeeSurname))
                                .and(createdAfter(createdAfter));
        return documentRepository.findAll(spec);
    }

    @Transactional
    public void deleteDocumentById(Long id) {
      if (!documentRepository.existsById(id)){
          throw new EntityNotFoundException("Document not found");
      }

        documentRepository.deleteById(id);
    }
    public Optional<Document> findById(Long id){
        return documentRepository.findById(id);

    }
    public List<Document> getVisibleDocument(String title,DocumentCategory category,String employeeName,String employeeSurname,LocalDateTime createdAfter){
        Specification<Document> spec = Specification.where(titleContains(title))
                .and(categoryEquals(category))
                .and(employeeNameContains(employeeName))
                .and(employeeSurnameContains(employeeSurname))
                .and(createdAfter(createdAfter));
List<Document> docs = documentRepository.findAll(spec);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();

        return docs.stream()
                .filter(doc -> {
                    if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))){
                        return true;
                    } else if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_LEADER_SHIFT"))){
                        return doc.getDocumentCategory() != null;
                    } else if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_VET"))){
                        return doc.getDocumentCategory() == DocumentCategory.MEDICAL || doc.getDocumentCategory() == DocumentCategory.EMPLOYEE_INFO;
                    }else if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ZOOKEEPER"))) {
                        return doc.getDocumentCategory() == DocumentCategory.EMPLOYEE_INFO;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

}



