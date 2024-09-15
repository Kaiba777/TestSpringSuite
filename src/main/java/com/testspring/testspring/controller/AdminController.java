package com.testspring.testspring.controller;

import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.testspring.testspring.model.AdminInscriptionDto;
import com.testspring.testspring.model.AppUser;
import com.testspring.testspring.repositorie.AppUserRepository;
import com.testspring.testspring.service.CustomUserDetails;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AdminController {

    @Autowired
    private AppUserRepository repo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Affiche le tableau de bord de l'administrateur authentifier
    @GetMapping("/admin/tableau-de-bord")
    public String admin(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            String nom;
            if (principal instanceof CustomUserDetails) {
                nom = ((CustomUserDetails) principal).getNom(); // Récupérer le nom
            } else {
                nom = "Inconnu";
            }

            model.addAttribute("nom", nom);
        }
        return "private/admin/apps-tasks-list-view";
    }

    // Affiche la page d'ajout d'utilisateur par l'administrateur authentifier
    @GetMapping("/admin/ajout-utilisateur")
    public String ajoutUtilisateur(Model model) {
        AdminInscriptionDto adminInscriptionDto = new AdminInscriptionDto();
        model.addAttribute(adminInscriptionDto);
        return "private/admin/apps-tasks-create";
    }

    // Traite les données de la page ajout utilisateur
    @PostMapping("/admin/ajout-utilisateur")
    public String ajoutUtilisateur(Model model, @Valid @ModelAttribute AdminInscriptionDto adminInscriptionDto,
            BindingResult result, @RequestParam("image") MultipartFile file) {

        // Vérifie si le password et le confirmerPassword sont les mêmes
        if (!adminInscriptionDto.getPassword().equals(adminInscriptionDto.getConfirmerPassword())) {
            result.addError(
                    new FieldError("adminInscriptionDto", "confirmerPassword",
                            "le mot de passe et le mot de passe de confirmation sont incorrects"));
        }

        // Vérifie si l'adresse email est déjà utiliser
        AppUser appUser = repo.findByEmail(adminInscriptionDto.getEmail());
        if (appUser != null) {
            result.addError(
                    new FieldError("adminInscriptionDto", "email", "Cette adresse email est déjà utiliser"));
        }

        // Vérifiez si le fichier est vide
        if (file.isEmpty()) {
            result.addError(new FieldError("adminInscriptionDto", "image", "Aucun fichier sélectionné"));
        } else if (!file.getContentType().startsWith("image/")) {
            result.addError(new FieldError("adminInscriptionDto", "image", "Le fichier doit être une image"));
        }

        // Declenche l'erreur de validation
        if (result.hasErrors()) {
            return "private/admin/apps-tasks-create";
        }

        // Permet de créer un utilisateur
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + fileName;
            File destinationFile = new File(filePath);
            file.transferTo(destinationFile);
            // Code le password
            var bCryptEncoder = new BCryptPasswordEncoder();

            AppUser nouveauUtilisateur = new AppUser();
            nouveauUtilisateur.setNom(adminInscriptionDto.getNom());
            nouveauUtilisateur.setPrenom(adminInscriptionDto.getPrenom());
            nouveauUtilisateur.setEmail(adminInscriptionDto.getEmail());
            nouveauUtilisateur.setImage(filePath);
            nouveauUtilisateur.setAge(adminInscriptionDto.getAge());
            nouveauUtilisateur.setSexe(adminInscriptionDto.getSexe());
            nouveauUtilisateur.setNumero(adminInscriptionDto.getNumero());
            nouveauUtilisateur.setPassword(bCryptEncoder.encode(adminInscriptionDto.getPassword()));
            nouveauUtilisateur.setRole(adminInscriptionDto.getRole());
            nouveauUtilisateur.setCreatedAt(new Date());

            repo.save(nouveauUtilisateur);

            // Vider le formulaire d'inscription après avoir créer un utilisateur
            model.addAttribute("adminInscriptionDto", new AdminInscriptionDto());

            // message de succès
            model.addAttribute("success", true);

            // Redirection vers la page de connexion après l'inscription
            return "redirect:/admin/tableau-de-bord";

        } catch (Exception ex) {
            result.addError(
                    // Nous renvoyons l'erreur sur le nom s'il y a une erreur
                    new FieldError("adminInscriptionDto", "nom", ex.getMessage()));
        }
        return "private/admin/apps-tasks-create";
    }

    // Affiche la page de profile de l'administrateur authentifier
    @GetMapping("/admin/mon-profile")
    public String monProfile() {
        return "private/admin/pages-profile";
    }

    // Affiche la page de modification du profile de l'administrateur authentifier
    @GetMapping("/admin/modifier-mon-profile")
    public String ModifierMonProfile() {
        return "private/admin/pages-profile-settings";
    }

    // Affiche les détails d'un utilisateur
    @GetMapping("/admin/details-utilisateur")
    public String detailsUtilisateur() {
        return "private/admin/apps-tasks-details";
    }
}
