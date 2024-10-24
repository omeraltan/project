package com.tradeguard.api.controller;

import com.tradeguard.api.dto.TransactionDTO;
import com.tradeguard.api.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tradeguard.api.constants.TransactionUrls.*;

@RestController
@RequestMapping(TRANSACTION_BASE)
@Tag(name = "transaction.tag.name", description = "transaction.tag.description")
public class TransactionController {
    private final Logger log = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "transaction.summary.get.customer.transactions", description = "transaction.description.get.customer.transactions")
    @GetMapping(GET_CUSTOMER_TRANSACTIONS)
    @PreAuthorize("#customerId == principal.id or hasRole('ADMIN')")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByCustomer(@PathVariable Long customerId) {
        log.info("getTransactionsByCustomer {}", customerId);
        List<TransactionDTO> transactions = transactionService.getTransactionsByCustomer(customerId);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "transaction.summary.get.all.transactions", description = "transaction.description.get.all.transactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the transactions"),
        @ApiResponse(responseCode = "404", description = "No transactions found", content = @Content)
    })
    @GetMapping(GET_ALL_TRANSACTIONS)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        log.info("getAllTransactions");
        List<TransactionDTO> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "transaction.summary.deposit", description = "transaction.description.deposit")
    @PostMapping(DEPOSIT)
    @PreAuthorize("#customerId == principal.id")
    public ResponseEntity<TransactionDTO> depositMoney(@RequestParam Long customerId, @RequestParam Double amount) {
        log.info("depositMoney {}", amount);
        TransactionDTO transactionDTO = transactionService.depositMoney(customerId, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionDTO);
    }

    @Operation(summary = "transaction.summary.withdraw", description = "transaction.description.withdraw")
    @PostMapping(WITHDRAW)
    @PreAuthorize("#customerId == principal.id")
    public ResponseEntity<TransactionDTO> withdrawMoney(@RequestParam Long customerId, @RequestParam Double amount) {
        log.info("withdrawMoney {}", amount);
        TransactionDTO transactionDTO = transactionService.withdrawMoney(customerId, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionDTO);
    }
}
