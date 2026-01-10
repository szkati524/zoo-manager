package com.zooManager.zooManager.repository;

import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.DocumentCategory;
import com.zooManager.zooManager.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class DocumentRepositoryTest {
@Autowired
    DocumentRepository documentRepository;
@Autowired
    EmployeeRepository employeeRepository;

private Employee anna;
private Employee marek;
private Document docAnnaAnimal;
private Document docMarekAnimal;
private Document docMarekMedical;

@BeforeEach
    void setUp(){
    documentRepository.deleteAll();
    employeeRepository.deleteAll();
    anna = new Employee(null,"Anna","Nowak" );
    marek = new Employee(null,"Marek","Nowakowski");
    anna.setProfession("Opiekun");
    marek.setProfession("Weterynarz");
    anna.setUsername("anna");
    anna.setPassword("1234");
    marek.setUsername("marek");
    marek.setPassword("1234");
    employeeRepository.save(anna);
    employeeRepository.save(marek);
    docAnnaAnimal = new Document(
            null,"Raport","tresc...", DocumentCategory.EMPLOYEE_INFO,anna, LocalDateTime.now().minusDays(10)
    );
    docMarekMedical = new Document(null,"Raport Med","tresc...",DocumentCategory.MEDICAL,marek,LocalDateTime.now().minusDays(5));
  docMarekAnimal = new Document(null,"MarekRaport","tresc...",DocumentCategory.EMPLOYEE_INFO,marek,LocalDateTime.now().minusDays(1));

  docAnnaAnimal = documentRepository.save(docAnnaAnimal);
  docMarekAnimal = documentRepository.save(docMarekAnimal);
  docMarekMedical = documentRepository.save(docMarekMedical);

}
@Test
    void save_ShouldPersistDocument(){
    Document newDoc = new Document(null, "Test Doc", "C", DocumentCategory.EMPLOYEE_INFO, anna, LocalDateTime.now());
    Document savedDoc = documentRepository.save(newDoc);
    assertThat(savedDoc).isNotNull();
    assertThat(savedDoc.getId()).isNotNull();
    assertThat(savedDoc.getEmployee().getName()).isEqualTo("Anna");

}
@Test
    void findById_ShouldFindSavedDocument(){
    Optional<Document> found = documentRepository.findById(docAnnaAnimal.getId());
    assertThat(found.isPresent()    );
    assertThat(found.get().getTitle()).isEqualTo("Raport");
    assertThat(found.get().getEmployee()).isEqualTo(anna);
}
@Test
    void findByEmployeeNameAndEmployeeSurname_ShouldReturnCorrectDocument(){
    List<Document> result = documentRepository.findByEmployeeNameAndEmployeeSurname("Anna","Nowak");
    assertThat(result).hasSize(1);
    assertThat(result).extracting(Document::getTitle).containsExactlyInAnyOrder("Raport");

}
@Test
    void findByEmployeeNameAndSurname_ShouldReturnEmptyList_WhenNotFound(){
    List<Document> result = documentRepository.findByEmployeeNameAndEmployeeSurname("Nie","Istnieje");
    assertThat(result).isEmpty();
}
@Test
    void findByEmployeeProfession_ShouldReturnCorrectDocuments(){
    List<Document> result = documentRepository.findByEmployeeProfession("Weterynarz");
    assertThat(result).hasSize(2);
    assertThat(result).extracting(Document::getTitle).containsExactlyInAnyOrder("Raport Med","MarekRaport");
}
@Test
    void findByEmployeeProfession_ShouldReturnEmptyList_WhenNotFound(){
    List<Document> result = documentRepository.findByEmployeeProfession("Nie ma takiej");
    assertThat(result).isEmpty();
}

    @Test
    void findAll_ShouldReturnAllDocuments(){

        List<Document> result = documentRepository.findAll();
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Document::getTitle).containsExactlyInAnyOrder("Raport","MarekRaport","Raport Med");

    }

    @Test
    void deleteById_ShouldRemoveDocument(){
        Long id = docAnnaAnimal.getId();
       documentRepository.deleteById(id);
        Optional<Document> deletedDocument = documentRepository.findById(id);
        assertThat(deletedDocument).isNotPresent();
        assertThat(documentRepository.count()).isEqualTo(2);
    }


}



