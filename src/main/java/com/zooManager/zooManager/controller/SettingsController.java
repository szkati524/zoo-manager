package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.EmployeeRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/settings")
public class SettingsController {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public SettingsController(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/change-password")
    public String changePasswordPage(){
        return "change-password";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/change-email")
    public String changeEmailPage(){
        return "change-email";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public String settingsPage(Model model, Principal principal){
        Employee user = employeeRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user",user);
        return "settings";

    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, Principal principal, RedirectAttributes redirectAttributes){
        Employee user = employeeRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(oldPassword,user.getPassword())){
            redirectAttributes.addFlashAttribute("errorPassword","Stare hasło jest nieprawidłowe");
            return "redirect:/settings";

        }
        user.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(user);
        redirectAttributes.addFlashAttribute("successPassword","Hasło zmienione pomyślnie!");
        return "redirect:/settings";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-email")
    public String changeEmail(@RequestParam String newEmail,@RequestParam String passwordConfirmation,Principal principal,RedirectAttributes redirectAttributes){
        Employee user = employeeRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(passwordConfirmation,user.getPassword())){
            redirectAttributes.addFlashAttribute("errorEmail","Hasło nieprawidłowe!");
            return "redirect:/settings";

        }
        user.setEmail(newEmail);
        employeeRepository.save(user);
        redirectAttributes.addFlashAttribute("successEmail","Email zostal zmieniony pomyślnie!");
        return "redirect:/settings";
    }

}
