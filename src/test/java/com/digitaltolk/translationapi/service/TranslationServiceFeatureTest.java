package com.digitaltolk.translationapi.service;

import com.digitaltolk.translationapi.dto.TranslationRequest;
import com.digitaltolk.translationapi.dto.TranslationResponse;
import com.digitaltolk.translationapi.dto.TranslationSearchRequest;
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

public class TranslationServiceFeatureTest {

    @Mock
    private TranslationRepository translationRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TranslationServiceImpl translationService;

    private UUID id;
    private Tag tag;
    private TranslationRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        tag = Tag.builder().id(UUID.randomUUID()).name("tag1").build();
        request = new TranslationRequest();
        request.setKey("login.title");
        request.setLocale("en");
        request.setValue("Login");
        request.setTagIds(Set.of(tag.getId()));
    }

    @Test
    void createTranslations_shouldSaveOnlyUniqueKeyLocale() {
        when(tagRepository.findAllById(any())).thenReturn(List.of(tag));
        when(translationRepository.saveAll(anyList()))
                .thenAnswer(inv -> inv.getArgument(0));

        List<TranslationRequest> list = List.of(request);
        List<TranslationResponse> result = translationService.createTranslations(list);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKey()).isEqualTo("login.title");
    }

    @Test
    void getTranslation_shouldReturnTranslation_whenExists() {
        Translation translation = Translation.builder()
                .id(id)
                .key("login.title")
                .locale("en")
                .value("Login")
                .tags(Set.of(tag))
                .build();

        when(translationRepository.findWithTagsById(id)).thenReturn(Optional.of(translation));

        TranslationResponse response = translationService.getTranslation(id);

        assertThat(response.getKey()).isEqualTo("login.title");
    }

    @Test
    void getTranslation_shouldThrowException_whenNotFound() {
        when(translationRepository.findWithTagsById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> translationService.getTranslation(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Translation not found");
    }

    @Test
    void deleteTranslation_shouldDeleteTranslationAndUnlinkTags() {
        Translation translation = Translation.builder().id(id).build();

        when(translationRepository.findById(id)).thenReturn(Optional.of(translation));

        translationService.deleteTranslation(id);

        verify(translationRepository).removeAllTagsFromTranslation(id);
        verify(translationRepository).deleteById(id);
    }

    @Test
    void updateTranslation_shouldUpdateWithNewValues() {
        Translation translation = Translation.builder().id(id).key("old").locale("en").value("old").tags(Set.of()).build();

        when(translationRepository.findById(id)).thenReturn(Optional.of(translation));
        when(tagRepository.findAllById(any())).thenReturn(List.of(tag));
        when(translationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TranslationResponse updated = translationService.updateTranslation(id, request);

        assertThat(updated.getKey()).isEqualTo("login.title");
        assertThat(updated.getTags()).contains("tag1");
    }

    @Test
    void updateTranslation_shouldThrow_whenIdNotFound() {
        when(translationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> translationService.updateTranslation(id, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Translation not found");
    }

    @Test
    void search_shouldReturnPaginatedResults() {
        TranslationSearchRequest searchRequest = new TranslationSearchRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(5);
        searchRequest.setKeys(List.of("login.title"));
        searchRequest.setLocales(List.of("en"));
        searchRequest.setTags(Set.of("tag1"));

        Translation translation = Translation.builder()
                .id(id)
                .key("login.title")
                .locale("en")
                .value("Login")
                .tags(Set.of(tag))
                .build();

        Page<Translation> page = new PageImpl<>(List.of(translation));
        when(translationRepository.searchWithFilters(any(), any(), any(), any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(page);

        Page<TranslationResponse> result = translationService.search(searchRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getKey()).isEqualTo("login.title");
    }

    @Test
    void exportTranslations_shouldReturnGroupedMap() {

        Translation t1 = Translation.builder().key("login.title").locale("en").value("Login").build();
        Translation t2 = Translation.builder().key("login.title").locale("fr").value("Connexion").build();
        when(translationRepository.findAll()).thenReturn(List.of(t1, t2));

        Map<String, Map<String, String>> result = translationService.exportTranslations();

        assertThat(result).containsKeys("en", "fr");
        assertThat(result.get("en")).containsEntry("login.title", "Login");
    }
}
