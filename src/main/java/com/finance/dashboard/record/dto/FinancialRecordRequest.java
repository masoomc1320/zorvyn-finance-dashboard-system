package com.finance.dashboard.record.dto;

import com.finance.dashboard.record.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinancialRecordRequest {

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    @NotBlank
    @Size(max = 100)
    private String category;

    @NotNull
    private LocalDate recordDate;

    @Size(max = 2000)
    private String notes;

    /**
     * When set, the record is created for this user. Only {@code ADMIN} may set this.
     */
    private Long targetUserId;
}
