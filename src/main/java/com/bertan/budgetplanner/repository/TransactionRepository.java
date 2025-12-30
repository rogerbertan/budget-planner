package com.bertan.budgetplanner.repository;

import com.bertan.budgetplanner.domain.Transaction;
import com.bertan.budgetplanner.domain.Type;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.type = :type")
    BigDecimal sumAmountByType(@Param("type") Type type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.type = :type " +
            "AND YEAR(t.transactionDate) = :year " +
            "AND MONTH(t.transactionDate) = :month")
    BigDecimal sumAmountByMonthAndYearAndType(
            @Param("month") int month,
            @Param("year") int year,
            @Param("type") Type type);


    @Query("SELECT new com.bertan.budgetplanner.dto.CategoriesSummaryResponseDTO(" +
            "t.category.name, " +
            "COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0)) " +
            "FROM Transaction t JOIN t.category c " +
            "WHERE YEAR(t.transactionDate) = :year " +
            "AND MONTH(t.transactionDate) = :month " +
            "GROUP BY c.name")
    List<CategoriesSummaryResponseDTO> findCategorySummariesByMonthAndYear(
            @Param("month") int month,
            @Param("year") int year);
}
