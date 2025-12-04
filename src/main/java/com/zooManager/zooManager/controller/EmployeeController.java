package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.configuration.Roles;
import com.zooManager.zooManager.service.AnimalService;
import com.zooManager.zooManager.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Controller
public class EmployeeController {
    private final static Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;
    private final AnimalService animalService;
    private final PasswordEncoder passwordEncoder;
@Autowired
    public EmployeeController(EmployeeService employeeService, AnimalService animalService, PasswordEncoder passwordEncoder) {
    this.employeeService = employeeService;
    this.animalService = animalService;
    this.passwordEncoder = passwordEncoder;
}
@PreAuthorize("isAuthenticated()")
    @GetMapping("employee")
            public String showEmployees( Model model){
   List<Employee> employees =  employeeService.getAllEmployees();
   model.addAttribute("employees",employees);
   return "employee";
}
    @PreAuthorize("hasAnyRole(T(com.zooManager.zooManager.configuration.Roles).ADMIN,T(com.zooManager.zooManager.configuration.Roles).LEADER_SHIFT)")
    @GetMapping("add-employee")
    public String showAddEmployee(Model model){
    model.addAttribute("employee",new Employee());
    model.addAttribute("allAnimals",animalService.getAllAnimals());
    model.addAttribute("roles", Roles.values());
    return "add-employee";
    }
    @PreAuthorize("hasAnyRole(T(com.zooManager.zooManager.configuration.Roles).ADMIN,T(com.zooManager.zooManager.configuration.Roles).LEADER_SHIFT)")
    @PostMapping("add-employee")
    public String addEmployee(@ModelAttribute Employee employee, @RequestParam("image") MultipartFile image, Model model){
    try{
        if (employee.getAnimalIds() != null && !employee.getAnimalIds().isEmpty()) {
            List<Animal> selectedAnimal = animalService.findAllByIds(employee.getAnimalIds());
            employee.setAnimals(selectedAnimal);
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            employee.setRole(employee.getProfession());
            for (Animal animal : selectedAnimal){
                animal.getEmployees().add(employee);
            }
        }



          employeeService.addEmployeeWithImage(employee,image);

        model.addAttribute("success",true);
    } catch (Exception e){
        model.addAttribute("error",true);
        log.error("Błąd podczas dodawania pracownika",e);

    }
    model.addAttribute("employee",new Employee());
    model.addAttribute("allAnimals",animalService.getAllAnimals());
    model.addAttribute("roles",List.of(
            Roles.ADMIN,
            Roles.LEADER_SHIFT,
            Roles.VET,
            Roles.ZOOKEEPER
    ));
        log.info("Dodawany pracownik: username={}, password={}, role={}",
                employee.getUsername(), employee.getPassword(), employee.getRole());

    return "add-employee";

    }
    @PreAuthorize("hasAnyRole(T(com.zooManager.zooManager.configuration.Roles).ADMIN,T(com.zooManager.zooManager.configuration.Roles).LEADER_SHIFT)")
    @PostMapping("employee/delete/{id}")
    public String deleteEmployeeById(@PathVariable Long id){
    employeeService.deleteEmployeeById(id);
    return "redirect:/employee";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/employee/{id}")
    public String viewEmployee(@PathVariable Long id,Model model){
    Employee employee = employeeService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee not found"));
    model.addAttribute("employee",employee);
    return "employee-details";
    }

}
