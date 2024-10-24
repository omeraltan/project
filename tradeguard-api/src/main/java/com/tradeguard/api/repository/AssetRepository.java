package com.tradeguard.api.repository;

import com.tradeguard.api.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query("SELECT a FROM Asset a WHERE a.customer.customerId = :customerId AND (:assetName IS NULL OR a.assetName = :assetName)")
    List<Asset> findByCustomerIdAndOptionalAssetName(@Param("customerId") Long customerId, @Param("assetName") String assetName);

    @Query("SELECT a FROM Asset a WHERE a.customer.customerId = :customerId")
    List<Asset> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT a FROM Asset a WHERE a.customer.customerId = :customerId AND a.assetName = :assetName")
    Asset findByCustomerIdAndAssetName(@Param("customerId") Long customerId, @Param("assetName") String assetName);

}
