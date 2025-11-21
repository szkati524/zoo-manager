package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.EmployeeRepository;
import com.zooManager.zooManager.service.EmailChangeService;
import com.zooManager.zooManager.service.PasswordChangeService;
import com.zooManager.zooManager.service.SettingsService;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final PasswordChangeService passwordChangeService;
    private final EmailChangeService emailChangeService;

    public SettingsController(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder,  PasswordChangeService passwordChangeService, EmailChangeService emailChangeService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;

        this.passwordChangeService = passwordChangeService;
        this.emailChangeService = emailChangeService;
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
            return "redirect:/settings/change-password";

        }
       passwordChangeService.requestPasswordChange(user,newPassword);
        redirectAttributes.addFlashAttribute("successPassword","Wysłaliśmy link potwierdzający na email!");
        return "redirect:/settings/change-password";
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
       emailChangeService.requestEmailChange(user,newEmail);
        redirectAttributes.addFlashAttribute("successEmail","Wysłano link potwierdzający na nowy email!");
        return "redirect:/settings/change-email";
    }
    @GetMapping("/confirm-password-change")
    public String confirmPasswordChange(@RequestParam String token,RedirectAttributes redirectAttributes){
        boolean success = passwordChangeService.confirmPasswordChange(token);
        if (!success){
            redirectAttributes.addFlashAttribute("errorPassword","Token wygasł lub jest nieprawidłowy!");
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("successPassword","Hasło zostało zmienione! zaloguj się używając nowego hasła.");
        return "redirect:/login";
    }
    @GetMapping("/confirm-email-change")
    public String confirmEmailChange(@RequestParam String token,RedirectAttributes redirectAttributes){
        boolean success = emailChangeService.confirmEmailChange(token);
        if (!success){
            redirectAttributes.addFlashAttribute("errorEmail","Token wygasł lub jest nieprawidłowy");
        } else {
            redirectAttributes.addFlashAttribute("successEmail", "Email został zmieniony pomyślnie!");
        }
        return "redirect:/settings";
    }

}
