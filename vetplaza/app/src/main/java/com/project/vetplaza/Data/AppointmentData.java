package com.project.vetplaza.Data;

import java.io.Serializable;

public class AppointmentData implements Serializable {
    private String id;
    public String client;
    public String date;
    public String time;
    public String phone;
    public String imageUri;
    public String hasAccepted;
    public String userId;
    public String clientId;
    public Boolean grooming, pharmacy, dentistry, surgical, laboratory, emergency;

    public AppointmentData() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public AppointmentData(String id, String client, String date, String time, String phone, String imageUri, String hasAccepted, String userId, String clientId, Boolean grooming, Boolean pharmacy, Boolean dentistry, Boolean surgical, Boolean laboratory,Boolean emergency) {
        this.id = id;
        this.client = client;
        this.date = date;
        this.time = time;
        this.phone = phone;
        this.imageUri = imageUri;
        this.hasAccepted = hasAccepted;
        this.userId = userId;
        this.clientId = clientId;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getHasAccepted() {
        return hasAccepted;
    }

    public void setHasAccepted(String hasAccepted) {
        this.hasAccepted = hasAccepted;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}



