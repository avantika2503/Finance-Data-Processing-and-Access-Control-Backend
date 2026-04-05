package com.finance.service;

import com.finance.model.FinancialRecord;
import com.finance.model.dto.CategoryBreakdown;
import com.finance.model.dto.DashboardSummary;
import com.finance.model.dto.MonthlyTrend;
import com.finance.model.dto.RecordResponse;
import com.finance.model.enums.RecordType;
import com.finance.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    @Transactional(readOnly = true)
    public DashboardSummary getSummary() {
        double income = nullToZero(recordRepository.sumAmountByType(RecordType.INCOME));
        double expense = nullToZero(recordRepository.sumAmountByType(RecordType.EXPENSE));
        return DashboardSummary.builder()
                .totalIncome(income)
                .totalExpenses(expense)
                .netBalance(income - expense)
                .build();
    }

    @Transactional(readOnly = true)
    public List<CategoryBreakdown> getCategoryBreakdown() {
        return recordRepository.sumAmountByCategory().stream()
                .map(row -> CategoryBreakdown.builder()
                        .category((String) row[0])
                        .totalAmount(((Number) row[1]).doubleValue())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MonthlyTrend> getMonthlyTrends() {
        return recordRepository.sumIncomeExpenseByMonth().stream()
                .map(row -> MonthlyTrend.builder()
                        .month((String) row[0])
                        .income(((Number) row[1]).doubleValue())
                        .expense(((Number) row[2]).doubleValue())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecordResponse> getRecentActivity(int limit) {
        int n = Math.min(50, Math.max(1, limit));
        List<FinancialRecord> rows = recordRepository.findRecentActive(PageRequest.of(0, n));
        return rows.stream().map(this::toRecordResponse).toList();
    }

    private double nullToZero(Double v) {
        return v == null ? 0.0 : v;
    }

    private RecordResponse toRecordResponse(FinancialRecord r) {
        return RecordResponse.builder()
                .id(r.getId())
                .amount(r.getAmount())
                .type(r.getType())
                .category(r.getCategory())
                .date(r.getEntryDate())
                .description(r.getDescription())
                .createdByName(r.getCreatedBy().getName())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
