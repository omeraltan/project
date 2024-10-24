package com.tradeguard.api.service;

import com.tradeguard.api.dto.CustomerContactDTO;
import com.tradeguard.api.dto.CustomerDTO;
import com.tradeguard.api.entity.Customer;
import com.tradeguard.api.exception.CustomerNotFoundException;
import com.tradeguard.api.mapper.CustomerMapper;
import com.tradeguard.api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TransactionService transactionService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, TransactionService transactionService) {
        this.customerRepository = customerRepository;
        this.transactionService = transactionService;
    }

    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
        Double balance = transactionService.getCustomerBalance(customer.getCustomerId());
        CustomerDTO customerDTO = CustomerMapper.toDTO(customer);
        customerDTO.setBalance(balance);
        return customerDTO;
    }

    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            throw new CustomerNotFoundException("No customers found");
        }
        return customers.stream()
            .map(customer -> {
                CustomerDTO customerDTO = CustomerMapper.toDTO(customer);
                Double balance = transactionService.getCustomerBalance(customer.getCustomerId());
                customerDTO.setBalance(balance);
                return customerDTO;
            })
            .collect(Collectors.toList());
    }

    public CustomerDTO updateCustomerContact(Long id, CustomerContactDTO customerContactDTO) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
        customer.setEmail(customerContactDTO.getEmail());
        customer.setPhoneNumber(customerContactDTO.getPhoneNumber());
        Customer updatedCustomer = customerRepository.save(customer);
        return CustomerMapper.toDTO(updatedCustomer);
    }

    public Customer findById(Long customerId) {
        return customerRepository.findBy_Id(customerId);
    }
}
