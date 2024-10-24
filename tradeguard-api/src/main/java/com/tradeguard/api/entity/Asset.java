package com.tradeguard.api.entity;

import jakarta.persistence.*;

@Entity
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assetId;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    private String assetName;
    private Double size;
    private Double usableSize;

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getUsableSize() {
        return usableSize;
    }

    public void setUsableSize(Double usableSize) {
        this.usableSize = usableSize;
    }
}
