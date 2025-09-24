package com.zooManager.zooManager.specification;

import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.DocumentCategory;
import org.springframework.data.jpa.domain.Specification;

import javax.print.Doc;
import java.time.LocalDateTime;

public class DocumentSpecification {
    public static Specification<Document> titleContains(String title){
        return ((root, query, cb) ->
                title == null ? null :
                cb.like(cb.lower(root.get("title")),"%" + title.toLowerCase() + "%"));
    }

        public static Specification<Document> categoryEquals(DocumentCategory category){
            return ((root, query, cb) ->
                    category == null ? null :
                    cb.equal(root.get("documentCategory"),category));
        }
        public static Specification<Document> employeeNameContains(String name){
        return ((root, query, cb) ->
                name == null ? null :
                cb.like(cb.lower(root.join("employee").get("name")),"%" + name.toLowerCase() + "%"));
    }
    public static Specification<Document> employeeSurnameContains(String surname){
        return ((root, query, cb) ->
                surname == null ? null :
                cb.like(cb.lower(root.join("employee").get("surname")),"%" + surname.toLowerCase() + "%"));
    }
    public static Specification<Document> createdAfter(LocalDateTime date) {
        return ((root, query, cb) ->
                date == null ? null :
                cb.greaterThan(root.get("createdAt"),date));
    }
}
