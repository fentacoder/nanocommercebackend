package com.infotechnano.nanocommerce.models;

import java.util.Arrays;
import java.util.UUID;

public class Post {

    private UUID id;
    private UUID authorId;
    private String title;
    private String price;
    private byte[] authorImage;
    private String message;
    private Integer likes;
    private String createdAt;

    public Post(){

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public byte[] getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(byte[] authorImage) {
        this.authorImage = authorImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", authorId=" + authorId +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", authorImage=" + Arrays.toString(authorImage) +
                ", message='" + message + '\'' +
                ", likes=" + likes +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
