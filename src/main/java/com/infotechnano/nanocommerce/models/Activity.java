package com.infotechnano.nanocommerce.models;

import java.util.UUID;

public class Activity {

    private UUID id;
    private UUID hostId;
    private String title;
    private String location;
    private String price;
    private String details;
    private String breakDescription;
    private String activityDate;
    private String activityTime;
    private String createdAt;

    public Activity(){

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getHostId() {
        return hostId;
    }

    public void setHostId(UUID hostId) {
        this.hostId = hostId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getBreakDescription() {
        return breakDescription;
    }

    public void setBreakDescription(String breakDescription) {
        this.breakDescription = breakDescription;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", hostId=" + hostId +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", price='" + price + '\'' +
                ", details='" + details + '\'' +
                ", breakDescription='" + breakDescription + '\'' +
                ", activityDate='" + activityDate + '\'' +
                ", activityTime='" + activityTime + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
