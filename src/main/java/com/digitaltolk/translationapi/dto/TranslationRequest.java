package com.digitaltolk.translationapi.dto;

import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Data
public class TranslationRequest {
    private String key;
    private String locale;
    private String value;
    private Set<UUID> tagIds;
}