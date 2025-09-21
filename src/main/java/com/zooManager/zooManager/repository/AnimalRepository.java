package com.zooManager.zooManager.repository;

import com.zooManager.zooManager.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AnimalRepository extends JpaRepository<Animal,Long> {

@Query("""
        SELECT DISTINCT a FROM Animal a
        LEFT JOIN a.employees e
        WHERE (:name IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:species IS NULL OR LOWER(a.species) LIKE LOWER(CONCAT('%',:species,'%')))
        AND (:currentVaccination IS NULL OR a.currentVaccination = :currentVaccination)
        AND (:employeeName IS NULL OR LOWER(e.name) LIKE LOWER (CONCAT('%',:employeeName,'%')))
        AND (:employeeName IS NULL OR LOWER(e.surname) LIKE LOWER (CONCAT('%',:employeeSurname,'%')))
        """)
    List<Animal> searchAnimals(
            @Param("name")String name,
            @Param("species") String species,
            @Param("currentVaccination")Boolean currentVaccination,
            @Param("employeeMame") String employeeName,
            @Param("employeeSurname") String employeeSurname
);

}
