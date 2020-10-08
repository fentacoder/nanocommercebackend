package com.infotechnano.nanocommerce.models;

import java.util.UUID;

public class Comment {

    private UUID id;
    private UUID authorId;
    private UUID postId;
    private String message;
    private String createdAt;

    public Comment(){

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

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
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
        return "Comment{" +
                "id=" + id +
                ", authorId=" + authorId +
                ", postId=" + postId +
                ", messsage='" + message + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
