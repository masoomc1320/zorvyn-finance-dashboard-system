package com.finance.dashboard.record;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    Optional<FinancialRecord> findByIdAndUser_Id(Long id, Long userId);

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r
            WHERE r.user.id = :userId AND r.type = :type
            AND (:from IS NULL OR r.recordDate >= :from)
            AND (:to IS NULL OR r.recordDate <= :to)
            """)
    BigDecimal sumAmountByUserAndType(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("""
            SELECT r.category, COALESCE(SUM(r.amount), 0) FROM FinancialRecord r
            WHERE r.user.id = :userId
            AND (:from IS NULL OR r.recordDate >= :from)
            AND (:to IS NULL OR r.recordDate <= :to)
            GROUP BY r.category
            """)
    List<Object[]> sumByCategory(
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("""
            SELECT r FROM FinancialRecord r WHERE r.user.id = :userId
            AND (:from IS NULL OR r.recordDate >= :from)
            AND (:to IS NULL OR r.recordDate <= :to)
            AND (:category IS NULL OR r.category = :category)
            AND (:type IS NULL OR r.type = :type)
            """)
    Page<FinancialRecord> findFiltered(
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("category") String category,
            @Param("type") TransactionType type,
            Pageable pageable);

    @Query("""
            SELECT r FROM FinancialRecord r WHERE
            (:from IS NULL OR r.recordDate >= :from)
            AND (:to IS NULL OR r.recordDate <= :to)
            AND (:category IS NULL OR r.category = :category)
            AND (:type IS NULL OR r.type = :type)
            """)
    Page<FinancialRecord> findAllFiltered(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("category") String category,
            @Param("type") TransactionType type,
            Pageable pageable);

    Page<FinancialRecord> findByUserIdOrderByRecordDateDescCreatedAtDesc(Long userId, Pageable pageable);

    @Query(
            value =
                    """
            SELECT DATE_FORMAT(r.record_date, '%Y-%u') AS bucket,
                   COALESCE(SUM(CASE WHEN r.type = 'INCOME' THEN r.amount ELSE 0 END), 0),
                   COALESCE(SUM(CASE WHEN r.type = 'EXPENSE' THEN r.amount ELSE 0 END), 0)
            FROM financial_records r
            WHERE r.user_id = :userId
            AND (:from IS NULL OR r.record_date >= :from)
            AND (:to IS NULL OR r.record_date <= :to)
            GROUP BY DATE_FORMAT(r.record_date, '%Y-%u')
            ORDER BY DATE_FORMAT(r.record_date, '%Y-%u')
            """,
            nativeQuery = true)
    List<Object[]> weeklyTrendsForUser(
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query(
            value =
                    """
            SELECT DATE_FORMAT(r.record_date, '%Y-%m') AS bucket,
                   COALESCE(SUM(CASE WHEN r.type = 'INCOME' THEN r.amount ELSE 0 END), 0),
                   COALESCE(SUM(CASE WHEN r.type = 'EXPENSE' THEN r.amount ELSE 0 END), 0)
            FROM financial_records r
            WHERE r.user_id = :userId
            AND (:from IS NULL OR r.record_date >= :from)
            AND (:to IS NULL OR r.record_date <= :to)
            GROUP BY DATE_FORMAT(r.record_date, '%Y-%m')
            ORDER BY DATE_FORMAT(r.record_date, '%Y-%m')
            """,
            nativeQuery = true)
    List<Object[]> monthlyTrendsForUser(
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}
