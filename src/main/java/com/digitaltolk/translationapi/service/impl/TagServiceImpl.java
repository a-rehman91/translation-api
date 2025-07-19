package com.digitaltolk.translationapi.service.impl;

import com.digitaltolk.translationapi.dto.TagRequest;
import com.digitaltolk.translationapi.dto.TagResponse;
import com.digitaltolk.translationapi.entity.Tag;
import com.digitaltolk.translationapi.repository.TagRepository;
import com.digitaltolk.translationapi.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<TagResponse> createTags(List<TagRequest> tagRequests) {

        Set<String> existingNames = tagRepository.findAll().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        List<Tag> toSave = tagRequests.stream()
                .filter(req -> !existingNames.contains(req.getName()))
                .map(req -> Tag.builder().name(req.getName()).build())
                .toList();

        return tagRepository.saveAll(toSave)
                .stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TagResponse getTag(UUID id) {
        return tagRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
    }

    @Override
    public boolean deleteTag(UUID id) {
        if (!tagRepository.existsById(id))
            return false;

        tagRepository.deleteById(id);
        return true;
    }

    @Override
    public TagResponse updateTag(UUID id, TagRequest tagRequest) {

        return tagRepository.findById(id)
                .map(tag -> {
                    tag.setName(tagRequest.getName());
                    return tagRepository.save(tag);
                })
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
    }

    private TagResponse toResponse(Tag tag) {

        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}