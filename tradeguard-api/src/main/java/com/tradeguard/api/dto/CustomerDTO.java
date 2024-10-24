package com.tradeguard.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CustomerDTO {
    private Long customerId;
    @NotEmpty(message = "customer.error.validation.empty.name")
    private String name;
    @NotEmpty(message = "customer.error.validation.empty.email")
    @Email(message = "customer.error.validation.valid.email")
    private String email;
    @Size(min = 10, max = 15, message = "customer.error.validation.size.phone")
    private String phoneNumber;
    private Double balance;

    public CustomerDTO() {
    }

    public CustomerDTO(Long customerId, String name, String email, String phoneNumber) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
