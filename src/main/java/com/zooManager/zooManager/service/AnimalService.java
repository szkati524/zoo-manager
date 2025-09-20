package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }


    public Animal addAnimal(Animal animal){
   return animalRepository.save(animal);
    }

    public List<Animal> getAllAnimals(){
        return animalRepository.findAll();
    }
    public Animal getAnimalByName(String name){
        return animalRepository.findByName(name);
    }
    public List<Animal> findBySpecies(String species){
        return animalRepository.findBySpecies(species);
    }
}
