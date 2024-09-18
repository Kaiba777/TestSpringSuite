package com.testspring.testspring.model;

import org.springframework.web.multipart.MultipartFile;

public class UserModificationDto {

    private MultipartFile image;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

}
