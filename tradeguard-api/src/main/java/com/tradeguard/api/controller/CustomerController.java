package com.tradeguard.api.controller;

import com.tradeguard.api.dto.CustomerContactDTO;
import com.tradeguard.api.dto.CustomerDTO;
import com.tradeguard.api.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tradeguard.api.constants.CustomerUrls.*;

@RestController
@RequestMapping(CUSTOMER_BASE)
@Tag(name = "customer.tag.name", description = "customer.tag.description")
public class CustomerController {
    private final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "customer.summary.get.id", description = "customer.description.get.id")
    @GetMapping(ID)
    @PreAuthorize("#id == principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        log.debug("Request to get Customer : {}", id);
        CustomerDTO customerDTO = customerService.getCustomerById(id);
        return ResponseEntity.ok(customerDTO);
    }

    @Operation(summary = "customer.summary.get.all", description = "customer.description.get.all")
    @GetMapping(GET_ALL_CUSTOMERS)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        log.debug("Request to get a page of Customers");
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "customer.summary.update", description = "customer.description.update")
    @PutMapping(ID)
    @PreAuthorize("#id == principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CustomerDTO> updateCustomerContact(@PathVariable Long id, @Valid @RequestBody CustomerContactDTO customerContactDTO) {
        log.debug("Request to update Customer : {}, {}", id, customerContactDTO);
        CustomerDTO updatedCustomer = customerService.updateCustomerContact(id, customerContactDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

}
