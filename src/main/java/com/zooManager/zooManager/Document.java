package com.zooManager.zooManager;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
    private String content;
    @Enumerated(EnumType.STRING)
    private DocumentCategory documentCategory;
    @ManyToOne
   private Employee employee;
    @CreationTimestamp
    private LocalDateTime createdAt;

   public Document(){

   }

    public Document(Long id, String title, String content, DocumentCategory documentCategory, Employee employee, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.documentCategory = documentCategory;
        this.employee = employee;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DocumentCategory getDocumentCategory() {
        return documentCategory;
    }

    public void setDocumentCategory(DocumentCategory documentCategory) {
        this.documentCategory = documentCategory;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(id, document.id) && Objects.equals(title, document.title) && Objects.equals(content, document.content) && documentCategory == document.documentCategory && Objects.equals(employee, document.employee) && Objects.equals(createdAt, document.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, documentCategory, employee, createdAt);
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", documentCategory=" + documentCategory +
                ", employee=" + employee +
                ", createdAt=" + createdAt +
                '}';
    }
}
