package com.bertan.budgetplanner.mapper;

import com.bertan.budgetplanner.domain.transaction.Transaction;
import com.bertan.budgetplanner.dto.CreateTransactionRequest;
import com.bertan.budgetplanner.dto.TransactionResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {

    public TransactionResponse toDto(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCategory() != null ? transaction.getCategory().getId() : null,
                transaction.getTransactionDate(),
                transaction.getCreatedAt()
        );
    }

    public Transaction toEntity(CreateTransactionRequest dto) {
        Transaction transaction = new Transaction();
        transaction.setType(dto.type());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        transaction.setTransactionDate(dto.transactionDate());
        return transaction;
    }

    public List<TransactionResponse> toDtoList(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toDto)
                .toList();
    }
}
