package com.finance.dashboard.record;

import com.finance.dashboard.record.dto.FinancialRecordResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FinancialRecordMapper {

    public static FinancialRecordResponse toResponse(FinancialRecord entity) {
        return FinancialRecordResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .amount(entity.getAmount())
                .type(entity.getType())
                .category(entity.getCategory())
                .recordDate(entity.getRecordDate())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
