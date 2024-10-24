package com.tradeguard.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AssetDTO {
    @JsonIgnore
    private Long assetId;
    @JsonIgnore
    private Long customerId;
    private String assetName;
    private Double size;
    private Double usableSize;

    public AssetDTO() {}

    public AssetDTO(Long assetId, Long customerId, String assetName, Double size, Double usableSize) {
        this.assetId = assetId;
        this.customerId = customerId;
        this.assetName = assetName;
        this.size = size;
        this.usableSize = usableSize;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
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
