package com.testspring.testspring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.testspring.testspring.model.AppUser;
import com.testspring.testspring.service.AppUserService;
import com.testspring.testspring.service.CustomUserDetails;

@Controller
public class UserController {

    @Autowired
    private AppUserService service;

    // Tableau de bord de l'utilisateur authentifier
    @GetMapping("/user/tableau-de-bord")
    public ModelAndView user(Model model) {

        // Permet de récupérer tous les utilisateurs
        List<AppUser> list = service.getAllAppUsers();

        // Récupère les informations de l'utilisateur authentifier
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            String nom;
            String image;
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                nom = userDetails.getNom(); // Récupérer le nom
                image = userDetails.getImage(); // Récupérer l'image
            } else {
                nom = "Inconnu";
                image = "Inconnu";
            }

            model.addAttribute("nom", nom);
            model.addAttribute("image", image);
        }
        return new ModelAndView("private/user/apps-tasks-list-view", "users", list);
    }

    // Affiche le profile de l'utilisateur
    @GetMapping("/user/mon-profile")
    public String monProfile(Model model) {

        // Récupère les informations de l'utilisateur authentifier
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            String nom;
            String image;
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                nom = userDetails.getNom(); // Récupérer le nom
                image = userDetails.getImage(); // Récupérer l'image
            } else {
                nom = "Inconnu";
                image = "Inconnu";
            }

            model.addAttribute("nom", nom);
            model.addAttribute("image", image);
        }
        return "private/user/pages-profile";
    }

    // Affiche la page de modification de l'utilisateur
    @GetMapping("/user/modifier-mon-profile")
    public String ModifierMonProfile(Model model) {

        // Récupère les informations de l'utilisateur authentifier
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            String nom;
            String image;
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                nom = userDetails.getNom(); // Récupérer le nom
                image = userDetails.getImage(); // Récupérer l'image
            } else {
                nom = "Inconnu";
                image = "Inconnu";
            }

            model.addAttribute("nom", nom);
            model.addAttribute("image", image);
        }
        return "private/user/pages-profile-settings";
    }

    // Affiche la page de detail d'un utilisateur
    @GetMapping("/user/detail-utilisateur")
    public String detailUtilisateur(Model model) {

        // Récupère les informations de l'utilisateur authentifier
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            String nom;
            String image;
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                nom = userDetails.getNom(); // Récupérer le nom
                image = userDetails.getImage(); // Récupérer l'image
            } else {
                nom = "Inconnu";
                image = "Inconnu";
            }

            model.addAttribute("nom", nom);
            model.addAttribute("image", image);
        }
        return "private/user/apps-tasks-details";
    }

}
