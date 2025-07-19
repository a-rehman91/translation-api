package com.digitaltolk.translationapi.service;

import com.digitaltolk.translationapi.dto.PagedResponse;
import com.digitaltolk.translationapi.dto.TranslationRequest;
import com.digitaltolk.translationapi.dto.TranslationSearchRequest;
import com.digitaltolk.translationapi.dto.TranslationResponse;
import com.digitaltolk.translationapi.entity.Tag;
import com.digitaltolk.translationapi.entity.Translation;
import com.digitaltolk.translationapi.repository.TagRepository;
import com.digitaltolk.translationapi.repository.TranslationRepository;
import com.digitaltolk.translationapi.service.impl.TranslationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TranslationServiceUnitTest {

    @Mock
    private TranslationRepository translationRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TranslationServiceImpl translationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTranslations_shouldSaveAndReturnResponses() {

        TranslationRequest request = new TranslationRequest();
        request.setKey("welcome");
        request.setLocale("en");
        request.setValue("Welcome");
        UUID tagId = UUID.randomUUID();
        request.setTagIds(Set.of(tagId));

        Tag tag = Tag.builder().id(tagId).name("web").build();

        when(tagRepository.findAllById(any())).thenReturn(List.of(tag));
        when(translationRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        List<TranslationResponse> result = translationService.createTranslations(List.of(request));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKey()).isEqualTo("welcome");
        assertThat(result.get(0).getTags()).containsExactly("web");
    }

    @Test
    void getTranslation_existingId_shouldReturnResponse() {
        UUID id = UUID.randomUUID();
        Translation translation = Translation.builder()
                .id(id).key("greet").locale("en").value("Hello")
                .tags(Set.of(Tag.builder().name("web").build()))
                .build();

        when(translationRepository.findWithTagsById(id)).thenReturn(Optional.of(translation));

        TranslationResponse response = translationService.getTranslation(id);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getKey()).isEqualTo("greet");
    }

    @Test
    void getTranslation_nonExistingId_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(translationRepository.findWithTagsById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> translationService.getTranslation(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Translation not found");
    }

    @Test
    void getAllTranslations_shouldReturnPagedResponse() {
        Translation t = Translation.builder()
                .id(UUID.randomUUID()).key("test").locale("en").value("Test")
                .tags(Set.of(Tag.builder().name("t1").build()))
                .build();
        Page<Translation> page = new PageImpl<>(List.of(t), PageRequest.of(0, 1), 1);

        when(translationRepository.findAll(any(Pageable.class))).thenReturn(page);

        var response = translationService.getAllTranslations(PageRequest.of(0, 1));

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void deleteTranslation_shouldRemoveTagsAndDelete() {
        UUID id = UUID.randomUUID();
        Translation t = Translation.builder().id(id).build();

        when(translationRepository.findById(id)).thenReturn(Optional.of(t));

        translationService.deleteTranslation(id);

        verify(translationRepository).removeAllTagsFromTranslation(id);
        verify(translationRepository).deleteById(id);
    }

    @Test
    void updateTranslation_shouldUpdateAndReturn() {
        UUID id = UUID.randomUUID();
        Translation existing = Translation.builder().id(id).key("k1").locale("en").value("v1").tags(new HashSet<>()).build();
        TranslationRequest request = new TranslationRequest();
        request.setKey("k2");
        request.setLocale("fr");
        request.setValue("Bonjour");
        request.setTagIds(Set.of());

        when(translationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(translationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TranslationResponse updated = translationService.updateTranslation(id, request);

        assertThat(updated.getKey()).isEqualTo("k2");
        assertThat(updated.getLocale()).isEqualTo("fr");
    }

    @Test
    void search_shouldReturnPageOfResponses() {
        Translation t = Translation.builder()
                .id(UUID.randomUUID()).key("greet").locale("en").value("Hello")
                .tags(Set.of(Tag.builder().name("ui").build()))
                .build();
        Page<Translation> page = new PageImpl<>(List.of(t));
        TranslationSearchRequest req = new TranslationSearchRequest();
        req.setPage(0);
        req.setSize(10);

        when(translationRepository.searchWithFilters(
                any(), any(), any(), any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), any())
        ).thenReturn(page);

        Page<TranslationResponse> result = translationService.search(req);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void exportTranslations_shouldGroupByLocale() {
        List<Translation> data = List.of(
                Translation.builder().key("k1").value("v1").locale("en").build(),
                Translation.builder().key("k2").value("v2").locale("fr").build(),
                Translation.builder().key("k3").value("v3").locale("en").build()
        );

        when(translationRepository.findAll()).thenReturn(data);

        Map<String, Map<String, String>> result = translationService.exportTranslations();

        assertThat(result.get("en")).hasSize(2);
        assertThat(result.get("fr")).hasSize(1);
    }

    @Test
    void createTranslations_shouldSkipInvalidTagIds() {
        UUID invalidTagId = UUID.randomUUID();
        TranslationRequest request = new TranslationRequest();
        request.setKey("sample");
        request.setLocale("en");
        request.setValue("text");
        request.setTagIds(Set.of(invalidTagId));

        when(tagRepository.findAllById(any())).thenReturn(Collections.emptyList());
        when(translationRepository.saveAll(any())).thenReturn(Collections.emptyList());

        List<TranslationResponse> result = translationService.createTranslations(List.of(request));

        assertThat(result).isEmpty();
    }

    @Test
    void getAllTranslations_shouldReturnEmptyPage_whenPageExceedsTotal() {
        Pageable pageable = PageRequest.of(10, 10); // assume only a few records exist
        Page<Translation> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(translationRepository.findAll(pageable)).thenReturn(emptyPage);

        PagedResponse<TranslationResponse> result = translationService.getAllTranslations(pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPageNumber()).isEqualTo(10);
    }

    @Test
    void search_shouldReturnEmptyPage_whenFiltersDoNotMatch() {
        TranslationSearchRequest request = new TranslationSearchRequest();
        request.setKeys(List.of("nonexistent"));
        request.setPage(0);
        request.setSize(10);

        when(translationRepository.searchWithFilters(
                any(), any(), any(), any(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), any()
        )).thenReturn(Page.empty());

        Page<TranslationResponse> result = translationService.search(request);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void deleteTranslation_shouldThrow_whenTranslationNotFound() {
        UUID id = UUID.randomUUID();
        when(translationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> translationService.deleteTranslation(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Translation not found");
    }
}
