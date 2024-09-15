package com.testspring.testspring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    // Tableau de bord de l'utilisateur
    @GetMapping("/user/tableau-de-bord")
    public String user() {
        return "private/user/apps-tasks-list-view";
    }

    // Affiche le profile de l'utilisateur
    @GetMapping("/user/mon-profile")
    public String monProfile() {
        return "private/user/pages-profile";
    }

    // Affiche la page de modification de l'utilisateur
    @GetMapping("/user/modifier-mon-profile")
    public String ModifierMonProfile() {
        return "private/user/pages-profile-settings";
    }

    // Affiche la page de detail d'un utilisateur
    @GetMapping("/user/detail-utilisateur")
    public String detailUtilisateur() {
        return "private/user/apps-tasks-details";
    }

}
