package com.digitaltolk.translationapi.seed;

import com.digitaltolk.translationapi.entity.Tag;
import com.digitaltolk.translationapi.entity.Translation;
import com.digitaltolk.translationapi.repository.TagRepository;
import com.digitaltolk.translationapi.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final TranslationRepository translationRepository;
    private final TagRepository tagRepository;

    public void run() {
        if (translationRepository.count() >= 100000) {
            System.out.println("Translations already seeded.");
            return;
        }

        List<Tag> tags = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            tags.add(tagRepository.save(Tag.builder().name("tag" + i).build()));
        }

        List<Translation> translations = new ArrayList<>();
        for (int i = 1; i <= 100000; i++) {
            Translation t = Translation.builder()
                    .key("key" + i)
                    .locale(i % 2 == 0 ? "en" : "fr")
                    .value("value" + i)
                    .tags(Set.of(tags.get(i % tags.size())))
                    .build();
            translations.add(t);

            // batch insert every 1000 records
            if (i % 1000 == 0) {
                translationRepository.saveAll(translations);
                translations.clear();
            }
        }

        // save any remaining
        if (!translations.isEmpty()) {
            translationRepository.saveAll(translations);
        }

        System.out.println("100k translations seeded successfully.");
    }
}
