package com.testspring.testspring.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.testspring.testspring.model.AppUser;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private String email;
    private String nom; // Champ personnalisé pour le nom
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(AppUser appUser, Collection<? extends GrantedAuthority> authorities) {
        this.email = appUser.getEmail();
        this.nom = appUser.getNom(); // Récupère le nom de l'utilisateur
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
