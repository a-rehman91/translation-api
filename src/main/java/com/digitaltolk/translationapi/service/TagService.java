package com.digitaltolk.translationapi.service;

import com.digitaltolk.translationapi.dto.TagRequest;
import com.digitaltolk.translationapi.dto.TagResponse;
import com.digitaltolk.translationapi.entity.Tag;

import java.util.List;
import java.util.UUID;

public interface TagService {

    List<TagResponse> createTags(List<TagRequest> tags);
    List<TagResponse> getAllTags();
    TagResponse getTag(UUID id);
    boolean deleteTag(UUID id);
    TagResponse updateTag(UUID id, TagRequest tagRequest);
}
