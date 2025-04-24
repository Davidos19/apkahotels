package org.example.apkahotels.controllers;
import org.springframework.ui.Model;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(
            @RequestParam(value="error", required=false) String error,
            @RequestParam(value="logout", required=false) String logout,
            Model model) {
        if (logout != null)  model.addAttribute("message", "Pomyślnie wylogowano");
        return "login";  // Twoje login.html
    }
}

