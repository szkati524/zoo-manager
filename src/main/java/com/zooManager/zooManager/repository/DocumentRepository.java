package com.zooManager.zooManager.repository;

import com.zooManager.zooManager.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Long> {
List<Document> findByEmployeeNameAndEmployeeSurname(String name, String surname);
List<Document> findByEmployeeProfession(String profession);

}
