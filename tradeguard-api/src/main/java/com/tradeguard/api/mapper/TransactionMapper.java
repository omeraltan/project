package com.tradeguard.api.mapper;

import com.tradeguard.api.dto.TransactionDTO;
import com.tradeguard.api.entity.Transaction;

public class TransactionMapper {

    public static TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setCustomerId(transaction.getCustomer().getCustomerId());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionDate(transaction.getTransactionDate());
        return dto;
    }
}
