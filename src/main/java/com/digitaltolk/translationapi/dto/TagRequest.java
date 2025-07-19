package com.digitaltolk.translationapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TagRequest {
    private UUID id;
    private String name;
}