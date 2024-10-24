package com.tradeguard.api.service;

import com.tradeguard.api.dto.AssetDTO;
import com.tradeguard.api.entity.Asset;
import com.tradeguard.api.entity.Customer;
import com.tradeguard.api.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Description;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AssetServiceTest {

    @InjectMocks
    private AssetService assetService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AssetRepository assetRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Description("Tüm asset'leri getirir ve müşterinin count'u sıfır olduğunda ek asset eklenmediğini doğrular.")
    void testGetAllAssets() {
        Asset asset = new Asset();
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        asset.setCustomer(customer);
        asset.setAssetName("Gold");

        when(assetRepository.findAll()).thenReturn(Collections.singletonList(asset));
        when(transactionService.getCustomerBalance(1L)).thenReturn(0.0);
        List<AssetDTO> result = assetService.getAllAssets();
        assertEquals(1, result.size());
        verify(assetRepository, times(1)).findAll();
        verify(transactionService, times(1)).getCustomerBalance(1L);
    }



    @Test
    @Description("Belirli bir customer id'si ile ve asset adı ile customer'a ait assetleri getirir. Eğer customer'ın bakiyesi varsa, bu bakiye TL olarak eklenir ve asset listesinde gösterilir.")
    void testGetAssetsByCustomerAndOptionalAssetName_WithAssetName() {
        Asset asset = new Asset();
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        asset.setCustomer(customer);
        asset.setAssetName("Gold");

        when(assetRepository.findByCustomerIdAndOptionalAssetName(1L, "Gold")).thenReturn(Collections.singletonList(asset));
        when(transactionService.getCustomerBalance(1L)).thenReturn(50.0);
        List<AssetDTO> result = assetService.getAssetsByCustomerAndOptionalAssetName(1L, "Gold");
        assertEquals(2, result.size());
        verify(assetRepository, times(1)).findByCustomerIdAndOptionalAssetName(1L, "Gold");
        verify(transactionService, times(1)).getCustomerBalance(1L);
    }

    @Test
    @Description("Asset adı verilmeden belirli bir customer id ile customer'a ait tüm assetleri getirir. Müşterinin bakiyesi sıfır olduğu için TL asset'i listede gösterilmez.")
    void testGetAssetsByCustomerAndOptionalAssetName_WithoutAssetName() {
        Asset asset = new Asset();
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        asset.setCustomer(customer);
        asset.setAssetName("Gold");

        when(assetRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(asset));
        when(transactionService.getCustomerBalance(1L)).thenReturn(0.0);
        List<AssetDTO> result = assetService.getAssetsByCustomerAndOptionalAssetName(1L, null);
        assertEquals(1, result.size());  // Sadece repo'dan gelen varlık, balance yok
        verify(assetRepository, times(1)).findByCustomerId(1L);
        verify(transactionService, times(1)).getCustomerBalance(1L);
    }

    @Test
    @Description("Belirli bir customer id ve asset adı ile veritabanından asset bilgilerini getirir ve doğru asset detaylarının döndürüldüğünü doğrular.")
    void testFindByCustomerIdAndAssetName() {
        Asset asset = new Asset();
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        asset.setCustomer(customer);
        asset.setAssetName("Gold");

        when(assetRepository.findByCustomerIdAndAssetName(1L, "Gold")).thenReturn(asset);
        AssetDTO result = assetService.findByCustomerIdAndAssetName(1L, "Gold");
        assertEquals("Gold", result.getAssetName());
        verify(assetRepository, times(1)).findByCustomerIdAndAssetName(1L, "Gold");
    }

    @Test
    @Description("Belirli bir customer id ve asset adı ile veritabanında asset bulunamadığında bir istisna fırlatıldığını doğrular.")
    void testFindByCustomerIdAndAssetName_ThrowsException() {
        when(assetRepository.findByCustomerIdAndAssetName(1L, "Gold")).thenReturn(null);
        assertThrows(RuntimeException.class, () -> assetService.findByCustomerIdAndAssetName(1L, "Gold"));
        verify(assetRepository, times(1)).findByCustomerIdAndAssetName(1L, "Gold");
    }

    @Test
    @Description("")
    void testUpdateAsset() {
        Asset asset = new Asset();
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        asset.setCustomer(customer);
        asset.setAssetId(1L);
        asset.setSize(10.0);
        asset.setUsableSize(8.0);

        AssetDTO assetDTO = new AssetDTO();
        assetDTO.setAssetId(1L);
        assetDTO.setSize(15.0);
        assetDTO.setUsableSize(12.0);

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        assetService.updateAsset(assetDTO);
        assertEquals(15.0, asset.getSize());
        assertEquals(12.0, asset.getUsableSize());
        verify(assetRepository, times(1)).findById(1L);
        verify(assetRepository, times(1)).save(asset);
    }

    @Test
    @Description("Verilen asset id'sine sahip asset'i günceller ve asset size'ının ve useableSize'ının güncellendiğini doğrular.")
    void testUpdateAsset_ThrowsException() {
        AssetDTO assetDTO = new AssetDTO();
        assetDTO.setAssetId(1L);

        when(assetRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> assetService.updateAsset(assetDTO));
        verify(assetRepository, times(1)).findById(1L);
    }

    @Test
    @Description("Pozitif bakiye durumunda customer'ın asset'lerini getirir ve TL asseet'inin eklenmesini doğrular.")
    void testGetAllAssets_WithPositiveBalance() {
        Asset asset = new Asset();
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        asset.setCustomer(customer);
        asset.setAssetName("Gold");

        when(assetRepository.findAll()).thenReturn(Collections.singletonList(asset));
        when(transactionService.getCustomerBalance(1L)).thenReturn(100.0);
        List<AssetDTO> result = assetService.getAllAssets();
        assertEquals(2, result.size());
        AssetDTO moneyAsset = result.get(1);
        assertEquals("TL", moneyAsset.getAssetName());
        assertEquals(100.0, moneyAsset.getSize());
        assertEquals(100.0, moneyAsset.getUsableSize());
        verify(assetRepository, times(1)).findAll();
        verify(transactionService, times(1)).getCustomerBalance(1L);
    }

}
