package com.tradeguard.api.mapper;

import com.tradeguard.api.dto.AssetDTO;
import com.tradeguard.api.entity.Asset;
import com.tradeguard.api.entity.Customer;

public class AssetMapper {

    public static AssetDTO toDto(Asset asset) {
        if (asset == null) {
            return null;
        }

        return new AssetDTO(
            asset.getAssetId(),
            asset.getCustomer() != null ? asset.getCustomer().getCustomerId() : null,
            asset.getAssetName(),
            asset.getSize(),
            asset.getUsableSize()
        );
    }

    public static Asset toEntity(AssetDTO assetDto, Customer customer) {
        if (assetDto == null) {
            return null;
        }

        Asset asset = new Asset();
        asset.setAssetId(assetDto.getAssetId());
        asset.setCustomer(customer);
        asset.setAssetName(assetDto.getAssetName());
        asset.setSize(assetDto.getSize());
        asset.setUsableSize(assetDto.getUsableSize());

        return asset;
    }
}
