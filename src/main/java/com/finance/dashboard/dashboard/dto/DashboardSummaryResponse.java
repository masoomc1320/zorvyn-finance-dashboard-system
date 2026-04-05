package com.finance.dashboard.dashboard.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class DashboardSummaryResponse {

    BigDecimal totalIncome;
    BigDecimal totalExpense;
    BigDecimal net;
    LocalDate from;
    LocalDate to;
}
