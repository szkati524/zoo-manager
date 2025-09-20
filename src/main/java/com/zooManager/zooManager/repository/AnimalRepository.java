package com.zooManager.zooManager.repository;

import com.zooManager.zooManager.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AnimalRepository extends JpaRepository<Animal,Long> {

public Animal findByName(String name);
public List<Animal> findBySpecies(String species);
}
