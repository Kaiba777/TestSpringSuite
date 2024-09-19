package com.testspring.testspring.controller;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.testspring.testspring.model.AppUser;
import com.testspring.testspring.model.PasswordChangeDto;
import com.testspring.testspring.service.AppUserService;
import com.testspring.testspring.service.CustomUserDetails;

import jakarta.validation.Valid;

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
            String prenom;
            String image;
            String age;
            String sexe;
            String numero;
            String email;
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                nom = userDetails.getNom(); // Récupérer le nom
                prenom = userDetails.getPrenom(); // Récupérer le prénom
                image = userDetails.getImage(); // Récupérer l'image
                age = userDetails.getAge(); // Récupérer l'âge
                sexe = userDetails.getSexe(); // Récupérer le sexe
                numero = userDetails.getNumero(); // Récupérer le numéro
                email = userDetails.getUsername(); // Récupérer l'email
            } else {
                nom = "Inconnu";
                prenom = "Inconnu";
                image = "Inconnu";
                age = "Inconnu";
                sexe = "Inconnu";
                numero = "Inconnu";
                email = "Inconnu";
            }

            model.addAttribute("nom", nom);
            model.addAttribute("prenom", prenom);
            model.addAttribute("image", image);
            model.addAttribute("age", age);
            model.addAttribute("sexe", sexe);
            model.addAttribute("numero", numero);
            model.addAttribute("email", email);
        }

        // Pour changer le mot de passe
        model.addAttribute("passwordChangeDto", new PasswordChangeDto());

        return "private/user/pages-profile-settings";
    }

    // Traite les données de la page de modification du profile
    @PostMapping("/modifier-mon-profile-utilisateur")
    public String ModifierMonProfile(@RequestParam("image") MultipartFile file) {

        try {
            // Récupérer l'utilisateur authentifié
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            AppUser existingUser = service.getAppUserByEmail(userDetails.getUsername());

            if (existingUser == null) {
                return "redirect:/admin/error";
            }

            // Répertoire d'upload relatif à la racine du projet
            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            // Créez le répertoire s'il n'existe pas
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            // Si un nouveau fichier est uploadé
            if (!file.isEmpty()) {
                // Supprimer l'ancienne image si elle existe
                if (existingUser.getImage() != null && !existingUser.getImage().isEmpty()) {
                    File oldImage = new File(uploadDir + existingUser.getImage());
                    if (oldImage.exists()) {
                        oldImage.delete();
                    }
                }

                // Enregistrer le nouveau fichier
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File newImage = new File(uploadDir + fileName);
                file.transferTo(newImage);

                // Mettre à jour le chemin de la nouvelle image
                existingUser.setImage(fileName);
            }

            // Sauvegarder les modifications dans la base de données
            service.sauvegarder(existingUser);

            // Recharger les détails de l'utilisateur dans le contexte de sécurité
            UserDetails updatedUserDetails = service.loadUserByUsername(existingUser.getEmail());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails,
                    updatedUserDetails.getPassword(), updatedUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/user/mon-profile";
    }

    // Traite les données pour la modification du mot de passe
    @RequestMapping("/modifier-password-utilisateur")
    public String ModifierMonProfile(Model model, @Valid @ModelAttribute PasswordChangeDto passwordChangeDto,
            BindingResult result) {

        // Récupérer l'utilisateur authentifié
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        AppUser existingUser = service.getAppUserByEmail(userDetails.getUsername());

        if (existingUser == null) {
            result.rejectValue("oldPassword", "error.passwordChangeDto", "Utilisateur non trouvé");
            return "private/user/pages-profile-settings";
        }

        // Vérifier l'ancien mot de passe
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(passwordChangeDto.getOldPassword(), existingUser.getPassword())) {
            result.rejectValue("oldPassword", "error.passwordChangeDto", "Ancien mot de passe incorrect");
        }

        // Vérifier si le nouveau mot de passe et la confirmation sont les mêmes
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.passwordChangeDto", "Les mots de passe ne correspondent pas");
        }

        // Valider les erreurs
        if (result.hasErrors()) {
            return "private/user/pages-profile-settings";
        }

        // Mettre à jour le mot de passe
        existingUser.setPassword(encoder.encode(passwordChangeDto.getNewPassword()));
        service.sauvegarder(existingUser);

        // Recharger les détails de l'utilisateur dans le contexte de sécurité
        UserDetails updatedUserDetails = service.loadUserByUsername(existingUser.getEmail());
        Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails,
                updatedUserDetails.getPassword(), updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        return "redirect:/user/mon-profile";
    }

}
