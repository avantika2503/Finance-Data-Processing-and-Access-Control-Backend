package com.finance.controller;

import com.finance.model.dto.CreateRecordRequest;
import com.finance.model.dto.RecordResponse;
import com.finance.model.dto.UpdateRecordRequest;
import com.finance.model.enums.RecordType;
import com.finance.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecordResponse create(@Valid @RequestBody CreateRecordRequest request) {
        return recordService.createRecord(request);
    }

    @GetMapping
    public Page<RecordResponse> list(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return recordService.getRecords(type, category, startDate, endDate, page, size);
    }

    @GetMapping("/{id}")
    public RecordResponse getOne(@PathVariable Long id) {
        return recordService.getRecordById(id);
    }

    @PutMapping("/{id}")
    public RecordResponse update(@PathVariable Long id, @Valid @RequestBody UpdateRecordRequest request) {
        return recordService.updateRecord(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        recordService.deleteRecord(id);
    }
}
