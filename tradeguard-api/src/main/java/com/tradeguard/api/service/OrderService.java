package com.tradeguard.api.service;

import com.tradeguard.api.dto.AssetDTO;
import com.tradeguard.api.dto.OrderDTO;
import com.tradeguard.api.entity.Customer;
import com.tradeguard.api.entity.Order;
import com.tradeguard.api.enums.OrderSide;
import com.tradeguard.api.enums.OrderStatus;
import com.tradeguard.api.exception.InvalidOrderStateException;
import com.tradeguard.api.exception.OrderNotFoundException;
import com.tradeguard.api.mapper.OrderMapper;
import com.tradeguard.api.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final AssetService assetService;
    private final TransactionService transactionService;

    public OrderService(OrderRepository orderRepository, CustomerService customerService, AssetService assetService, TransactionService transactionService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.assetService = assetService;
        this.transactionService = transactionService;
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDto) {
        Customer customer = customerService.findById(orderDto.getCustomerId());
        if (orderDto.getOrderSide().equals("BUY")) {
            Double customerBalance = transactionService.getCustomerBalance(orderDto.getCustomerId());
            Double requiredAmount = orderDto.getPrice() * orderDto.getSize();

            if (customerBalance < requiredAmount) {
                throw new RuntimeException("Insufficient balance: You do not have enough funds to complete this transaction.");
            }
            transactionService.updateCustomerBalance(orderDto.getCustomerId(), requiredAmount);
        }
        if (orderDto.getOrderSide().equals("SELL")) {
            AssetDTO assetToSell = assetService.findByCustomerIdAndAssetName(orderDto.getCustomerId(), orderDto.getAssetName());

            if (assetToSell.getUsableSize() < orderDto.getSize()) {
                throw new RuntimeException("Insufficient asset quantity: The amount of the asset you want to sell is not sufficient.");
            }
            assetToSell.setUsableSize(assetToSell.getUsableSize() - orderDto.getSize());
            assetService.updateAsset(assetToSell);
        }
        Order order = OrderMapper.toEntity(orderDto, customer);
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toDto(savedOrder);
    }

    public List<OrderDTO> listOrdersByCustomerOrDateRange(Long customerId, String startDate, String endDate) {
        List<Order> orders;
        if (customerId != null && startDate != null && endDate != null) {
            orders = orderRepository.findOrdersByCustomerAndDateRange(customerId, startDate, endDate);
        } else if (customerId != null) {
            orders = orderRepository.findOrdersByCustomer(customerId);
        } else if (startDate != null && endDate != null) {
            orders = orderRepository.findOrdersByDateRange(startDate, endDate);
        } else {
            orders = orderRepository.findAll();
        }
        return orders.stream().map(OrderMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new InvalidOrderStateException("Only PENDING orders can be canceled.");
        }
        orderRepository.updateOrderStatus(orderId, OrderStatus.CANCELED);
        if (order.getOrderSide() == OrderSide.BUY) {
            transactionService.depositMoney(order.getCustomer().getCustomerId(), order.getPrice() * order.getSize());
        }
        if (order.getOrderSide() == OrderSide.SELL) {
            AssetDTO asset = assetService.findByCustomerIdAndAssetName(order.getCustomer().getCustomerId(), order.getAssetName());
            asset.setUsableSize(asset.getUsableSize() + order.getSize());
            assetService.updateAsset(asset);
        }
    }

    public boolean isOrderOwner(Long orderId, Long customerId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return order.getCustomer().getCustomerId().equals(customerId);
    }

    @Transactional
    public void matchOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new InvalidOrderStateException("Only PENDING orders can be matched");
        }

        if (order.getOrderSide().equals(OrderSide.BUY)) {
            AssetDTO asset = assetService.findByCustomerIdAndAssetName(order.getCustomer().getCustomerId(), order.getAssetName());
            if (asset == null) {
                asset = new AssetDTO();
                asset.setCustomerId(order.getCustomer().getCustomerId());
                asset.setAssetName(order.getAssetName());
                asset.setSize(order.getSize());
                asset.setUsableSize(order.getSize());
            } else {
                asset.setSize(asset.getSize() + order.getSize());
                asset.setUsableSize(asset.getUsableSize() + order.getSize());
            }
            assetService.updateAsset(asset);
        }

        if (order.getOrderSide().equals(OrderSide.SELL)) {
            AssetDTO asset = assetService.findByCustomerIdAndAssetName(order.getCustomer().getCustomerId(), order.getAssetName());
            asset.setSize(asset.getSize() - order.getSize());
            assetService.updateAsset(asset);
            Double earnedAmount = order.getPrice() * order.getSize();
            transactionService.depositMoney(order.getCustomer().getCustomerId(), earnedAmount);
        }
        orderRepository.updateOrderStatus(orderId, OrderStatus.MATCHED);
    }


}
