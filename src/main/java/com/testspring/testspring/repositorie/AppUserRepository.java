package com.testspring.testspring.repositorie;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testspring.testspring.model.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    // Retrouver un utilisateur par son email
    public AppUser findByEmail(String email);

    // Récupérer le nombre d'administrateur
    long countByRole(String role);
}
