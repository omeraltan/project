package com.tradeguard.api.service;

import com.tradeguard.api.dto.CustomerDTO;
import com.tradeguard.api.dto.CustomerContactDTO;
import com.tradeguard.api.entity.Customer;
import com.tradeguard.api.exception.CustomerNotFoundException;
import com.tradeguard.api.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Description;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {
    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @Description("Verilen customer id'sine göre customer'ın bilgilerini getirir ve bu bilgilere customer'ın bakiyesini de ekler. CustomerDTO ile döner.")
    void testGetCustomerById_WithPositiveBalance() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setName("Omer Altun");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionService.getCustomerBalance(1L)).thenReturn(100.0);
        CustomerDTO result = customerService.getCustomerById(1L);
        assertEquals("Omer Altun", result.getName());
        assertEquals(100.0, result.getBalance());
        verify(customerRepository, times(1)).findById(1L);
        verify(transactionService, times(1)).getCustomerBalance(1L);
    }

    @Test
    @Description("Verilen customer id'sine sahip customer bulunamadığında exception fırlatır.")
    void testGetCustomerById_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(1L));
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @Description("Tüm customer'ları getirir ve bu bilgilere customer'ların bakiyesini de ekler.")
    void testGetAllCustomers_WithBalances() {
        Customer customer1 = new Customer();
        customer1.setCustomerId(1L);
        customer1.setName("Ebru Altun");
        Customer customer2 = new Customer();
        customer2.setCustomerId(2L);
        customer2.setName("Ömer Altan");

        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));
        when(transactionService.getCustomerBalance(1L)).thenReturn(100.0);
        when(transactionService.getCustomerBalance(2L)).thenReturn(200.0);
        List<CustomerDTO> result = customerService.getAllCustomers();
        assertEquals(2, result.size());
        assertEquals(100.0, result.get(0).getBalance());
        assertEquals(200.0, result.get(1).getBalance());
        verify(customerRepository, times(1)).findAll();
        verify(transactionService, times(1)).getCustomerBalance(1L);
        verify(transactionService, times(1)).getCustomerBalance(2L);
    }

    @Test
    @Description("Customer bulunamadığında tüm customer'ları getirirken exception fırlatır.")
    void testGetAllCustomers_ThrowsException() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(CustomerNotFoundException.class, () -> customerService.getAllCustomers());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @Description("Customer iletişim bilgilerini günceller ve güncellenmiş customer bilgilerini döner.")
    void testUpdateCustomerContact() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setEmail("omer@example.com");
        customer.setPhoneNumber("555-555-5555");

        CustomerContactDTO contactDTO = new CustomerContactDTO();
        contactDTO.setEmail("newomer@example.com");
        contactDTO.setPhoneNumber("555-123-4567");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        CustomerDTO updatedCustomer = customerService.updateCustomerContact(1L, contactDTO);
        assertEquals("newomer@example.com", updatedCustomer.getEmail());
        assertEquals("555-123-4567", updatedCustomer.getPhoneNumber());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    @Description("Verilen customer id'sine göre customer bulunamadığında iletişim bilgilerini güncellemeye çalışırken exception fırlatır.")
    void testUpdateCustomerContact_ThrowsException() {
        CustomerContactDTO contactDTO = new CustomerContactDTO();
        contactDTO.setEmail("omer@gmail.com");
        contactDTO.setPhoneNumber("555-123-4567");
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomerContact(1L, contactDTO));
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @Description("Verilen customer id'sine göre customer bilgilerini getirir.")
    void testFindById() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        when(customerRepository.findBy_Id(1L)).thenReturn(customer);
        Customer result = customerService.findById(1L);
        assertEquals(1L, result.getCustomerId());
        verify(customerRepository, times(1)).findBy_Id(1L);
    }
}
