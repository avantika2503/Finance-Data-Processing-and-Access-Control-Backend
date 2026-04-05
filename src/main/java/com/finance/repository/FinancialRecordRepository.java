package com.finance.repository;

import com.finance.model.FinancialRecord;
import com.finance.model.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>,
        JpaSpecificationExecutor<FinancialRecord> {

    @EntityGraph(type = EntityGraphType.FETCH, attributePaths = {"createdBy"})
    Optional<FinancialRecord> findByIdAndIsDeletedFalse(Long id);

    @Override
    @EntityGraph(type = EntityGraphType.FETCH, attributePaths = {"createdBy"})
    Page<FinancialRecord> findAll(Specification<FinancialRecord> spec, Pageable pageable);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.isDeleted = false AND r.type = :type")
    Double sumAmountByType(@Param("type") RecordType type);

    @Query("SELECT r.category, COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.isDeleted = false GROUP BY r.category ORDER BY r.category")
    List<Object[]> sumAmountByCategory();

    @Query(value = """
            SELECT FORMATDATETIME(r.entry_date, 'yyyy-MM') AS ym,
                   COALESCE(SUM(CASE WHEN r.record_type = 'INCOME' THEN r.amount ELSE 0 END), 0),
                   COALESCE(SUM(CASE WHEN r.record_type = 'EXPENSE' THEN r.amount ELSE 0 END), 0)
            FROM financial_records r
            WHERE r.is_deleted = FALSE
            GROUP BY FORMATDATETIME(r.entry_date, 'yyyy-MM')
            ORDER BY ym
            """, nativeQuery = true)
    List<Object[]> sumIncomeExpenseByMonth();

    @EntityGraph(type = EntityGraphType.FETCH, attributePaths = {"createdBy"})
    @Query("SELECT r FROM FinancialRecord r WHERE r.isDeleted = false ORDER BY r.entryDate DESC, r.createdAt DESC")
    List<FinancialRecord> findRecentActive(Pageable pageable);
}
