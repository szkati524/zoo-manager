package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.DocumentCategory;
import com.zooManager.zooManager.repository.DocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public Document addDocument(Document document) {
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

}



