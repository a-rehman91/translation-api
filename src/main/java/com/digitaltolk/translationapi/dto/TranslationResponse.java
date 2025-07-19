package com.digitaltolk.translationapi.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class TranslationResponse {
    private UUID id;
    private String key;
    private String locale;
    private String value;
    private Set<String> tags;
}