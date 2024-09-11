package com.testspring.testspring.model;


import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class InscriptionDto {

    @NotEmpty
    private String nom;

    @NotEmpty
    private String prenom;

    @NotEmpty
    @Email
    private String email;

    private MultipartFile image;

    @NotEmpty
    private String age;

    @NotEmpty
    private String sexe;

    @NotEmpty
    @Size(min = 8, max = 8, message = "Le numéro doit être de 8 chiffres")
    private String numero;

    @NotEmpty
    @Size(min = 8, message = "Le mot de passe doit être au minimum de 8 caractères")
    private String password;

    private String confirmerPassword;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmerPassword() {
        return confirmerPassword;
    }

    public void setConfirmerPassword(String confirmerPassword) {
        this.confirmerPassword = confirmerPassword;
    }

    

    

    
}
