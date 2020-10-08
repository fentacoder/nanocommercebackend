package com.infotechnano.nanocommerce.models;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.UUID;

public class ArticleImage {

    private UUID id;
    private UUID articleId;
    private byte[] imageData;
    private String type;
    private String createdAt;

    public ArticleImage(){

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getArticleId() {
        return articleId;
    }

    public void setArticleId(UUID articleId) {
        this.articleId = articleId;
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
        return "ArticleImage{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", imageData=" + Arrays.toString(imageData) +
                ", type='" + type + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
