package com.infotechnano.nanocommerce.models;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.UUID;

public class ActivityImage {

    private UUID id;
    private UUID activityId;
    private byte[] imageData;
    private String type;
    private String createdAt;

    public ActivityImage(){

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getActivityId() {
        return activityId;
    }

    public void setActivityId(UUID activityId) {
        this.activityId = activityId;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ActivityImage{" +
                "id=" + id +
                ", activityId=" + activityId +
                ", imageData=" + Arrays.toString(imageData) +
                ", type='" + type + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
