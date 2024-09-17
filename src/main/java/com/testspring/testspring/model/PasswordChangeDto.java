package com.testspring.testspring.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeDto {

    @NotBlank(message = "Ancien mot de passe est requis")
    private String oldPassword;

    @NotBlank(message = "Nouveau mot de passe est requis")
    @Size(min = 8, message = "Le nouveau mot de passe doit comporter au moins 8 caractères")
    private String newPassword;

    @NotBlank(message = "Confirmation du mot de passe est requise")
    private String confirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
