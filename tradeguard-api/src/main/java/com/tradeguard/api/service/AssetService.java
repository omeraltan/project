package com.tradeguard.api.service;

import com.tradeguard.api.dto.AssetDTO;
import com.tradeguard.api.entity.Asset;
import com.tradeguard.api.mapper.AssetMapper;
import com.tradeguard.api.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetService {

    private final TransactionService transactionService;
    private final AssetRepository assetRepository;

    @Autowired
    public AssetService(TransactionService transactionService, AssetRepository assetRepository) {
        this.transactionService = transactionService;
        this.assetRepository = assetRepository;
    }

    public List<AssetDTO> getAllAssets() {
        List<Asset> assets = assetRepository.findAll();
        List<AssetDTO> assetDTOs = assets.stream().map(AssetMapper::toDto).collect(Collectors.toList());
        List<AssetDTO> additionalAssets = new ArrayList<>();
        assetDTOs.forEach(assetDTO -> {
            Double balance = transactionService.getCustomerBalance(assetDTO.getCustomerId());
            if (balance > 0) {
                AssetDTO moneyAsset = new AssetDTO();
                moneyAsset.setAssetName("TL");
                moneyAsset.setSize(balance);
                moneyAsset.setUsableSize(balance);
                additionalAssets.add(moneyAsset);  // Yeni listeye ekleme yapılıyor
            }
        });
        assetDTOs.addAll(additionalAssets);
        return assetDTOs;
    }


    public List<AssetDTO> getAssetsByCustomerAndOptionalAssetName(Long customerId, String assetName) {
        List<Asset> assets;
        if (assetName != null && !assetName.isEmpty()) {
            assets = assetRepository.findByCustomerIdAndOptionalAssetName(customerId, assetName);
        } else {
            assets = assetRepository.findByCustomerId(customerId);
        }
        List<AssetDTO> assetDTOs = assets.stream().map(AssetMapper::toDto).collect(Collectors.toList());
        Double balance = transactionService.getCustomerBalance(customerId);
        if (balance > 0) {
            AssetDTO moneyAsset = new AssetDTO();
            moneyAsset.setAssetName("TL");
            moneyAsset.setSize(balance);
            moneyAsset.setUsableSize(balance);
            assetDTOs.add(moneyAsset);
        }
        return assetDTOs;
    }

    public AssetDTO findByCustomerIdAndAssetName(Long customerId, String assetName) {
        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName);
        if (asset == null) {
            throw new RuntimeException("Asset not found for customer with ID: " + customerId + " and asset name: " + assetName);
        }
        return AssetMapper.toDto(asset);
    }

    public void updateAsset(AssetDTO assetDTO) {
        Asset asset = assetRepository.findById(assetDTO.getAssetId()).orElseThrow(() -> new RuntimeException("Asset not found with ID: " + assetDTO.getAssetId()));
        asset.setSize(assetDTO.getSize());
        asset.setUsableSize(assetDTO.getUsableSize());
        assetRepository.save(asset);
    }
}
