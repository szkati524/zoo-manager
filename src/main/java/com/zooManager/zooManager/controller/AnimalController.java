package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AnimalController {

    private final AnimalService animalService;

    @Autowired
    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping("/add-animal")
    public String showAddAnimal(Model model) {
        model.addAttribute("animal", new Animal());
        return "add-animal";
    }

    @PostMapping("/add-animal")
    public String addAnimal(@ModelAttribute Animal animal, Model model) {
        try {
            animalService.addAnimal(animal);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", false);

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
            return "animals";
        }
    }



