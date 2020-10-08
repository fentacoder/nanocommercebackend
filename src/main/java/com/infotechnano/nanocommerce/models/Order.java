package com.infotechnano.nanocommerce.models;

import java.util.Date;
import java.util.UUID;

public class Order {
    private UUID id;
    private UUID productId;
    private UUID userId;
    private UUID activityId;
    private String paypalOrderId;
    private String stripeOrderId;
    private String bidAmount;
    private String processingFee;
    private String shippingFee;
    private String totalPrice;
    private String orderDate;

    public Order(){

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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getPaypalOrderId() {
        return paypalOrderId;
    }

    public void setPaypalOrderId(String paypalOrderId) {
        this.paypalOrderId = paypalOrderId;
    }

    public String getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(String bidAmount) {
        this.bidAmount = bidAmount;
    }

    public String getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(String processingFee) {
        this.processingFee = processingFee;
    }

    public String getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(String shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public UUID getActivityId() {
        return activityId;
    }

    public void setActivityId(UUID activityId) {
        this.activityId = activityId;
    }

    public String getStripeOrderId() {
        return stripeOrderId;
    }

    public void setStripeOrderId(String stripeOrderId) {
        this.stripeOrderId = stripeOrderId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", productId=" + productId +
                ", userId=" + userId +
                ", activityId=" + activityId +
                ", paypalOrderId='" + paypalOrderId + '\'' +
                ", stripeOrderId='" + stripeOrderId + '\'' +
                ", bidAmount='" + bidAmount + '\'' +
                ", processingFee='" + processingFee + '\'' +
                ", shippingFee='" + shippingFee + '\'' +
                ", totalPrice='" + totalPrice + '\'' +
                ", orderDate='" + orderDate + '\'' +
                '}';
    }
}
