package com.finance.service;

import com.finance.exception.AppException;
import com.finance.model.FinancialRecord;
import com.finance.model.User;
import com.finance.model.dto.CreateRecordRequest;
import com.finance.model.dto.RecordResponse;
import com.finance.model.dto.UpdateRecordRequest;
import com.finance.model.enums.RecordType;
import com.finance.repository.FinancialRecordRepository;
import com.finance.repository.FinancialRecordSpecification;
import com.finance.repository.UserRepository;
import com.finance.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    @Transactional
    public RecordResponse createRecord(CreateRecordRequest request) {
        Long userId = currentUserId();
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "User not found"));
        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory().trim())
                .entryDate(request.getDate())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .isDeleted(false)
                .createdBy(creator)
                .build();
        return toResponse(recordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public Page<RecordResponse> getRecords(
            RecordType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {
        int p = Math.max(0, page);
        int s = Math.min(100, Math.max(1, size));
        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "entryDate", "createdAt"));
        Specification<FinancialRecord> spec = FinancialRecordSpecification.activeWithFilters(type, category, startDate, endDate);
        return recordRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public RecordResponse getRecordById(Long id) {
        FinancialRecord record = recordRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Record not found"));
        return toResponse(record);
    }

    @Transactional
    public RecordResponse updateRecord(Long id, UpdateRecordRequest request) {
        if (request.getAmount() == null && request.getType() == null && request.getCategory() == null
                && request.getDate() == null && request.getDescription() == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "At least one field must be provided for update");
        }
        FinancialRecord record = recordRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Record not found"));
        if (request.getAmount() != null) {
            record.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            record.setType(request.getType());
        }
        if (request.getCategory() != null) {
            record.setCategory(request.getCategory().trim());
        }
        if (request.getDate() != null) {
            record.setEntryDate(request.getDate());
        }
        if (request.getDescription() != null) {
            record.setDescription(request.getDescription().trim());
        }
        return toResponse(recordRepository.save(record));
    }

    @Transactional
    public void deleteRecord(Long id) {
        FinancialRecord record = recordRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Record not found"));
        record.setDeleted(true);
        recordRepository.save(record);
    }

    private Long currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal up) {
            return up.getId();
        }
        throw new AppException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }

    private RecordResponse toResponse(FinancialRecord r) {
        return RecordResponse.builder()
                .id(r.getId())
                .amount(r.getAmount())
                .type(r.getType())
                .category(r.getCategory())
                .date(r.getEntryDate())
                .description(r.getDescription())
                .createdByName(r.getCreatedBy().getName())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
