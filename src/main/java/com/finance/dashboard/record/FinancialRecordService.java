package com.finance.dashboard.record;

import com.finance.dashboard.common.exception.ResourceNotFoundException;
import com.finance.dashboard.record.dto.FinancialRecordRequest;
import com.finance.dashboard.record.dto.FinancialRecordResponse;
import com.finance.dashboard.security.SecurityUtils;
import com.finance.dashboard.security.UserDetailsImpl;
import com.finance.dashboard.user.RoleName;
import com.finance.dashboard.user.User;
import com.finance.dashboard.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    /**
     * ADMIN: full CRUD on all records. ANALYST: read/filter own records. VIEWER: no access to this API (enforced via
     * {@code @PreAuthorize}).
     */
    @Transactional
    public FinancialRecordResponse create(FinancialRecordRequest request) {
        UserDetailsImpl current = SecurityUtils.requireCurrentUser();
        Long ownerId = current.getId();
        if (request.getTargetUserId() != null) {
            if (!current.getRoles().contains(RoleName.ADMIN)) {
                throw new AccessDeniedException("Only ADMIN can assign records to another user");
            }
            ownerId = request.getTargetUserId();
        }
        User owner = userRepository
                .findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner user not found"));
        Instant now = Instant.now();
        FinancialRecord entity = FinancialRecord.builder()
                .user(owner)
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory().trim())
                .recordDate(request.getRecordDate())
                .notes(request.getNotes())
                .createdAt(now)
                .updatedAt(now)
                .build();
        return FinancialRecordMapper.toResponse(financialRecordRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<FinancialRecordResponse> list(
            java.time.LocalDate from,
            java.time.LocalDate to,
            String category,
            TransactionType type,
            Pageable pageable) {
        UserDetailsImpl me = SecurityUtils.requireCurrentUser();
        Page<FinancialRecord> page;
        if (me.getRoles().contains(RoleName.ADMIN)) {
            page = financialRecordRepository.findAllFiltered(from, to, category, type, pageable);
        } else {
            page = financialRecordRepository.findFiltered(me.getId(), from, to, category, type, pageable);
        }
        return page.map(FinancialRecordMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public FinancialRecordResponse getById(Long id) {
        FinancialRecord entity = loadAuthorizedRead(id);
        return FinancialRecordMapper.toResponse(entity);
    }

    @Transactional
    public FinancialRecordResponse update(Long id, FinancialRecordRequest request) {
        FinancialRecord entity = financialRecordRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        entity.setAmount(request.getAmount());
        entity.setType(request.getType());
        entity.setCategory(request.getCategory().trim());
        entity.setRecordDate(request.getRecordDate());
        entity.setNotes(request.getNotes());
        entity.setUpdatedAt(Instant.now());
        if (request.getTargetUserId() != null) {
            User newOwner = userRepository
                    .findById(request.getTargetUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner user not found"));
            entity.setUser(newOwner);
        }
        return FinancialRecordMapper.toResponse(financialRecordRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        FinancialRecord entity = financialRecordRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        financialRecordRepository.delete(entity);
    }

    private FinancialRecord loadAuthorizedRead(Long id) {
        UserDetailsImpl me = SecurityUtils.requireCurrentUser();
        if (me.getRoles().contains(RoleName.ADMIN)) {
            return financialRecordRepository
                    .findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        }
        return financialRecordRepository
                .findByIdAndUser_Id(id, me.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
    }
}
