package com.finance.repository;

import com.finance.model.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>,
        JpaSpecificationExecutor<FinancialRecord> {

    @EntityGraph(type = EntityGraphType.FETCH, attributePaths = {"createdBy"})
    Optional<FinancialRecord> findByIdAndIsDeletedFalse(Long id);

    @Override
    @EntityGraph(type = EntityGraphType.FETCH, attributePaths = {"createdBy"})
    Page<FinancialRecord> findAll(Specification<FinancialRecord> spec, Pageable pageable);
}
