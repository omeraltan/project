package com.tradeguard.api.controller;

import com.tradeguard.api.dto.AssetDTO;
import com.tradeguard.api.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tradeguard.api.constants.AssetUrls.*;

@RestController
@RequestMapping(ASSET_BASE)
@Tag(name = "asset.tag.name", description = "asset.tag.description")
public class AssetController {
    private final Logger log = LoggerFactory.getLogger(AssetController.class);
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @Operation(summary = "asset.summary.get.by.customer.and.name", description = "asset.description.get.by.customer.and.name")
    @GetMapping(ASSET_BY_CUSTOMER_AND_NAME)
    @PreAuthorize("#customerId == principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AssetDTO>> getAssetsByCustomerAndOptionalAssetName(
        @PathVariable Long customerId,
        @RequestParam(required = false) String assetName) {
        log.debug("getAssetsByCustomerAndOptionalAssetName called with customerId {} and assetName {}", customerId, assetName);
        List<AssetDTO> assets = assetService.getAssetsByCustomerAndOptionalAssetName(customerId, assetName);
        return ResponseEntity.ok(assets);
    }

    @Operation(summary = "asset.summary.get.all", description = "asset.description.get.all")
    @GetMapping(ALL)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AssetDTO>> getAllAssets() {
        log.debug("getAllAssets called");
        List<AssetDTO> assets = assetService.getAllAssets();
        return ResponseEntity.ok(assets);
    }

}
