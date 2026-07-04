package com.bertan.budgetplanner.repository;

import com.bertan.budgetplanner.domain.transaction.Transaction;
import com.bertan.budgetplanner.domain.category.Type;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByUserId(Long userId, Pageable pageable);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.type = :type")
    BigDecimal sumAmountByType(@Param("type") Type type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.type = :type " +
            "AND t.user.id = :userId")
    BigDecimal sumAmountByTypeAndUserId(@Param("type") Type type, @Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.type = :type " +
            "AND YEAR(t.transactionDate) = :year " +
            "AND MONTH(t.transactionDate) = :month")
    BigDecimal sumAmountByMonthAndYearAndType(
            @Param("month") int month,
            @Param("year") int year,
            @Param("type") Type type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.type = :type " +
            "AND YEAR(t.transactionDate) = :year " +
            "AND MONTH(t.transactionDate) = :month " +
            "AND t.user.id = :userId")
    BigDecimal sumAmountByMonthAndYearAndTypeAndUserId(
            @Param("month") int month,
            @Param("year") int year,
            @Param("type") Type type,
            @Param("userId") Long userId);

    @Query("SELECT new com.bertan.budgetplanner.dto.CategoriesSummaryResponse(" +
            "t.category.name, " +
            "COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0)) " +
            "FROM Transaction t JOIN t.category c " +
            "WHERE YEAR(t.transactionDate) = :year " +
            "AND MONTH(t.transactionDate) = :month " +
            "GROUP BY c.name")
    List<CategoriesSummaryResponse> findCategorySummariesByMonthAndYear(
            @Param("month") int month,
            @Param("year") int year);

    @Query("SELECT new com.bertan.budgetplanner.dto.CategoriesSummaryResponse(" +
            "t.category.name, " +
            "COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0)) " +
            "FROM Transaction t JOIN t.category c " +
            "WHERE YEAR(t.transactionDate) = :year " +
            "AND MONTH(t.transactionDate) = :month " +
            "AND t.user.id = :userId " +
            "GROUP BY c.name")
    List<CategoriesSummaryResponse> findCategorySummariesByMonthAndYearAndUserId(
            @Param("month") int month,
            @Param("year") int year,
            @Param("userId") Long userId);
}
