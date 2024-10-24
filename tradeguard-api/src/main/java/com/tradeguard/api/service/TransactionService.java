package com.tradeguard.api.service;

import com.tradeguard.api.dto.TransactionDTO;
import com.tradeguard.api.entity.Customer;
import com.tradeguard.api.entity.Transaction;
import com.tradeguard.api.enums.TransactionType;
import com.tradeguard.api.exception.CustomerNotFoundException;
import com.tradeguard.api.exception.InsufficientFundsException;
import com.tradeguard.api.exception.NoDataFoundException;
import com.tradeguard.api.mapper.TransactionMapper;
import com.tradeguard.api.repository.CustomerRepository;
import com.tradeguard.api.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    public TransactionDTO depositMoney(Long customerId, Double amount) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now().toString());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionMapper.toDTO(savedTransaction);
    }

    public TransactionDTO withdrawMoney(Long customerId, Double amount) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
        if (getCustomerBalance(customerId) < amount) {
            throw new InsufficientFundsException("Insufficient balance for withdrawal.");
        }
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now().toString());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionMapper.toDTO(savedTransaction);
    }

    public List<TransactionDTO> getTransactionsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
        List<Transaction> transactions = transactionRepository.findByCustomer(customer);
        return transactions.stream().map(TransactionMapper::toDTO).collect(Collectors.toList());
    }

    public List<TransactionDTO> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        if (transactions.isEmpty()) {
            throw new NoDataFoundException("No records found.");
        }
        return transactions.stream().map(TransactionMapper::toDTO).collect(Collectors.toList());
    }

    public Double getCustomerBalance(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
        List<Transaction> transactions = transactionRepository.findByCustomer(customer);
        Double balance = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
                balance += Math.abs(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionType.WITHDRAW) {
                balance -= Math.abs(transaction.getAmount());
            }
        }
        return balance;
    }

    @Transactional
    public void updateCustomerBalance(Long customerId, Double amountToDeduct) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        Double currentBalance = getCustomerBalance(customerId);
        if (currentBalance < amountToDeduct) {
            throw new RuntimeException("Insufficient balance: You do not have enough funds to complete this transaction.");
        }
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setAmount(-amountToDeduct);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setTransactionDate(LocalDateTime.now().toString());
        transactionRepository.save(transaction);
    }
}
