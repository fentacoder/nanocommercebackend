package com.infotechnano.nanocommerce.models;

import java.util.UUID;

public class Highlight {

    private UUID id;
    private UUID productId;
    private String message;
    private String createdAt;

    public Highlight(){

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Highlight{" +
                "id=" + id +
                ", productId=" + productId +
                ", highlight='" + message + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
