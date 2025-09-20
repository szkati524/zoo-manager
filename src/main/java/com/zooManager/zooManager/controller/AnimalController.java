package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public String showAddAnimal(Model model){
    model.addAttribute("animal",new Animal());
    return "add-animal";
    }
    @PostMapping("/add-animal")
    public String addAnimal(@ModelAttribute Animal animal, Model model){
    try{
        animalService.addAnimal(animal);
        model.addAttribute("success",true);
    } catch (Exception e){
        model.addAttribute("error",false);

    }
        model.addAttribute("animal",new Animal());
   return "add-animal";
    }
    @GetMapping("/animals")
    public String showAnimals(Model model,Animal animal){
    List<Animal> animalList = animalService.getAllAnimals();
    model.addAttribute("animals",animalList);
    model.addAttribute("animal",new Animal());
    return "animals";
    }
}
