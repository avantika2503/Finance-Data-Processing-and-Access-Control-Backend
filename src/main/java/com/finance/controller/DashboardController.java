package com.finance.controller;

import com.finance.model.dto.CategoryBreakdown;
import com.finance.model.dto.DashboardSummary;
import com.finance.model.dto.MonthlyTrend;
import com.finance.model.dto.RecordResponse;
import com.finance.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummary summary() {
        return dashboardService.getSummary();
    }

    @GetMapping("/category-breakdown")
    public List<CategoryBreakdown> categoryBreakdown() {
        return dashboardService.getCategoryBreakdown();
    }

    @GetMapping("/monthly-trends")
    public List<MonthlyTrend> monthlyTrends() {
        return dashboardService.getMonthlyTrends();
    }

    @GetMapping("/recent-activity")
    public List<RecordResponse> recentActivity(
            @RequestParam(defaultValue = "10") int limit) {
        return dashboardService.getRecentActivity(limit);
    }
}
