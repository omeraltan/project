package com.tradeguard.api.dto;

import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class OrderDTO {

    private Long orderId;
    @NotNull(message = "order.error.validation.empty.customerId")
    private Long customerId;
    @NotBlank(message = "order.error.validation.empty.assetName")
    private String assetName;
    @NotBlank(message = "order.error.validation.empty.orderSide")
    private String orderSide;
    @NotNull(message = "order.error.validation.empty.size")
    private Double size;
    @NotNull(message = "order.error.validation.empty.price")
    private Double price;
    @NotNull(message = "order.error.validation.empty.status")
    private String status;
    @Column(nullable = false)
    private String createDate;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now().toString();
    }

    public OrderDTO() {}

    public OrderDTO(Long orderId, Long customerId, String assetName, String orderSide, Double size, Double price, String status, String createDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.assetName = assetName;
        this.orderSide = orderSide;
        this.size = size;
        this.price = price;
        this.status = status;
        this.createDate = createDate;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getOrderSide() {
        return orderSide;
    }

    public void setOrderSide(String orderSide) {
        this.orderSide = orderSide;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

}
