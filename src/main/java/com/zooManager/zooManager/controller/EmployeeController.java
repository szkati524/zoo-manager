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

import java.util.List;

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
    public String addEmployee(@ModelAttribute Employee employee, Model model){
    try{
        if (employee.getAnimalIds() != null && !employee.getAnimalIds().isEmpty()) {
            List<Animal> selectedAnimal = animalService.findAllByIds(employee.getAnimalIds());
            employee.setAnimals(selectedAnimal);

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
