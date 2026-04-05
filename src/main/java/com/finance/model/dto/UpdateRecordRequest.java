package com.finance.model.dto;

import com.finance.model.enums.RecordType;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateRecordRequest {

    @Positive(message = "Amount must be positive")
    private Double amount;

    private RecordType type;

    private String category;

    private LocalDate date;

    private String description;
}
