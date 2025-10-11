package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.repository.EmployeeRepository;
import com.zooManager.zooManager.service.AnimalService;
import com.zooManager.zooManager.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class AnimalController {
    private final static Logger log = LoggerFactory.getLogger(AnimalController.class);

    private final AnimalService animalService;

    private final EmployeeService employeeService;
    @Autowired
    public AnimalController(AnimalService animalService, EmployeeService employeeService) {
        this.animalService = animalService;

        this.employeeService = employeeService;
    }

    @GetMapping("/add-animal")
    public String showAddAnimal(Model model) {
        model.addAttribute("animal", new Animal());
        return "add-animal";
    }

    @PostMapping("/add-animal")
    public String addAnimal(@ModelAttribute Animal animal, @RequestParam("image") MultipartFile image, Model model) {
        try{

            animalService.addAnimalWithImage(animal,image);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", true);
            log.error("Błąd podczas dodawnia zwierzęcia",e);
        }

        model.addAttribute("animal", new Animal());
        return "add-animal";
    }



    @GetMapping("/animals")
    public String searchAnimals(Model model, @RequestParam(required = false) String name,
                                @RequestParam(required = false) String species,
                                @RequestParam(required = false) Boolean currentVaccination,
                                @RequestParam(required = false) String employeeName,
                                @RequestParam(required = false) String employeeSurname) {
        List<Animal> animals;
        boolean isFilterEmpty = isBlank(name) &&
                isBlank(species) &&
                currentVaccination == null &&
                isBlank(employeeName) &&
                isBlank(employeeSurname);
        if (isFilterEmpty) {
            animals = animalService.getAllAnimals();
        } else {

            animals = animalService.searchAnimal(
                    isBlank(name) ? null : name,
                    isBlank(species) ? null : species,
                    currentVaccination,
                    isBlank(employeeName) ? null : employeeName,
                    isBlank(employeeSurname) ? null : employeeSurname
            );
        }
            model.addAttribute("animals", animals);
            model.addAttribute("name", name);
            model.addAttribute("species", species);
            model.addAttribute("currentVaccination", currentVaccination);
            model.addAttribute("employeeName", employeeName);
            model.addAttribute("employeeSurname", employeeSurname);
            model.addAttribute("allEmployees",employeeService.getAllEmployees());
            return "animals";
        }
        @PostMapping("/animals/delete/{id}")
    public String deleteAnimalsById(@PathVariable Long id){
        animalService.deleteAnimalById(id);
        return "redirect:/animals";

        }
        @PostMapping("/animals/vaccination/{id}")
    public String setVaccinationAnimal(@PathVariable Long id,@RequestParam boolean status){
        animalService.toggleVaccination(id,status);
        return "redirect:/animals";
        }
        @PostMapping("/animals/assign/{animalId}")
    public String assignEmployeeToAnimal(@PathVariable Long animalId,@RequestParam Long employeeId){
        animalService.assignEmployeeToAnimal(animalId,employeeId);
        return "redirect:/animals";
        }
        @GetMapping("/animals/{id}")
    public String viewAnimal(@PathVariable Long id,Model model){
        Animal animal = animalService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Animal not found"));
        model.addAttribute("animal",animal);
        return "animal-details";
        }
        private boolean isBlank(String s) {
        return s == null || s.isBlank();
        }
    }



