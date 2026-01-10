package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.DocumentCategory;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.EmployeeRepository;
import com.zooManager.zooManager.service.DocumentService;
import com.zooManager.zooManager.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(DocumentController.class)
@WithMockUser(roles = "ADMIN")
public class DocumentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    private Document testDocument;
    private Employee testEmployee;

    @BeforeEach
    void setUp(){
        testEmployee = new Employee(1L,"Anna","Nowak");
        testEmployee.setUsername("anna");
        testEmployee.setPassword("1234");
        testDocument = new Document(1L,"Raport","tekst...", DocumentCategory.MEDICAL,testEmployee, LocalDateTime.of(2025,1,15,10,0));

    }

    @Test
    void searchDocuments_ShouldCallServiceAndReturnDocumentView() throws Exception{
        List<Document> documents = Collections.singletonList(testDocument);
        when(employeeRepository.findByUsername("anna")).thenReturn(Optional.of(testEmployee));
        when(documentService.getVisibleDocument(
                any(), any(), any(), any(), any()
        )).thenReturn(documents);
        mockMvc.perform(get("/documents"))
                .andExpect(status().isOk())
                .andExpect(view().name("document"))
                .andExpect(model().attribute("documents",hasSize(1)));
        verify(documentService,times(1)).getVisibleDocument(
                any(),any(),any(),any(),any()
        );
    }
    @Test
    void searchDocuments_WithFilters_ShouldCallServiceWithParameters() throws Exception{
        LocalDateTime filterDate = LocalDateTime.of(2025,1,1,0,0);
        when(documentService.getVisibleDocument(
                eq("Raport"),eq(DocumentCategory.MEDICAL),eq("Anna"),eq("Nowak"),any()
        )).thenReturn(Collections.singletonList(testDocument));
        mockMvc.perform(get("/documents")
                        .with(csrf())
                .param("title","Raport")
                .param("category","MEDICAL")
                .param("employeeName","Anna")
                .param("employeeSurname","Nowak")
                .param("createdAfter",filterDate.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("document"))
                .andExpect(model().attribute("documents",hasSize(1)));
        verify(documentService,times(1)).getVisibleDocument( eq("Raport"), eq(DocumentCategory.MEDICAL), eq("Anna"), eq("Nowak"), any());


    }
    @Test
    void showAddDocument_ShouldReturnAddDocumentView() throws Exception{
        mockMvc.perform(get("/add-document")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-document"))
                .andExpect(model().attributeExists("document"))
                .andExpect(model().attribute("document",is(instanceOf(Document.class))));
    }
    @Test
    void viewDocument_ShouldReturnDetailsView_WhenFound() throws Exception{
        when(documentService.findById(1L)).thenReturn(Optional.of(testDocument));
        mockMvc.perform(get("/documents/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("document-details"))
                .andExpect(model().attribute("document",testDocument)   );
    }
    @Test
    void viewDocument_NotFound_ShouldReturn404()throws Exception{
        when(documentService.findById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/documents/99")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException));
    }
    @Test
    @WithMockUser(username = "anna")
    void addDocument_Success_ShouldCallServiceAndShowSuccess() throws Exception {
        Document newDocument = new Document(null,"Test Doc","C",DocumentCategory.MEDICAL,testEmployee,LocalDateTime.now());
        when(employeeRepository.findByUsername("anna")).thenReturn(Optional.of(testEmployee));
        when(documentService.addDocument(testDocument,testEmployee)).thenReturn(testDocument);


        mockMvc.perform(post("/add-document")
                .flashAttr("document",newDocument)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-document"))
                .andExpect(model().attribute("success",true));
        verify(documentService,times(1)).addDocument(newDocument,testEmployee);




    }
    @Test
    @WithMockUser(username = "anna")
    void addDocument_Failure_ShouldShowError() throws Exception{
        Document newDocument = new Document(null,"Test doc","C",DocumentCategory.MEDICAL,testEmployee,LocalDateTime.now());
        when(employeeRepository.findByUsername("anna")).thenReturn(Optional.of(testEmployee));
        doThrow(new RuntimeException("DB error")).when(documentService).addDocument(any(Document.class),any(Employee.class));
        mockMvc.perform(post("/add-document")
                .flashAttr("document",newDocument)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-document"))
                .andExpect(model().attribute("error",true));
        verify(documentService,times(1)).addDocument(any(Document.class),any(Employee.class));

    }
    @Test
    void deleteDocument_Success_ShouldRedirectAndShowMessage() throws Exception{
        Long idToDelete = 1L;
        mockMvc.perform(post("/documents/delete/{id}",idToDelete)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents"))
                .andExpect(flash().attribute("message","Document deleted successfully!"));
        verify(documentService,times(1)).deleteDocumentById(idToDelete);

    }
    @Test
    void deleteDocument_NotFound_ShouldRedirectAndShowError()throws Exception{
        Long idToDelete = 99L;
        doThrow(new EntityNotFoundException()).when(documentService).deleteDocumentById(idToDelete);
        mockMvc.perform(post("/documents/delete/{id}",idToDelete)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/documents"))
                .andExpect(flash().attribute("error","Document not found!"));
        verify(documentService,times(1)).deleteDocumentById(idToDelete);
    }

}
