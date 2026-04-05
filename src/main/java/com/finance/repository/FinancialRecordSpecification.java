package com.finance.repository;

import com.finance.model.FinancialRecord;
import com.finance.model.enums.RecordType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class FinancialRecordSpecification {

    private FinancialRecordSpecification() {
    }

    public static Specification<FinancialRecord> activeWithFilters(
            RecordType type,
            String category,
            LocalDate startDate,
            LocalDate endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("isDeleted")));
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase()));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("entryDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("entryDate"), endDate));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
