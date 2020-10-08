package com.infotechnano.nanocommerce.models;

import java.util.Arrays;
import java.util.UUID;

public class Message implements Comparable<Message> {

    private UUID id;
    private UUID senderId;
    private String senderName;
    private UUID receiverId;
    private String message;
    private byte[] image;
    private String imageType;
    private Integer readYet;
    private String type;
    private String sentAt;

    public Message(){

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public Integer getReadYet() {
        return readYet;
    }

    public void setReadYet(Integer readYet) {
        this.readYet = readYet;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public int compareTo(Message o) {
        return getSentAt().compareTo(o.getSentAt());
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", senderName='" + senderName + '\'' +
                ", receiverId=" + receiverId +
                ", message='" + message + '\'' +
                ", image=" + Arrays.toString(image) +
                ", imageType='" + imageType + '\'' +
                ", readYet=" + readYet +
                ", type='" + type + '\'' +
                ", sentAt='" + sentAt + '\'' +
                '}';
    }
}
