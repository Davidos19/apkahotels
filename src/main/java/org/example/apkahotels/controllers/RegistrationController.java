package org.example.apkahotels.controllers;

import jakarta.validation.Valid;
import org.example.apkahotels.models.AppUser;
import org.example.apkahotels.models.RegistrationForm;
import org.example.apkahotels.repositories.AppUserRepository;
import org.example.apkahotels.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final InMemoryUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository userRepo;
    private final UserService userService;    // ← dołóż

    @Autowired
    public RegistrationController(InMemoryUserDetailsManager udm, PasswordEncoder passwordEncoder, AppUserRepository userRepo, UserService userService) {
        this.userDetailsManager  = udm;
        this.passwordEncoder = passwordEncoder;
        this.userRepo           = userRepo;
        this.userService= userService;

    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid RegistrationForm form,
                               BindingResult br, Model model) {
        if (br.hasErrors() || !form.getPassword().equals(form.getConfirmPassword())) {
            if (!form.getPassword().equals(form.getConfirmPassword())) {
                model.addAttribute("error", "Hasła nie są zgodne");
            }
            return "register";
        }
        if (userDetailsManager.userExists(form.getUsername())) {
            model.addAttribute("error", "Użytkownik o tej nazwie już istnieje");
            return "register";
        }

        // 1) Zarejestruj domenowego użytkownika w UserService
        AppUser domain = new AppUser();
        domain.setUsername(form.getUsername());
        domain.setPassword(passwordEncoder.encode(form.getPassword()));
        domain.setEmail(form.getEmail());
        domain.setFirstName(form.getFirstName());
        domain.setLastName(form.getLastName());
        domain.setPhoneNumber(form.getPhoneNumber());
        domain.setProfileImageUrl(null);
        userService.registerUser(domain);           // ← tu zamiast userRepo.save(...)

        // 2) Dodaj w Spring Security
        UserDetails secUser = User.builder()
                .username(domain.getUsername())
                .password(domain.getPassword())
                .roles("USER")
                .build();
        userDetailsManager.createUser(secUser);

        return "redirect:/login?registered=true";
    }
}
