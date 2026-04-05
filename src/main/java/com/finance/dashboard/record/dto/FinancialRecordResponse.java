package com.finance.dashboard.record.dto;

import com.finance.dashboard.record.TransactionType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Value
@Builder
public class FinancialRecordResponse {

    Long id;
    Long userId;
    BigDecimal amount;
    TransactionType type;
    String category;
    LocalDate recordDate;
    String notes;
    Instant createdAt;
    Instant updatedAt;
}
