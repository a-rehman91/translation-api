package com.digitaltolk.translationapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class TranslationSearchRequest {
    private List<String> keys;
    private List<String> values;
    private List<String> locales;
    private Set<String> tags;
    @Min(0)
    private int page = 0;
    @Min(1)
    @Max(1000)
    private int size = 50;
}