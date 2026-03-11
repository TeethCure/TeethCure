package com.example.backend.region.domain;

import com.example.backend.global.domain.SoftDeletedDomain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "characters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Character extends SoftDeletedDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "characters_id")
    private int characterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regions_id", nullable = false)
    private Region region;

    @Column(name = "characters_name", nullable = false)
    private String characterName;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "characters_story_video_url", nullable = false)
    private String characterStoryVideoUrl;

    @Column(name = "characters_main_image_url", nullable = false)
    private String characterMainImageUrl;

    public Character(
            Region region,
            String characterName,
            boolean isDefault,
            String characterStoryVideoUrl,
            String characterMainImageUrl
    ) {
        this.region = region;
        this.characterName = characterName;
        this.isDefault = isDefault;
        this.characterStoryVideoUrl = characterStoryVideoUrl;
        this.characterMainImageUrl = characterMainImageUrl;
    }
}

