package com.digitaltolk.translationapi.controller;

import com.digitaltolk.translationapi.dto.PagedResponse;
import com.digitaltolk.translationapi.dto.TranslationRequest;
import com.digitaltolk.translationapi.dto.TranslationResponse;
import com.digitaltolk.translationapi.dto.TranslationSearchRequest;
import com.digitaltolk.translationapi.repository.TagRepository;
import com.digitaltolk.translationapi.service.TranslationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/translations")
@RequiredArgsConstructor
@Validated
public class TranslationController {

    private final TranslationService translationService;
    private final TagRepository tagRepository;

    @GetMapping("/status")
    public String test(){
        return "working...";
    }

    @PostMapping
    public ResponseEntity<List<TranslationResponse>> create(@RequestBody List<TranslationRequest> translationRequests) {

        return ResponseEntity.ok(translationService.createTranslations(translationRequests));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TranslationResponse>> getAllTranslations(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(1000) int size
    ) {

        return ResponseEntity.ok(translationService.getAllTranslations(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TranslationResponse> getTranslation(@PathVariable UUID id) {

        return ResponseEntity.ok(translationService.getTranslation(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        translationService.deleteTranslation(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TranslationResponse> update(@PathVariable UUID id, @RequestBody TranslationRequest translationRequest) {

        return ResponseEntity.ok(translationService.updateTranslation(id, translationRequest));
    }

    @PostMapping("/search")
    public ResponseEntity<PagedResponse<TranslationResponse>> search(@Valid @RequestBody TranslationSearchRequest translationSearchRequest) {

        Page<TranslationResponse> page = translationService.search(translationSearchRequest);
        PagedResponse<TranslationResponse> response = PagedResponse.<TranslationResponse>builder()
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(page.getContent())
                .hasMore(page.hasNext())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    public ResponseEntity<Map<String, Map<String, String>>> exportTranslations() {
        return ResponseEntity.ok(translationService.exportTranslations());
    }
}