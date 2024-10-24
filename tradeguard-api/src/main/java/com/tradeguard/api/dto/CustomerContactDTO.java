package com.tradeguard.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CustomerContactDTO {
    @NotEmpty(message = "customer.error.validation.empty.email")
    @Email(message = "customer.error.validation.valid.email")
    private String email;

    @Size(min = 10, max = 15, message = "customer.error.validation.size.phone")
    private String phoneNumber;

    public CustomerContactDTO() {
    }

    public CustomerContactDTO(String email, String phoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
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
}
