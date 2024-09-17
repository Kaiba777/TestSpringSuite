package com.testspring.testspring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

import com.testspring.testspring.model.AppUser;
import com.testspring.testspring.repositorie.AppUserRepository;

@Service
public class AppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository repo;

    // Permet d'authentifier un utilisateur par son email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = repo.findByEmail(email);

        // Si l'utilisateur existe
        if (appUser != null) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + appUser.getRole()));
            return new CustomUserDetails(appUser, authorities);
            // var authentifier = User.withUsername(appUser.getEmail())
            // .password(appUser.getPassword())
            // .roles(appUser.getRole())
            // .build();
            // return authentifier;
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    // Permet de récupérer tous les utilisateurs
    public List<AppUser> getAllAppUsers() {
        return repo.findAll();
    }

    // Permet de récupérer un utilisateur par l'id
    public AppUser getAppUserById(int id) {
        return repo.findById(id).get();
    }

    // Permet de sauvegarder les informations d'un utilisateur
    public void sauvegarder(AppUser appUser) {
        repo.save(appUser);
    }

    // Permet de supprimer un utilisateur
    public void deleteById(int id) {
        repo.deleteById(id);
    }

    // Permet de récupérer un utilisateur par son email
    public AppUser getAppUserByEmail(String email) {
        return repo.findByEmail(email);
    }

}
