package com.testspring.testspring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.testspring.testspring.model.AppUser;
import com.testspring.testspring.repositorie.AppUserRepository;

@Service
public class AppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository repo;

    //Permet d'authentifier un utilisateur par son email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = repo.findByEmail(email);

        //Si l'utilisateur existe
        if (appUser != null) {
            var authentifier = User.withUsername(appUser.getEmail())
                                            .password(appUser.getPassword())
                                            .roles(appUser.getRole())
                                            .build();
            return authentifier;
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

}
