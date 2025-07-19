package com.digitaltolk.translationapi.repository;

import com.digitaltolk.translationapi.entity.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, UUID> {

    @EntityGraph(attributePaths = {"tags"})
    Optional<Translation> findWithTagsById(UUID id);
    @Modifying
    @Query(value = "DELETE FROM translation_tag WHERE translation_id = :translationId", nativeQuery = true)
    void removeAllTagsFromTranslation(@Param("translationId") UUID translationId);

    @Query("""
    SELECT DISTINCT t FROM Translation t
    LEFT JOIN t.tags tag
    WHERE (:keysNull = TRUE OR t.key IN :keys)
      AND (:valuesNull = TRUE OR t.value IN :values)
      AND (:localesNull = TRUE OR t.locale IN :locales)
      AND (:tagsNull = TRUE OR tag.name IN :tagNames)
    """)
    @EntityGraph(attributePaths = {"tags"})
    Page<Translation> searchWithFilters(
            @Param("keys") List<String> keys,
            @Param("values") List<String> values,
            @Param("locales") List<String> locales,
            @Param("tagNames") Set<String> tagNames,
            @Param("keysNull") boolean keysNull,
            @Param("valuesNull") boolean valuesNull,
            @Param("localesNull") boolean localesNull,
            @Param("tagsNull") boolean tagsNull,
            Pageable pageable
    );
}