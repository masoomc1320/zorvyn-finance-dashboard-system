package com.finance.dashboard.dashboard.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class TrendPointResponse {

    String period;
    BigDecimal income;
    BigDecimal expense;
    BigDecimal net;
}
