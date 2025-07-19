package com.digitaltolk.translationapi.service;

import com.digitaltolk.translationapi.dto.TagRequest;
import com.digitaltolk.translationapi.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TagServicePerformanceTest {

    @Autowired
    private TagService tagService;

    private List<TagRequest> largeTagList;

    @BeforeEach
    void setUp() {
        // Generate 10,000 unique tags
        largeTagList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            largeTagList.add(TagRequest.builder().name("tag_" + i).build());
        }
    }

    @Test
    void createTags_shouldCompleteWithinTimeLimit() {
        long start = System.currentTimeMillis();

        tagService.createTags(largeTagList);

        long duration = System.currentTimeMillis() - start;
        System.out.println("Execution time: " + duration + " ms");

        assertThat(duration).isLessThan(500);
    }
}
