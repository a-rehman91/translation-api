package com.digitaltolk.translationapi.controller;

import com.digitaltolk.translationapi.dto.TagRequest;
import com.digitaltolk.translationapi.dto.TagResponse;
import com.digitaltolk.translationapi.entity.Tag;
import com.digitaltolk.translationapi.repository.TagRepository;
import com.digitaltolk.translationapi.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagRepository tagRepository;

    @PostMapping
    public ResponseEntity<List<TagResponse>> createTags(@RequestBody List<TagRequest> tagRequests) {

        return ResponseEntity.ok(tagService.createTags(tagRequests));
    }

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {

        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getTag(@PathVariable UUID id) {

        return ResponseEntity.ok(tagService.getTag(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id) {

        if(tagService.deleteTag(id)) return ResponseEntity.noContent().build();

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponse> updateTag(@PathVariable UUID id, @RequestBody TagRequest tagRequest) {

        return ResponseEntity.ok(tagService.updateTag(id, tagRequest));
    }
}