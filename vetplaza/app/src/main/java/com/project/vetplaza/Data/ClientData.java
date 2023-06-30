package com.project.vetplaza.Data;

import java.io.Serializable;

public class ClientData implements Serializable {
    private String id;
    public String imageUri;
    public String name;
    public String email;
    public String password;
    public String phone;
    public String address;
    public String petName;
    public String petType;
    public String petMedicalHistory;


    public ClientData() {

    }

    public ClientData(String id, String imageUri, String name, String email, String password, String phone, String address, String petName, String petType, String petMedicalHistory) {
        this.id = id;
        this.imageUri = imageUri;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.petName = petName;
        this.petType = petType;
        this.petMedicalHistory = petMedicalHistory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getPetMedicalHistory() {
        return petMedicalHistory;
    }

    public void setPetMedicalHistory(String petMedicalHistory) {
        this.petMedicalHistory = petMedicalHistory;
    }
}