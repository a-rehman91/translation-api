package com.digitaltolk.translationapi.service.impl;

import com.digitaltolk.translationapi.dto.PagedResponse;
import com.digitaltolk.translationapi.dto.TranslationRequest;
import com.digitaltolk.translationapi.dto.TranslationResponse;
import com.digitaltolk.translationapi.dto.TranslationSearchRequest;
import com.digitaltolk.translationapi.entity.Tag;
import com.digitaltolk.translationapi.entity.Translation;
import com.digitaltolk.translationapi.repository.TagRepository;
import com.digitaltolk.translationapi.repository.TranslationRepository;
import com.digitaltolk.translationapi.service.TranslationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final TranslationRepository translationRepository;
    private final TagRepository tagRepository;

//    @Override
//    public Translation createTranslation(Translation translation) {
//        return translationRepository.save(translation);
//    }

    @Override
    public List<TranslationResponse> createTranslations(List<TranslationRequest> translationRequests) {

        List<Translation> translations = new ArrayList<>();

        for (TranslationRequest transaction : translationRequests) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(transaction.getTagIds()));
            Translation translation = Translation.builder()
                    .key(transaction.getKey())
                    .locale(transaction.getLocale())
                    .value(transaction.getValue())
                    .tags(tags)
                    .build();
            translations.add(translation);
        }

        return translationRepository.saveAll(translations)
                .stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

//    @Override
//    public List<TranslationResponse> getAllTranslations() {
//
//        return translationRepository.findAll()
//                .stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//    }

    @Override
    public PagedResponse<TranslationResponse> getAllTranslations(Pageable pageable) {
        Page<Translation> page = translationRepository.findAll(pageable);

        List<TranslationResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return PagedResponse.<TranslationResponse>builder()
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(content)
                .hasMore(page.hasNext())
                .build();
    }

    @Override
    public TranslationResponse getTranslation(UUID id) {
        Optional<Translation> byId = translationRepository.findWithTagsById(id);

        return translationRepository.findWithTagsById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Translation not found"));
    }

    @Override
    @Transactional
    public void deleteTranslation(UUID id) {

        Translation translation = translationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Translation not found"));

        translationRepository.removeAllTagsFromTranslation(id);
        translationRepository.deleteById(id);
    }

    @Override
    public TranslationResponse updateTranslation(UUID id, TranslationRequest translationRequest){

        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(translationRequest.getTagIds()));

        return translationRepository.findById(id)
                .map(translation -> {
                    translation.setKey(translationRequest.getKey());
                    translation.setLocale(translationRequest.getLocale());
                    translation.setValue(translationRequest.getValue());
                    translation.setTags(tags);
                    return  translationRepository.save(translation);
                })
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Translation not found"));
    }

    // todo: filtering in application level
//    @Override
//    public List<TranslationResponse> search(List<String> keys,
//                                            List<String> values,
//                                            List<String> locales,
//                                            Set<String> tags) {
//
//        List<Translation> filteredTranslation = translationRepository.findAll().stream()
//                .filter(t -> keys == null || keys.contains(t.getKey()))
//                .filter(t -> values == null || values.contains(t.getValue()))
//                .filter(t -> locales == null || locales.contains(t.getLocale()))
//                .filter(t -> {
//                    if (tags == null || tags.isEmpty()) return true;
//                    Set<String> translationTagNames = t.getTags().stream().map(Tag::getName).collect(Collectors.toSet());
////                    return translationTagNames.containsAll(tags); // AND logic
//                    return tags.stream().anyMatch(translationTagNames::contains); // OR logic
//                })
//                .toList();
//
//        return filteredTranslation.stream()
//                .map(this::toResponse)
//                .toList();
//    }

    // todo: filtering in db level
    @Override
    public Page<TranslationResponse> search(TranslationSearchRequest translationSearchRequest) {

        Pageable pageable = PageRequest.of(translationSearchRequest.getPage(), translationSearchRequest.getSize());
        List<String> keys = translationSearchRequest.getKeys();
        List<String> values = translationSearchRequest.getValues();
        List<String> locales = translationSearchRequest.getLocales();
        Set<String> tags = translationSearchRequest.getTags();

        Page<Translation> translations = translationRepository.searchWithFilters(
                keys != null && !keys.isEmpty() ? keys : null,
                values != null && !values.isEmpty() ? values : null,
                locales != null && !locales.isEmpty() ? locales : null,
                tags != null && !tags.isEmpty() ? tags : null,
                keys == null || keys.isEmpty(),
                values == null || values.isEmpty(),
                locales == null || locales.isEmpty(),
                tags == null || tags.isEmpty(),
                pageable
        );

//        Page<Translation> translations = translationRepository.searchWithFilters(
//                translationSearchRequest.getKeys(),
//                translationSearchRequest.getValues(),
//                translationSearchRequest.getLocales(),
//                translationSearchRequest.getTags(),
//                pageable
//        );

        return translations.map(this::toResponse);
    }

    @Override
    public Map<String, Map<String, String>> exportTranslations() {
        List<Translation> allTranslations = translationRepository.findAll();

        Map<String, Map<String, String>> export = new HashMap<>();

        for (Translation t : allTranslations) {
            export.computeIfAbsent(t.getLocale(), k -> new HashMap<>())
                    .put(t.getKey(), t.getValue());
        }

        return export;
    }

//    private List<Translation> searchByKey(String key) {
//
//        return translationRepository.findByKeyContainingIgnoreCase(key);
//    }
//
//    private List<Translation> searchByValue(String value) {
//        return translationRepository.findByValueContainingIgnoreCase(value);
//    }
//
//    private List<Translation> getByLocale(String locale) {
//        return translationRepository.findByLocale(locale);
//    }
//
//    private List<Translation> searchByTags(Set<String> tagNames) {
//        Set<Tag> tags = new HashSet<>(tagRepository.findAllByNameIn(tagNames));
//        if (tags.isEmpty()) return Collections.emptyList();
//
//        return translationRepository.findAll().stream()
//                .filter(t -> t.getTags().containsAll(tags))
//                .toList();
//    }
//
//    private List<Translation> searchByTagsAndLocale(Set<String> tagNames, String locale) {
//        Set<Tag> tags = new HashSet<>(tagRepository.findAllByNameIn(tagNames));
//        if (tags.isEmpty()) return Collections.emptyList();
//
//        return translationRepository.findAll().stream()
//                .filter(t -> locale.equalsIgnoreCase(t.getLocale()) && t.getTags().containsAll(tags))
//                .toList();
//    }

    private TranslationResponse toResponse(Translation translation) {

        System.out.println("Tags size: " + translation.getTags().size());

        Set<Tag> tags = translation.getTags(); // loaded already by EntityGraph

        Set<String> tagNames = tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        return TranslationResponse.builder()
                .id(translation.getId())
                .key(translation.getKey())
                .locale(translation.getLocale())
                .value(translation.getValue())
                .tags(tagNames)
                .build();
    }
}