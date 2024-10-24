package com.tradeguard.api.mapper;

import com.tradeguard.api.dto.OrderDTO;
import com.tradeguard.api.entity.Customer;
import com.tradeguard.api.entity.Order;
import com.tradeguard.api.enums.OrderSide;
import com.tradeguard.api.enums.OrderStatus;

public class OrderMapper {

    public static OrderDTO toDto(Order order) {
        if (order == null) {
            return null;
        }

        return new OrderDTO(
            order.getOrderId(),
            order.getCustomer() != null ? order.getCustomer().getCustomerId() : null,
            order.getAssetName(),
            order.getOrderSide() != null ? order.getOrderSide().name() : null,
            order.getSize(),
            order.getPrice(),
            order.getStatus() != null ? order.getStatus().name() : null,
            order.getCreateDate()
        );
    }

    public static Order toEntity(OrderDTO orderDto, Customer customer) {
        if (orderDto == null) {
            return null;
        }

        Order order = new Order();
        order.setOrderId(orderDto.getOrderId());
        order.setCustomer(customer); // Customer entity, dışarıdan alınır.
        order.setAssetName(orderDto.getAssetName());
        order.setOrderSide(orderDto.getOrderSide() != null ? OrderSide.valueOf(orderDto.getOrderSide()) : null);
        order.setSize(orderDto.getSize());
        order.setPrice(orderDto.getPrice());
        order.setStatus(orderDto.getStatus() != null ? OrderStatus.valueOf(orderDto.getStatus()) : null);
        order.setCreateDate(orderDto.getCreateDate());

        return order;
    }


}
