package com.project.vetplaza.Data;

import java.io.Serializable;
import java.util.Date;

public class ClinicData implements Serializable {
    private String id;
    public String imageUri;
    public String name;
    public String email;
    public String password;
    public String phone;
    public String address;
    public String time;
    public String hasApproved;
    public String lastLogin;
    public String about;
    public String gcash;
    public Boolean grooming, pharmacy, dentistry, surgical, laboratory, emergency;

    public ClinicData() {

    }

    public ClinicData(String id, String imageUri, String name, String email, String password, String phone, String address, String time, String hasApproved, String lastLogin, String about, String gcash,
                      Boolean grooming,Boolean pharmacy, Boolean dentistry, Boolean surgical,Boolean laboratory, Boolean emergency) {
        this.id = id;
        this.imageUri = imageUri;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.time = time;
        this.hasApproved = hasApproved;
        this.lastLogin = lastLogin;
        this.about = about;
        this.gcash = gcash;
        this.grooming = grooming;
        this.pharmacy = pharmacy;
        this.dentistry = dentistry;
        this.surgical = surgical;
        this.laboratory = laboratory;
        this.emergency = emergency;
    }

    public Boolean getGrooming() {
        return grooming;
    }

    public void setGrooming(Boolean grooming) {
        this.grooming = grooming;
    }

    public Boolean getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Boolean pharmacy) {
        this.pharmacy = pharmacy;
    }

    public Boolean getDentistry() {
        return dentistry;
    }

    public void setDentistry(Boolean dentistry) {
        this.dentistry = dentistry;
    }

    public Boolean getSurgical() {
        return surgical;
    }

    public void setSurgical(Boolean surgical) {
        this.surgical = surgical;
    }

    public Boolean getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(Boolean laboratory) {
        this.laboratory = laboratory;
    }

    public Boolean getEmergency() {
        return emergency;
    }

    public void setEmergency(Boolean emergency) {
        this.emergency = emergency;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getGcash() {
        return gcash;
    }

    public void setGcash(String gcash) {
        this.gcash = gcash;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getHasApproved() {
        return hasApproved;
    }

    public void setHasApproved(String hasApproved) {
        this.hasApproved = hasApproved;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}