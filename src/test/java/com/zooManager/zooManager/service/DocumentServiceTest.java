package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.DocumentCategory;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {
    @Mock
    private DocumentRepository documentRepository;
    @InjectMocks
    private DocumentService documentService;

    private Document testDocument;
    private Document testDocument2;
    @BeforeEach
    void setUp(){
        Employee testEmployee = new Employee(10L,"Anna","Nowak");
        testDocument = new Document(1L,"Choroba Lwa","treść...", DocumentCategory.EMPLOYEE_INFO,testEmployee, LocalDateTime.now().minusDays(10));
        testDocument2 = new Document(2L,"Dziwne zachowanie","tresc...",DocumentCategory.MEDICAL,testEmployee,LocalDateTime.now().minusDays(5));

    }
    @Test
    void getAllDocuments_ShouldReturnAllDocuments(){
        List<Document> expectedDocuments = Arrays.asList(testDocument,testDocument2);
        when(documentRepository.findAll()).thenReturn(expectedDocuments);
        List<Document> result = documentService.getAllDocuments();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Document::getTitle).containsExactlyInAnyOrder("Choroba Lwa","Dziwne zachowanie");
        verify(documentRepository,times(1)).findAll();
    }
}
