package com.digitaltolk.translationapi.service;

import com.digitaltolk.translationapi.dto.TagRequest;
import com.digitaltolk.translationapi.dto.TagResponse;
import com.digitaltolk.translationapi.entity.Tag;
import com.digitaltolk.translationapi.repository.TagRepository;
import com.digitaltolk.translationapi.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


//Create tags with duplicate filtering
//Fetch all tags
//Get tag by ID (positive & negative)
//Delete tag (exists / not exists)
//Update tag (exists / not exists)

@ExtendWith(MockitoExtension.class)
public class TagServiceFeatureTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private UUID tagId;
    private Tag tag;
    private TagRequest tagRequest;

    @BeforeEach
    void setUp() {
        tagId = UUID.randomUUID();
        tag = Tag.builder().id(tagId).name("tag1").build();
        tagRequest = TagRequest.builder().id(tagId).name("tag1").build();
    }

    @Test
    void createTags_shouldFilterDuplicatesAndSaveNewTags() {
        List<TagRequest> tagRequests = List.of(tagRequest);

        when(tagRepository.findAll()).thenReturn(Collections.emptyList());
        when(tagRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Tag> toSave = invocation.getArgument(0);
            toSave.get(0).setId(tagId); // simulate DB ID generation
            return toSave;
        });

        List<TagResponse> responses = tagService.createTags(tagRequests);

        assertEquals(1, responses.size());
        assertEquals("tag1", responses.get(0).getName());
    }

    @Test
    void getAllTags_shouldReturnAllTagsAsResponses() {
        when(tagRepository.findAll()).thenReturn(List.of(tag));

        List<TagResponse> responses = tagService.getAllTags();

        assertEquals(1, responses.size());
        assertEquals(tagId, responses.get(0).getId());
        assertEquals("tag1", responses.get(0).getName());
    }

    @Test
    void getTag_shouldReturnTagResponse_whenFound() {
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        TagResponse response = tagService.getTag(tagId);

        assertEquals(tagId, response.getId());
        assertEquals("tag1", response.getName());
    }

    @Test
    void getTag_shouldThrowException_whenNotFound() {
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tagService.getTag(tagId));
        assertEquals("Tag not found", ex.getMessage());
    }

    @Test
    void deleteTag_shouldReturnTrue_whenExists() {
        when(tagRepository.existsById(tagId)).thenReturn(true);

        boolean result = tagService.deleteTag(tagId);

        assertTrue(result);
        verify(tagRepository).deleteById(tagId);
    }

    @Test
    void deleteTag_shouldReturnFalse_whenNotExists() {
        when(tagRepository.existsById(tagId)).thenReturn(false);

        boolean result = tagService.deleteTag(tagId);

        assertFalse(result);
        verify(tagRepository, never()).deleteById(tagId);
    }

    @Test
    void updateTag_shouldUpdateAndReturnTag_whenExists() {
        Tag updatedTag = Tag.builder().id(tagId).name("newTag").build();
        TagRequest updateRequest = TagRequest.builder().name("newTag").build();

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(tagRepository.save(any(Tag.class))).thenReturn(updatedTag);

        TagResponse response = tagService.updateTag(tagId, updateRequest);

        assertEquals(tagId, response.getId());
        assertEquals("newTag", response.getName());
    }

    @Test
    void updateTag_shouldThrowException_whenNotFound() {
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tagService.updateTag(tagId, tagRequest));
        assertEquals("Tag not found", ex.getMessage());
    }
}
