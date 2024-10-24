package com.tradeguard.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tradeguard.api.dto.TransactionDTO;
import com.tradeguard.api.entity.Customer;
import com.tradeguard.api.entity.Transaction;
import com.tradeguard.api.enums.TransactionType;
import com.tradeguard.api.exception.CustomerNotFoundException;
import com.tradeguard.api.exception.InsufficientFundsException;
import com.tradeguard.api.repository.CustomerRepository;
import com.tradeguard.api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Description;

class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Description("Customer id'sine göre DEPOSIT işlemi yapar ve doğru şekilde kaydedildiğini doğrular.")
    void testDepositMoney() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TransactionDTO result = transactionService.depositMoney(1L, 100.0);
        assertEquals(100.0, result.getAmount());
        assertEquals(TransactionType.DEPOSIT, result.getTransactionType());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @Description("Customer bulunamadığında DEPOSIT işlemi yaparken exception fırlatır.")
    void testDepositMoney_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> transactionService.depositMoney(1L, 100.0));
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @Description("Customer id'sine göre WITHDRAW işlemi yapar ve doğru şekilde kaydedildiğini doğrular.")
    void testWithdrawMoney() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        Transaction depositTransaction = new Transaction();
        depositTransaction.setTransactionType(TransactionType.DEPOSIT);
        depositTransaction.setAmount(100.0);
        depositTransaction.setCustomer(customer);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomer(customer)).thenReturn(Collections.singletonList(depositTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TransactionDTO result = transactionService.withdrawMoney(1L, 50.0);
        assertEquals(50.0, result.getAmount());
        assertEquals(TransactionType.WITHDRAW, result.getTransactionType());
        verify(customerRepository, times(2)).findById(1L);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @Description("Customer bakiyesi yetersiz olduğunda WITHDRAW işlemi yaparken exception fırlatır.")
    void testWithdrawMoney_InsufficientFunds() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomer(customer)).thenReturn(Collections.emptyList());
        assertThrows(InsufficientFundsException.class, () -> transactionService.withdrawMoney(1L, 50.0));
        verify(customerRepository, times(2)).findById(1L);
    }

    @Test
    @Description("Customer'ın transaction geçmişini alır ve doğru şekilde döndürüldüğünü doğrular.")
    void testGetTransactionsByCustomer() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        Transaction transaction = new Transaction();
        transaction.setAmount(100.0);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setCustomer(customer);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomer(customer)).thenReturn(Collections.singletonList(transaction));
        List<TransactionDTO> result = transactionService.getTransactionsByCustomer(1L);
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getAmount());
        verify(customerRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).findByCustomer(customer);
    }

    @Test
    @Description("Tüm transactionları alır ve en az bir transaction bulunduğunu doğrular.")
    void testGetAllTransactions() {
        Transaction transaction = new Transaction();
        transaction.setAmount(100.0);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        transaction.setCustomer(customer);
        when(transactionRepository.findAll()).thenReturn(Collections.singletonList(transaction));
        List<TransactionDTO> result = transactionService.getAllTransactions();
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getAmount());
        verify(transactionRepository, times(1)).findAll();
    }


    @Test
    @Description("Customer id'sine göre bakiyeyi hesaplar ve doğru sonucu döner.")
    void testGetCustomerBalance() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        Transaction depositTransaction = new Transaction();
        depositTransaction.setAmount(100.0);
        depositTransaction.setTransactionType(TransactionType.DEPOSIT);
        depositTransaction.setCustomer(customer);
        Transaction withdrawTransaction = new Transaction();
        withdrawTransaction.setAmount(50.0);
        withdrawTransaction.setTransactionType(TransactionType.WITHDRAW);
        withdrawTransaction.setCustomer(customer);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomer(customer)).thenReturn(List.of(depositTransaction, withdrawTransaction));
        Double balance = transactionService.getCustomerBalance(1L);
        assertEquals(50.0, balance);
        verify(customerRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).findByCustomer(customer);
    }

    @Test
    @Description("Customer bakiyesi yetersiz olduğunda bakiyeyi güncellemeye çalışırken exception fırlatır.")
    void testUpdateCustomerBalance_InsufficientFunds() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomer(customer)).thenReturn(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> transactionService.updateCustomerBalance(1L, 100.0));
        verify(customerRepository, times(2)).findById(1L);
    }

}

