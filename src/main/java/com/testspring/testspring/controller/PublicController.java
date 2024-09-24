package com.testspring.testspring.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.testspring.testspring.service.CustomUserDetails;

@Controller
public class PublicController {

    @GetMapping("/")
    public String home(Model model) {

        // Récupère les informations de l'utilisateur authentifier
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            String nom;
            String image;
            String role;
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                nom = userDetails.getNom(); // Récupérer le nom
                image = userDetails.getImage(); // Récupérer l'image
                role = userDetails.getRole(); // Récupérer le role
            } else {
                nom = "Inconnu";
                image = "Inconnu";
                role = "Inconnu";
            }

            model.addAttribute("nom", nom);
            model.addAttribute("image", image);
            model.addAttribute("role", role);
        }

        return "public/index";
    }

}
