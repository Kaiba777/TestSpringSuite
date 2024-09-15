package com.testspring.testspring.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // Vérifier les rôles de l'utilisateur
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_administrateur"))) {
            // Rediriger vers la page administrateur
            response.sendRedirect("/admin/tableau-de-bord");
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_utilisateur"))) {
            // Rediriger vers la page utilisateur
            response.sendRedirect("/user/tableau-de-bord");
        } else {
            // Rediriger vers la page d'accueil par défaut
            response.sendRedirect("/");
        }
    }

}
