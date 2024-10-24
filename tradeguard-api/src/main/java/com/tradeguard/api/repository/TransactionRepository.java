package com.tradeguard.api.repository;

import com.tradeguard.api.entity.Customer;
import com.tradeguard.api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCustomer(Customer customer);

}
