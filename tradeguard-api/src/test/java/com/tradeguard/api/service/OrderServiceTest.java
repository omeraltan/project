package com.tradeguard.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tradeguard.api.dto.AssetDTO;
import com.tradeguard.api.dto.OrderDTO;
import com.tradeguard.api.entity.Customer;
import com.tradeguard.api.entity.Order;
import com.tradeguard.api.exception.InvalidOrderStateException;
import com.tradeguard.api.repository.OrderRepository;
import com.tradeguard.api.enums.OrderSide;
import com.tradeguard.api.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Description;

import java.util.Optional;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private AssetService assetService;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Description("Başarılı bir satın alma (BUY) siparişi oluşturur. Customer bakiyesi yeterli ise, siparişin oluşturulup veritabanına kaydedilmesini ve customer'ın bakiyesinden sipariş tutarının düşülmesini doğrular.")
    void testCreateOrder_BuyOrder() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        OrderDTO orderDto = new OrderDTO();
        orderDto.setCustomerId(1L);
        orderDto.setOrderSide(OrderSide.BUY.toString());
        orderDto.setPrice(100.0);
        orderDto.setSize(1.0);

        when(customerService.findById(1L)).thenReturn(customer);
        when(transactionService.getCustomerBalance(1L)).thenReturn(200.0);
        OrderDTO result = orderService.createOrder(orderDto);
        verify(transactionService, times(1)).updateCustomerBalance(1L, 100.0);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @Description("BUY işlemi için customer bakiyesi yetersiz olduğunda exception fırlatılır.")
    void testCreateOrder_InsufficientBalance() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        OrderDTO orderDto = new OrderDTO();
        orderDto.setCustomerId(1L);
        orderDto.setOrderSide(OrderSide.BUY.toString());
        orderDto.setPrice(100.0);
        orderDto.setSize(2.0);
        when(customerService.findById(1L)).thenReturn(customer);
        when(transactionService.getCustomerBalance(1L)).thenReturn(150.0);
        assertThrows(RuntimeException.class, () -> orderService.createOrder(orderDto));
    }

    @Test
    @Description("Bir SELL işlemi başarılı olduğunda asset güncellenir ve order oluşturulur.")
    void testCreateOrder_SellOrder() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        OrderDTO orderDto = new OrderDTO();
        orderDto.setCustomerId(1L);
        orderDto.setOrderSide(OrderSide.SELL.toString());
        orderDto.setAssetName("Gold");
        orderDto.setSize(1.0);
        AssetDTO assetDto = new AssetDTO();
        assetDto.setUsableSize(2.0);
        when(customerService.findById(1L)).thenReturn(customer);
        when(assetService.findByCustomerIdAndAssetName(1L, "Gold")).thenReturn(assetDto);
        OrderDTO result = orderService.createOrder(orderDto);
        verify(assetService, times(1)).updateAsset(any(AssetDTO.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @Description("Bir BUY işlemi için satılacak asset miktarı yetersiz olduğunda exception fırlatılır.")
    void testCreateOrder_InsufficientAsset() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        OrderDTO orderDto = new OrderDTO();
        orderDto.setCustomerId(1L);
        orderDto.setOrderSide(OrderSide.SELL.toString());
        orderDto.setAssetName("Tesla Inc. Stock");
        orderDto.setSize(3.0);
        AssetDTO assetDto = new AssetDTO();
        assetDto.setUsableSize(2.0);
        when(customerService.findById(1L)).thenReturn(customer);
        when(assetService.findByCustomerIdAndAssetName(1L, "Tesla Inc. Stock")).thenReturn(assetDto);
        assertThrows(RuntimeException.class, () -> orderService.createOrder(orderDto));
    }

    @Test
    @Description("Verilen order id'sine göre order'ı iptal eder ve customer bakiyesini günceller.")
    void testCancelOrder_BuyOrder() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        Order order = new Order();
        order.setOrderId(1L);
        order.setCustomer(customer);
        order.setOrderSide(OrderSide.BUY);
        order.setStatus(OrderStatus.PENDING);
        order.setPrice(100.0);
        order.setSize(1.0);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        orderService.cancelOrder(1L);
        verify(orderRepository, times(1)).updateOrderStatus(1L, OrderStatus.CANCELED);
        verify(transactionService, times(1)).depositMoney(1L, 100.0);
    }

    @Test
    @Description("Sadece PENDING durumundaki order'ların iptal edilmesine izin verilir.")
    void testCancelOrder_NonPendingOrder() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        Order order = new Order();
        order.setOrderId(1L);
        order.setCustomer(customer);
        order.setOrderSide(OrderSide.BUY);
        order.setStatus(OrderStatus.MATCHED); // İptal edilemez durum
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(InvalidOrderStateException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    @Description("Verilen order id'sine göre order sahibini doğrular.")
    void testIsOrderOwner() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        Order order = new Order();
        order.setOrderId(1L);
        order.setCustomer(customer);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        boolean isOwner = orderService.isOrderOwner(1L, 1L);
        assertTrue(isOwner);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @Description("Verilen order id'sine göre MATCHED işlemi yapar ve customer asset'lerini günceller.")
    void testMatchOrder_BuyOrder() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        Order order = new Order();
        order.setOrderId(1L);
        order.setCustomer(customer);
        order.setOrderSide(OrderSide.BUY);
        order.setStatus(OrderStatus.PENDING);
        order.setAssetName("Ethereum");
        order.setSize(1.0);
        AssetDTO asset = new AssetDTO();
        asset.setSize(10.0);
        asset.setUsableSize(5.0);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(assetService.findByCustomerIdAndAssetName(1L, "Ethereum")).thenReturn(asset);
        orderService.matchOrder(1L);
        verify(assetService, times(1)).updateAsset(any(AssetDTO.class));
        verify(orderRepository, times(1)).updateOrderStatus(1L, OrderStatus.MATCHED);
    }
}
