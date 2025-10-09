package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.AnimalRepository;
import com.zooManager.zooManager.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
public List<Animal> searchAnimal(String name,String species,Boolean currentVaccination,String employeeName,String employeeSurname){
        return animalRepository.searchAnimals(name,species,currentVaccination,employeeName,employeeSurname);
}


    public List<Animal> findAllByIds(List<Long> Ids) {
        return animalRepository.findAllById(Ids);
    }
        public Optional<Animal> findById(Long id){
           return animalRepository.findById(id);
        }

    @Transactional
    public void deleteAnimalById(Long id){
        Animal animal = animalRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Animal not found"));
        for (Employee emp : animal.getEmployees()){
            emp.getAnimals().remove(animal);
        }
        animal.getEmployees().clear();
       animalRepository.deleteById(id);
    }
    @Transactional
    public void toggleVaccination(Long id,boolean status){
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        animal.setCurrentVaccination(status);
        animalRepository.save(animal);
    }
    @Transactional
    public void assignEmployeeToAnimal(Long animalId, long employeeId, EmployeeRepository employeeRepository) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        if (!animal.getEmployees().contains(employee)){
            animal.getEmployees().add(employee);
            employee.getAnimals().add(animal);
        }
        animalRepository.save(animal);
    }

}
