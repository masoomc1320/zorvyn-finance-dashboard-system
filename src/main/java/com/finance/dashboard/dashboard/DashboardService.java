package com.finance.dashboard.dashboard;

import com.finance.dashboard.dashboard.dto.CategoryTotalResponse;
import com.finance.dashboard.dashboard.dto.DashboardSummaryResponse;
import com.finance.dashboard.dashboard.dto.TrendPointResponse;
import com.finance.dashboard.record.FinancialRecordMapper;
import com.finance.dashboard.record.FinancialRecordRepository;
import com.finance.dashboard.record.TransactionType;
import com.finance.dashboard.record.dto.FinancialRecordResponse;
import com.finance.dashboard.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository financialRecordRepository;

    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary(LocalDate from, LocalDate to) {
        Long userId = SecurityUtils.requireCurrentUser().getId();
        BigDecimal income = nullToZero(
                financialRecordRepository.sumAmountByUserAndType(userId, TransactionType.INCOME, from, to));
        BigDecimal expense = nullToZero(
                financialRecordRepository.sumAmountByUserAndType(userId, TransactionType.EXPENSE, from, to));
        return DashboardSummaryResponse.builder()
                .totalIncome(income)
                .totalExpense(expense)
                .net(income.subtract(expense))
                .from(from)
                .to(to)
                .build();
    }

    @Transactional(readOnly = true)
    public List<CategoryTotalResponse> byCategory(LocalDate from, LocalDate to) {
        Long userId = SecurityUtils.requireCurrentUser().getId();
        return financialRecordRepository.sumByCategory(userId, from, to).stream()
                .map(row -> CategoryTotalResponse.builder()
                        .category(Objects.toString(row[0], ""))
                        .totalAmount(toBd(row[1]))
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FinancialRecordResponse> recent(int limit) {
        Long userId = SecurityUtils.requireCurrentUser().getId();
        int safe = Math.min(Math.max(limit, 1), 100);
        var page = financialRecordRepository.findByUserIdOrderByRecordDateDescCreatedAtDesc(
                userId, PageRequest.of(0, safe, Sort.by(Sort.Direction.DESC, "recordDate", "createdAt")));
        return page.getContent().stream().map(FinancialRecordMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TrendPointResponse> trends(TrendGranularity granularity, LocalDate from, LocalDate to) {
        Long userId = SecurityUtils.requireCurrentUser().getId();
        List<Object[]> rows = switch (granularity) {
            case WEEK -> financialRecordRepository.weeklyTrendsForUser(userId, from, to);
            case MONTH -> financialRecordRepository.monthlyTrendsForUser(userId, from, to);
        };
        return rows.stream()
                .map(r -> {
                    BigDecimal inc = toBd(r[1]);
                    BigDecimal exp = toBd(r[2]);
                    return TrendPointResponse.builder()
                            .period(Objects.toString(r[0], ""))
                            .income(inc)
                            .expense(exp)
                            .net(inc.subtract(exp))
                            .build();
                })
                .toList();
    }

    private static BigDecimal nullToZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static BigDecimal toBd(Object o) {
        if (o == null) {
            return BigDecimal.ZERO;
        }
        if (o instanceof BigDecimal b) {
            return b;
        }
        if (o instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        return new BigDecimal(o.toString());
    }
}
