package com.tradeguard.api.repository;

import com.tradeguard.api.entity.Order;
import com.tradeguard.api.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId")
    Optional<Order> findById(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o WHERE o.customer.customerId = :customerId AND o.createDate BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByCustomerAndDateRange(@Param("customerId") Long customerId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT o FROM Order o WHERE o.customer.customerId = :customerId")
    List<Order> findOrdersByCustomer(@Param("customerId") Long customerId);

    @Query("SELECT o FROM Order o WHERE o.createDate BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.orderId = :orderId")
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") OrderStatus status);

}
