package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.repository.EmployeeRepository;
import com.zooManager.zooManager.service.AnimalService;
import com.zooManager.zooManager.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class AnimalController {

    private final AnimalService animalService;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    @Autowired
    public AnimalController(AnimalService animalService, EmployeeRepository employeeRepository, EmployeeService employeeService) {
        this.animalService = animalService;
        this.employeeRepository = employeeRepository;
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
            if (!image.isEmpty()) {
                Path projectDir = Paths.get(System.getProperty("user.dir"));
                Path uploadPath = projectDir.resolve("uploads");
                if (!Files.exists(uploadPath)){
                    Files.createDirectories(uploadPath);
                }
                String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                image.transferTo(filePath.toFile());
                animal.setImagePath(fileName);
            }
            animalService.addAnimal(animal);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", false);
            e.printStackTrace();
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
        boolean isFilterEmpty = (name == null || name.isBlank() &&
                species == null || species.isBlank() &&
                currentVaccination == null &&
                employeeName == null || employeeName.isBlank() &&
                employeeSurname == null || employeeSurname.isBlank());
        if (isFilterEmpty) {
            animals = animalService.getAllAnimals();
        } else {
            animals = animalService.searchAnimal(
                    name.isBlank() ? null : name,
                    species.isBlank() ? null : species,
                    currentVaccination,
                    employeeName.isBlank() ? null : employeeName,
                    employeeSurname.isBlank() ? null : employeeSurname
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
        animalService.assignEmployeeToAnimal(animalId,employeeId,employeeRepository);
        return "redirect:/animals";
        }
        @GetMapping("/animals/{id}")
    public String viewAnimal(@PathVariable Long id,Model model){
        Animal animal = animalService.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        model.addAttribute("animal",animal);
        return "animal-details";
        }
    }



