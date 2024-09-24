package com.testspring.testspring.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.testspring.testspring.model.AdminInscriptionDto;
import com.testspring.testspring.model.AdminModificationDto;
import com.testspring.testspring.model.AppUser;
import com.testspring.testspring.model.PasswordChangeDto;
import com.testspring.testspring.repositorie.AppUserRepository;
import com.testspring.testspring.service.AppUserService;
import com.testspring.testspring.service.CustomUserDetails;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AdminController {

    @Autowired
    private AppUserRepository repo;

    @Autowired
    private AppUserService service;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/uploads/{filename:.+}")
    @ResponseBody
    public ResponseEntity serveFile(@PathVariable String filename) {
        try {
            // Créer le chemin vers le fichier
            Path file = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            // Vérifier si le fichier existe et est lisible
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Affiche le tableau de bord de l'administrateur authentifier
    @GetMapping("/admin/tableau-de-bord")
    public ModelAndView admin(Model model) {

        // Récupère le nombre d'utilisateur
        long totalUtilisateurs = service.countUsers();

        // Récupère le nombre d'administrateur
        long totalAdmins = service.countAdmin();

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

        model.addAttribute("totalUtilisateurs", totalUtilisateurs);
        model.addAttribute("totalAdmins", totalAdmins);

        return new ModelAndView("private/admin/apps-tasks-list-view", "users", list);
    }

    // Affiche la page d'ajout d'un utilisateur par l'administrateur authentifier
    @GetMapping("/admin/ajout-utilisateur")
    public String ajoutUtilisateur(Model model) {
        AdminInscriptionDto adminInscriptionDto = new AdminInscriptionDto();
        model.addAttribute(adminInscriptionDto);

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
        return "private/admin/apps-tasks-create";
    }

    // Traite les données de la page ajout utilisateur
    @PostMapping("/admin/ajout-utilisateur")
    public String ajoutUtilisateur(Model model, @Valid @ModelAttribute AdminInscriptionDto adminInscriptionDto,
            BindingResult result, @RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes) {

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
            nouveauUtilisateur.setImage(fileName);
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

            // Message de succès
            redirectAttributes.addFlashAttribute("message_ajout", "Utilisateur ajouter avec succès !");

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
        return "private/admin/pages-profile";
    }

    // Affiche la page de modification du profile de l'administrateur authentifier
    @GetMapping("/admin/modifier-mon-profile")
    public String modifierMonProfile(Model model) {

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
            } else {
                nom = "Inconnu";
                prenom = "Inconnu";
                image = "Inconnu";
                age = "Inconnu";
                sexe = "Inconnu";
                numero = "Inconnu";
                role = "Inconnu";
                email = "Inconnu";
            }

            model.addAttribute("nom", nom);
            model.addAttribute("prenom", prenom);
            model.addAttribute("image", image);
            model.addAttribute("age", age);
            model.addAttribute("sexe", sexe);
            model.addAttribute("numero", numero);
            model.addAttribute("role", role);
            model.addAttribute("email", email);
        }

        // Pour changer le mot de passe
        model.addAttribute("passwordChangeDto", new PasswordChangeDto());

        return "private/admin/pages-profile-settings";
    }

    // Traite les données pour la modification du mot de passe
    @RequestMapping("/modifier-password")
    public String modifierPassword(Model model, @Valid @ModelAttribute PasswordChangeDto passwordChangeDto,
            BindingResult result, RedirectAttributes redirectAttributes) {

        // Récupérer l'utilisateur authentifié
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        AppUser existingUser = service.getAppUserByEmail(userDetails.getUsername());

        if (existingUser == null) {
            result.rejectValue("oldPassword", "error.passwordChangeDto", "Utilisateur non trouvé");
            return "private/admin/pages-profile-settings";
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
            return "private/admin/pages-profile-settings";
        }

        // Mettre à jour le mot de passe
        existingUser.setPassword(encoder.encode(passwordChangeDto.getNewPassword()));
        service.sauvegarder(existingUser);

        // Recharger les détails de l'utilisateur dans le contexte de sécurité
        UserDetails updatedUserDetails = service.loadUserByUsername(existingUser.getEmail());
        Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails,
                updatedUserDetails.getPassword(), updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // Message de succès
        redirectAttributes.addFlashAttribute("message_password", "Mot de passe modifier avec succès !");

        return "redirect:/admin/mon-profile";
    }

    // Traite les données de la page de modification du profile de l'administrateur
    // authentifier
    @PostMapping("/modifier-mon-profile")
    public String modifierProfile(Model model, @Valid @ModelAttribute AdminModificationDto adminModificationDto,
            @RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes) {

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

            // Mettre à jour les autres informations de l'utilisateur
            existingUser.setNom(adminModificationDto.getNom());
            existingUser.setPrenom(adminModificationDto.getPrenom());
            existingUser.setEmail(adminModificationDto.getEmail());
            existingUser.setAge(adminModificationDto.getAge());
            existingUser.setSexe(adminModificationDto.getSexe());
            existingUser.setNumero(adminModificationDto.getNumero());
            existingUser.setRole(adminModificationDto.getRole());

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

        // Message de succès
        redirectAttributes.addFlashAttribute("message_profile", "Profile modifier avec succès !");

        return "redirect:/admin/mon-profile";
    }

    // Affiche les détails d'un utilisateur
    @RequestMapping("/admin/details-utilisateur-{id}")
    public String detailsUtilisateur(@PathVariable("id") int id, Model model) {

        // Affiche les détails d'un utilisateur à partir de l'id
        AppUser appUser = service.getAppUserById(id);
        model.addAttribute("user", appUser);

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
        return "private/admin/apps-tasks-details";
    }

    // Affiche la page de modification d'un utilisateur par l'administrateur
    @RequestMapping("/admin/modifier-utilisateur-{id}")
    public String modifierUtilisateur(@PathVariable("id") int id, Model model) {

        AppUser appUser = service.getAppUserById(id);
        model.addAttribute("user", appUser);

        AdminModificationDto adminModificationDto = new AdminModificationDto();
        model.addAttribute(adminModificationDto);

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
        return "private/admin/apps-tasks-edit";
    }

    // Permet de traiter les données de la page de modification d'un utilisateur
    @PostMapping("/modifie")
    public String modifierUtilisateur(Model model, @Valid @ModelAttribute AdminModificationDto adminModificationDto,
            @RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes) {

        try {
            // Récupérer l'utilisateur existant à partir de la base de données
            AppUser existingUser = service.getAppUserById(adminModificationDto.getId());
            if (existingUser == null) {
                // Gérer le cas où l'utilisateur n'existe pas (afficher une erreur, etc.)
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

            // Mettre à jour les autres informations de l'utilisateur
            existingUser.setNom(adminModificationDto.getNom());
            existingUser.setPrenom(adminModificationDto.getPrenom());
            existingUser.setEmail(adminModificationDto.getEmail());
            existingUser.setAge(adminModificationDto.getAge());
            existingUser.setSexe(adminModificationDto.getSexe());
            existingUser.setNumero(adminModificationDto.getNumero());
            existingUser.setRole(adminModificationDto.getRole());

            // Sauvegarder les modifications dans la base de données
            service.sauvegarder(existingUser);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Message de succès
        redirectAttributes.addFlashAttribute("message_utilisateur", "Utilisateur modifier avec succès !");

        return "redirect:/admin/tableau-de-bord";
    }

    // Suppression d'un utilisateur par l'administrateur
    @RequestMapping("/admin/supprimer-utilisateur-{id}")
    public String supprimerUtilisateur(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {

        // Récupère l'utilisateur et supprime son image
        AppUser existingUser = service.getAppUserById(id);
        if (existingUser.getImage() != null && !existingUser.getImage().isEmpty()) {
            File oldImage = new File(uploadDir + existingUser.getImage());
            if (oldImage.exists()) {
                oldImage.delete();
            }
        }

        // Supprimer un utilisateur
        service.deleteById(id);

        // Message de succès
        redirectAttributes.addFlashAttribute("message_suppression", "Utilisateur supprimer avec succès !");

        return "redirect:/admin/tableau-de-bord";
    }
}
