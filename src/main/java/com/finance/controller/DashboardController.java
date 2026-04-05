package com.finance.controller;

import com.finance.model.dto.CategoryBreakdown;
import com.finance.model.dto.DashboardSummary;
import com.finance.model.dto.MonthlyTrend;
import com.finance.model.dto.RecordResponse;
import com.finance.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Aggregates and trends (ANALYST or ADMIN)")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Income, expenses, net balance")
    @GetMapping("/summary")
    public DashboardSummary summary() {
        return dashboardService.getSummary();
    }

    @Operation(summary = "Totals grouped by category")
    @GetMapping("/category-breakdown")
    public List<CategoryBreakdown> categoryBreakdown() {
        return dashboardService.getCategoryBreakdown();
    }

    @Operation(summary = "Monthly income vs expense (H2 native aggregation)")
    @GetMapping("/monthly-trends")
    public List<MonthlyTrend> monthlyTrends() {
        return dashboardService.getMonthlyTrends();
    }

    @Operation(summary = "Most recent non-deleted records")
    @GetMapping("/recent-activity")
    public List<RecordResponse> recentActivity(
            @RequestParam(defaultValue = "10") int limit) {
        return dashboardService.getRecentActivity(limit);
    }
}
