package com.tradeguard.api.repository;

import com.tradeguard.api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.customerId = :customerId")
    Customer findBy_Id(@Param("customerId") Long customerId);

}
