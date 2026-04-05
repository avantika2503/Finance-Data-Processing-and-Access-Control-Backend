package com.finance.model.dto;

import com.finance.model.enums.RecordType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class RecordResponse {

    private Long id;
    private Double amount;
    private RecordType type;
    private String category;
    private LocalDate date;
    private String description;
    private String createdByName;
    private Instant createdAt;
    private Instant updatedAt;
}
