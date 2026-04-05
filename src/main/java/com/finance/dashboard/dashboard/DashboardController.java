package com.finance.dashboard.dashboard;

import com.finance.dashboard.dashboard.dto.CategoryTotalResponse;
import com.finance.dashboard.dashboard.dto.DashboardSummaryResponse;
import com.finance.dashboard.dashboard.dto.TrendPointResponse;
import com.finance.dashboard.record.dto.FinancialRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Aggregated metrics for the authenticated user. VIEWER and ANALYST can read summaries; record CRUD remains role
 * gated elsewhere.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    public DashboardSummaryResponse summary(
            @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to) {
        return dashboardService.summary(from, to);
    }

    @GetMapping("/by-category")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    public List<CategoryTotalResponse> byCategory(
            @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to) {
        return dashboardService.byCategory(from, to);
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    public List<FinancialRecordResponse> recent(@RequestParam(defaultValue = "10") int limit) {
        return dashboardService.recent(limit);
    }

    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    public List<TrendPointResponse> trends(
            @RequestParam TrendGranularity granularity,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return dashboardService.trends(granularity, from, to);
    }
}
