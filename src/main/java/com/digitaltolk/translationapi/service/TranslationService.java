package com.digitaltolk.translationapi.service;

import com.digitaltolk.translationapi.dto.PagedResponse;
import com.digitaltolk.translationapi.dto.TranslationRequest;
import com.digitaltolk.translationapi.dto.TranslationResponse;
import com.digitaltolk.translationapi.dto.TranslationSearchRequest;
import com.digitaltolk.translationapi.entity.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface TranslationService {
//    Translation createTranslation(Translation translation);
    List<TranslationResponse> createTranslations(List<TranslationRequest> translations);
    PagedResponse<TranslationResponse> getAllTranslations(Pageable pageable);
    TranslationResponse getTranslation(UUID id);
    void deleteTranslation(UUID id);
    TranslationResponse updateTranslation(UUID id, TranslationRequest translationRequest);
    Page<TranslationResponse> search(TranslationSearchRequest translationSearchRequest);
    Map<String, Map<String, String>> exportTranslations();
}