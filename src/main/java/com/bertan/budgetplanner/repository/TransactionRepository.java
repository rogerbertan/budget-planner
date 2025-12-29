package com.bertan.budgetplanner.repository;

import com.bertan.budgetplanner.domain.Transaction;
import com.bertan.budgetplanner.dto.TransactionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
