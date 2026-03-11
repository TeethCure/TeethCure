package com.example.backend.region.domain;

import com.example.backend.global.domain.SoftDeletedDomain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "regions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region extends SoftDeletedDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regions_id")
    private Long regionId;

    @Column(name = "regions_name", nullable = false)
    private String regionName;

    @Column(name = "total_characters", nullable = false)
    private Integer totalCharacterNumber;

    @Column(name = "main_story_url", nullable = false)
    private String mainStoryUrl;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    public Region(String regionName, Integer totalCharacterNumber, String mainStoryUrl,
            Integer orderNumber) {
        this.regionName = regionName;
        this.totalCharacterNumber = totalCharacterNumber;
        this.mainStoryUrl = mainStoryUrl;
        this.orderNumber = orderNumber;
    }
}
