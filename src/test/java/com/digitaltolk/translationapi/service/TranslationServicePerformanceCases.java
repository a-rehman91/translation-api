package com.digitaltolk.translationapi.service;

import com.digitaltolk.translationapi.dto.TranslationRequest;
import com.digitaltolk.translationapi.dto.TranslationSearchRequest;
import com.digitaltolk.translationapi.entity.Tag;
import com.digitaltolk.translationapi.repository.TagRepository;
import com.digitaltolk.translationapi.repository.TranslationRepository;
import com.digitaltolk.translationapi.service.impl.TranslationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TranslationServicePerformanceCases {

    private TranslationRepository translationRepository;
    private TagRepository tagRepository;
    private TranslationServiceImpl translationService;

    @BeforeEach
    void setUp() {
        translationRepository = mock(TranslationRepository.class);
        tagRepository = mock(TagRepository.class);
        translationService = new TranslationServiceImpl(translationRepository, tagRepository);
    }

    @Test
    void searchPerformance_shouldExecuteUnder500ms() {
        TranslationSearchRequest request = new TranslationSearchRequest();
        request.setPage(0);
        request.setSize(10);
        request.setKeys(List.of("key1"));
        request.setLocales(List.of("en"));
        request.setTags(Set.of("web"));

        when(translationRepository.searchWithFilters(
                any(), any(), any(), any(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0));

        long start = System.nanoTime();
        translationService.search(request);
        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        System.out.println("Search duration: " + durationMs + "ms");
        assertThat(durationMs).isLessThan(500);
    }

    @Test
    void exportPerformance_shouldExecuteUnder500ms() {
        when(translationRepository.findAll()).thenReturn(List.of());

        long start = System.nanoTime();
        translationService.exportTranslations();
        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        System.out.println("Export duration: " + durationMs + "ms");
        assertThat(durationMs).isLessThan(500);
    }

    @Test
    void createTranslationsPerformance_shouldExecuteUnder1000ms() {
        List<TranslationRequest> requests = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            TranslationRequest req = new TranslationRequest();
            req.setKey("key" + i);
            req.setLocale("en");
            req.setValue("value" + i);
            req.setTagIds(Set.of(UUID.randomUUID()));
            requests.add(req);
        }

        when(tagRepository.findAllById(any())).thenReturn(List.of());
        when(translationRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        long start = System.nanoTime();
        translationService.createTranslations(requests);
        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        System.out.println("Create duration: " + durationMs + "ms");
        assertThat(durationMs).isLessThan(500);
    }
}
