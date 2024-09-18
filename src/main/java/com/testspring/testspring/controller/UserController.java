package com.testspring.testspring.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    // Affiche la page de detail d'un utilisateur
    @RequestMapping("/user/detail-utilisateur-{id}")
    public String detailUtilisateur(@PathVariable("id") int id, Model model) {

        // Affiche les détails d'un utilisateur à partir de l'id
        AppUser appUser = service.getAppUserById(id);
        model.addAttribute("user", appUser);

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

    // Affiche le profile de l'utilisateur
    @GetMapping("/user/mon-profile")
    public String monProfile(Model model) {

        // Récupère les informations de l'utilisateur authentifier
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            String nom;
            String prenom;
            String image;
            String age;
            String sexe;
            String numero;
            String role;
            String email;
            Date createdAt;
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                nom = userDetails.getNom(); // Récupérer le nom
                prenom = userDetails.getPrenom(); // Récupérer le prénom
                image = userDetails.getImage(); // Récupérer l'image
                age = userDetails.getAge(); // Récupérer l'âge
                sexe = userDetails.getSexe(); // Récupérer le sexe
                numero = userDetails.getNumero(); // Récupérer le numéro
                role = userDetails.getRole(); // Récupérer le role
                email = userDetails.getUsername(); // Récupérer l'email
                createdAt = userDetails.getCreatedAt(); // Récupérer le nom
            } else {
                nom = "Inconnu";
                prenom = "Inconnu";
                image = "Inconnu";
                age = "Inconnu";
                sexe = "Inconnu";
                numero = "Inconnu";
                role = "Inconnu";
                email = "Inconnu";
                createdAt = new Date(); // Valeur par défaut
            }

            model.addAttribute("nom", nom);
            model.addAttribute("prenom", prenom);
            model.addAttribute("image", image);
            model.addAttribute("age", age);
            model.addAttribute("sexe", sexe);
            model.addAttribute("numero", numero);
            model.addAttribute("role", role);
            model.addAttribute("email", email);
            model.addAttribute("createdAt", createdAt);
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

}
