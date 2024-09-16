package com.testspring.testspring.controller;

import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.testspring.testspring.model.AppUser;
import com.testspring.testspring.model.InscriptionDto;
import com.testspring.testspring.repositorie.AppUserRepository;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AuthController {

    @Autowired
    private AppUserRepository repo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Affiche la page d'inscription de gère les règles de validation
    @GetMapping("/Inscription")
    public String inscription(Model model) {
        InscriptionDto inscriptionDto = new InscriptionDto();
        model.addAttribute(inscriptionDto);

        // Message de Succès
        model.addAttribute("success", false);
        return "auth/auth-signup-basic";
    }

    // Permet de valider le formulaire
    @PostMapping("/Inscription")
    public String inscription(Model model, @Valid @ModelAttribute InscriptionDto inscriptionDto, BindingResult result) {

        MultipartFile file = inscriptionDto.getImage();

        // Vérifie si le password et le confirmerPassword sont les mêmes
        if (!inscriptionDto.getPassword().equals(inscriptionDto.getConfirmerPassword())) {
            result.addError(
                    new FieldError("inscriptionDto", "confirmerPassword",
                            "le mot de passe et le mot de passe de confirmation sont incorrects"));
        }

        // Vérifie si l'adresse email est déjà utiliser
        AppUser appUser = repo.findByEmail(inscriptionDto.getEmail());
        if (appUser != null) {
            result.addError(
                    new FieldError("inscriptionDto", "email", "Cette adresse email est déjà utiliser"));
        }

        // Vérifiez si le fichier est vide
        if (file.isEmpty()) {
            result.addError(new FieldError("inscriptionDto", "image", "Aucun fichier sélectionné"));
        } else if (!file.getContentType().startsWith("image/")) {
            result.addError(new FieldError("inscriptionDto", "image", "Le fichier doit être une image"));
        }

        // Declenche l'erreur de validation
        if (result.hasErrors()) {
            return "auth/auth-signup-basic";
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
            nouveauUtilisateur.setNom(inscriptionDto.getNom());
            nouveauUtilisateur.setPrenom(inscriptionDto.getPrenom());
            nouveauUtilisateur.setEmail(inscriptionDto.getEmail());
            nouveauUtilisateur.setImage(fileName);
            nouveauUtilisateur.setAge(inscriptionDto.getAge());
            nouveauUtilisateur.setSexe(inscriptionDto.getSexe());
            nouveauUtilisateur.setNumero(inscriptionDto.getNumero());
            nouveauUtilisateur.setPassword(bCryptEncoder.encode(inscriptionDto.getPassword()));
            nouveauUtilisateur.setRole("utilisateur");
            nouveauUtilisateur.setCreatedAt(new Date());

            repo.save(nouveauUtilisateur);

            // Vider le formulaire d'inscription après avoir créer un utilisateur
            model.addAttribute("inscriptionDto", new InscriptionDto());

            // message de succès
            model.addAttribute("success", true);

            // Redirection vers la page de connexion après l'inscription
            return "redirect:/login";

        } catch (Exception ex) {
            result.addError(
                    // Nous renvoyons l'erreur sur le nom s'il y a une erreur
                    new FieldError("inscriptionDto", "nom", ex.getMessage()));
        }

        return "auth/auth-signup-basic";

    }

    @GetMapping("/login")
    public String connexion() {
        return "auth/auth-signin-basic";
    }

}
