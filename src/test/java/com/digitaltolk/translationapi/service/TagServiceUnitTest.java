package com.digitaltolk.translationapi.service;

import com.digitaltolk.translationapi.dto.TagRequest;
import com.digitaltolk.translationapi.dto.TagResponse;
import com.digitaltolk.translationapi.entity.Tag;
import com.digitaltolk.translationapi.repository.TagRepository;
import com.digitaltolk.translationapi.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TagServiceUnitTest {

    private TagRepository tagRepository;
    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        tagRepository = mock(TagRepository.class);
        tagService = new TagServiceImpl(tagRepository);
    }

    @Test
    void testCreateTags_savesNonExistingTags() {
        List<TagRequest> requests = List.of(
                TagRequest.builder()
                        .id(null)
                        .name("tag1")
                        .build(),
                TagRequest.builder()
                        .id(null)
                        .name("tag2")
                        .build()
        );

        when(tagRepository.findAll()).thenReturn(List.of(Tag.builder().name("tag2").build()));
        when(tagRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<TagResponse> responses = tagService.createTags(requests);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("tag1");

        ArgumentCaptor<List<Tag>> captor = ArgumentCaptor.forClass(List.class);
        verify(tagRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).extracting(Tag::getName).containsExactly("tag1");
    }

    @Test
    void testGetAllTags_returnsAllTags() {
        List<Tag> tags = List.of(
                Tag.builder().id(UUID.randomUUID()).name("tag1").build(),
                Tag.builder().id(UUID.randomUUID()).name("tag2").build()
        );
        when(tagRepository.findAll()).thenReturn(tags);

        List<TagResponse> responses = tagService.getAllTags();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(TagResponse::getName).containsExactlyInAnyOrder("tag1", "tag2");
    }

    @Test
    void testGetTag_existingId_returnsTag() {
        UUID id = UUID.randomUUID();
        Tag tag = Tag.builder().id(id).name("web").build();
        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));

        TagResponse response = tagService.getTag(id);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("web");
    }

    @Test
    void testGetTag_nonExistingId_throwsException() {
        UUID id = UUID.randomUUID();
        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.getTag(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Tag not found");
    }

    @Test
    void testDeleteTag_existingId_deletesAndReturnsTrue() {
        UUID id = UUID.randomUUID();
        when(tagRepository.existsById(id)).thenReturn(true);

        boolean result = tagService.deleteTag(id);

        assertThat(result).isTrue();
        verify(tagRepository).deleteById(id);
    }

    @Test
    void testDeleteTag_nonExistingId_returnsFalse() {
        UUID id = UUID.randomUUID();
        when(tagRepository.existsById(id)).thenReturn(false);

        boolean result = tagService.deleteTag(id);

        assertThat(result).isFalse();
        verify(tagRepository, never()).deleteById(any());
    }

    @Test
    void testUpdateTag_existingId_updatesAndReturnsResponse() {
        UUID id = UUID.randomUUID();
        Tag existingTag = Tag.builder().id(id).name("old").build();
        when(tagRepository.findById(id)).thenReturn(Optional.of(existingTag));
        when(tagRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TagRequest updateRequest = TagRequest.builder().name("new").build();

        TagResponse response = tagService.updateTag(id, updateRequest);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("new");
    }

    @Test
    void testUpdateTag_nonExistingId_throwsException() {
        UUID id = UUID.randomUUID();
        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.updateTag(id, TagRequest.builder().name("name").build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Tag not found");
    }

}
