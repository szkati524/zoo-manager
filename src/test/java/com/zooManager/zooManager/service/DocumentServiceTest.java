package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.DocumentCategory;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.DocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private DocumentService documentService;

    private Document testDocument;
    private Document testDocument2;
    private Employee testEmployee;
    @BeforeEach
    void setUp(){
        Employee testEmployee = new Employee(10L,"Anna","Nowak");
        testDocument = new Document(1L,"Choroba Lwa","treść...", DocumentCategory.EMPLOYEE_INFO,testEmployee, LocalDateTime.now().minusDays(10));
        testDocument2 = new Document(2L,"Dziwne zachowanie","tresc...",DocumentCategory.MEDICAL,testEmployee,LocalDateTime.now().minusDays(5));
        testEmployee.setUsername("anna");
        testEmployee.setPassword("1234");

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
    @Test
    @WithMockUser(roles = "ADMIN")
            void addDocument_ShouldSaveAndReturnDocument(){
        Authentication auth = new UsernamePasswordAuthenticationToken(testEmployee,null,List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        Document result = documentService.addDocument(testDocument,testEmployee);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L );
        verify(documentRepository,times(1)).save(any(Document.class));
        SecurityContextHolder.clearContext();
    }
    @Test
    void findById_ShouldReturnDocument_WhenFound(){
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        Optional<Document> result = documentService.findById(1L);
        assertThat(result.isPresent());
        assertThat(result.get().getTitle()).isEqualTo("Choroba Lwa");
        verify(documentRepository,times(1)).findById(1L );

    }
    @Test
    @WithMockUser(roles = "LEADER_SHIFT")
    void findById_ShouldReturnEmptyOptional_WhenNotFound(){
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Document> result = documentService.findById(99L);
        assertFalse(result.isPresent());
        verify(documentRepository,times(1)).findById(99L);
    }
    @Test
    void searchDocument_ShouldCallRepositoryWithSpecification(){
        List<Document> expectedDocuments = Arrays.asList(testDocument);
        when(documentRepository.findAll(any(Specification.class))).thenReturn(expectedDocuments);

        List<Document> result = documentService.searchDocuments("Choroba Lwa",
                DocumentCategory.EMPLOYEE_INFO,"Anna","Nowak",LocalDateTime.now()
        );
        assertThat(result).hasSize(1);
        verify(documentRepository,times(1)).findAll(any(Specification.class));
    }
    @Test
    void searchDocuments_NoFilters_ShouldCallRepositoryWithSpecification(){
        when(documentRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(testDocument,testDocument2));
        List<Document> result = documentService.searchDocuments(
                null,null,null,null,null
        );
        assertThat(result).hasSize(2);
        verify(documentRepository,times(1)).findAll(any(Specification.class     ));
    }
    @Test
    void deleteDocumentById_ShouldDeleteDocument_WhenExist(){
        Long idToDelete = 1L;
        when(documentRepository.existsById(idToDelete)).thenReturn(true);
        documentService.deleteDocumentById(idToDelete);
        verify(documentRepository,times(1)).existsById(idToDelete);
        verify(documentRepository,times(1)).deleteById(idToDelete);

    }
    @Test
    void deleteDocumentById_ShouldThrowException_WhenNotExists(){
        Long idToDelete = 99L;
        when(documentRepository.existsById(idToDelete)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> {
                    documentService.deleteDocumentById(idToDelete);
                },"powinnien rzucić EntityNotFoundException");

        verify(documentRepository,times(1)).existsById(idToDelete);
        verify(documentRepository,never()).deleteById(anyLong());
    }


}
