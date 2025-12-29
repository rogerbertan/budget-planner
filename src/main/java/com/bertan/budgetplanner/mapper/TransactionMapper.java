package com.bertan.budgetplanner.mapper;

import com.bertan.budgetplanner.domain.Transaction;
import com.bertan.budgetplanner.dto.CreateTransactionRequestDTO;
import com.bertan.budgetplanner.dto.TransactionResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {

    public TransactionResponseDTO toDto(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCategory() != null ? transaction.getCategory().getId() : null,
                transaction.getTransactionDate(),
                transaction.getCreatedAt()
        );
    }

    public Transaction toEntity(CreateTransactionRequestDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setType(dto.type());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        transaction.setTransactionDate(dto.transactionDate());
        return transaction;
    }

    public List<TransactionResponseDTO> toDtoList(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toDto)
                .toList();
    }
}
