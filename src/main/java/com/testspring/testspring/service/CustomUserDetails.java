package com.testspring.testspring.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.testspring.testspring.model.AppUser;

import java.util.Collection;
import java.util.Date;

public class CustomUserDetails implements UserDetails {

    private String email;
    private String nom; // Champ personnalisé pour le nom
    private String prenom; // Champ personnalisé pour le prénom
    private String image; // Champ personnalisé pour l'image
    private String age; // Champ personnalisé pour l'age
    private String sexe; // Champ personnalisé pour le sexe
    private String numero; // Champ personnalisé pour le numéro de téléphone
    private String role; // Champ personnalisé pour le role
    private Date createdAt; // Champ personnalisé pour la date de création du compte
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(AppUser appUser, Collection<? extends GrantedAuthority> authorities) {
        this.email = appUser.getEmail();
        this.nom = appUser.getNom(); // Récupère le nom de l'utilisateur
        this.prenom = appUser.getPrenom(); // Récupère le prénom de l'utilisateur
        this.image = appUser.getImage(); // Récupère le l'image de l'utilisateur
        this.age = appUser.getAge(); // Récupère l'âge de l'utilisateur
        this.sexe = appUser.getSexe(); // Récupère le sexe de l'utilisateur
        this.numero = appUser.getNumero(); // Récupère le numéro de l'utilisateur
        this.role = appUser.getRole(); // Récupère le role de l'utilisateur
        this.createdAt = appUser.getCreatedAt(); // Récupère la date de création du compte
        this.password = appUser.getPassword();
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getNom() {
        return nom; // Méthode personnalisée pour récupérer le nom
    }

    public String getPrenom() {
        return prenom; // Méthode personnalisée pour récupérer le prénom
    }

    public String getImage() {
        return image; // Méthode personnalisée pour récupérer l'image
    }

    public String getAge() {
        return age; // Méthode personnalisée pour récupérer l'age
    }

    public String getSexe() {
        return sexe; // Méthode personnalisée pour récupérer le sexe
    }

    public String getNumero() {
        return numero; // Méthode personnalisée pour récupérer le numéro
    }

    public String getRole() {
        return role; // Méthode personnalisée pour récupérer le role
    }

    public Date getCreatedAt() {
        return createdAt; // Méthode personnalisée pour récupérer la date
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
