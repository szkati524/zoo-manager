package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.service.AnimalService;
import com.zooManager.zooManager.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;
    private final AnimalService animalService;
@Autowired
    public EmployeeController(EmployeeService employeeService, AnimalService animalService) {
    this.employeeService = employeeService;
    this.animalService = animalService;
}

    @GetMapping("employee")
            public String showEmployees( Model model){
   List<Employee> employees =  employeeService.getAllEmployees();
   model.addAttribute("employees",employees);
   return "employee";
}
    @GetMapping("add-employee")
    public String showAddEmployee(Model model){
    model.addAttribute("employee",new Employee());
    model.addAttribute("allAnimals",animalService.getAllAnimals());
    return "add-employee";
    }
    @PostMapping("add-employee")
    public String addEmployee(@ModelAttribute Employee employee, @RequestParam("image") MultipartFile image, Model model){
    try{
        if (!image.isEmpty()) {


            Path uploadPath = Paths.get(System.getProperty("user.dir"),"uploads");
            if (!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            image.transferTo(filePath.toFile());
            employee.setImagePath(fileName);

    }

        if (employee.getAnimalIds() != null && !employee.getAnimalIds().isEmpty()) {
            List<Animal> selectedAnimal = animalService.findAllByIds(employee.getAnimalIds());
            employee.setAnimals(selectedAnimal);
for (Animal animal : selectedAnimal){
    animal.getEmployees().add(employee);
}
        }

        employeeService.addEmployee(employee);
        model.addAttribute("success",true);
    } catch (Exception e){
        model.addAttribute("error",false);

    }
    model.addAttribute("employee",new Employee());
    model.addAttribute("allAnimals",animalService.getAllAnimals());
    return "add-employee";

    }
    @PostMapping("employee/delete/{id}")
    public String deleteEmployeeById(@PathVariable Long id){
    employeeService.deleteEmployeeById(id);
    return "redirect:/employee";
    }

}
