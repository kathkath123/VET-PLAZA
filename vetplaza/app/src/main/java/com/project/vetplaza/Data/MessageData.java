package com.project.vetplaza.Data;

import java.io.Serializable;

public class MessageData implements Serializable {
    private String id;
    public String imageUri;
    public String name;
    public String clinicId;

    public MessageData() {

    }

    public MessageData(String id, String imageUri, String name, String clinicId) {
        this.id = id;
        this.imageUri = imageUri;
        this.name = name;
        this.clinicId = clinicId;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
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
}