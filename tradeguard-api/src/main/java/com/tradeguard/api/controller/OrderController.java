package com.tradeguard.api.controller;


import com.tradeguard.api.dto.OrderDTO;
import com.tradeguard.api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tradeguard.api.constants.OrderUrls.*;

@RestController
@RequestMapping(ORDER_BASE)
@Tag(name = "order.tag.name", description = "order.tag.description")
public class OrderController {
    private final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "order.summary.create", description = "order.description.create")
    @PostMapping
    @PreAuthorize("#orderDTO.customerId == principal.id")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        log.debug("REST request to save Order : {}", orderDTO);
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(summary = "order.summary.list", description = "order.description.list")
    @GetMapping(ORDER_LIST)
    @PreAuthorize("hasRole('ADMIN') or #customerId == principal.id")
    public ResponseEntity<List<OrderDTO>> listOrders(
        @RequestParam(required = false) Long customerId,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate) {
        log.debug("REST request to list Orders : {} {} {}", customerId, startDate, endDate);
        List<OrderDTO> orders = orderService.listOrdersByCustomerOrDateRange(customerId, startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "order.summary.delete", description = "order.description.delete")
    @DeleteMapping(ORDER_BY_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN') or @orderService.isOrderOwner(#orderId, principal.id)")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        log.debug("REST request to delete Order : {}", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order successfully canceled.");
    }

    @Operation(summary = "order.summary.match", description = "order.description.match")
    @PostMapping(ORDER_MATCH)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> matchOrder(@PathVariable Long orderId) {
        log.debug("REST request to match Order : {}", orderId);
        orderService.matchOrder(orderId);
        return ResponseEntity.ok().build();
    }

}
