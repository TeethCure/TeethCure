package com.example.backend.profile.domain;

import com.example.backend.account.domain.Account;
import com.example.backend.global.domain.SoftDeletedDomain;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends SoftDeletedDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profiles_id")
    private Long profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounts_id", nullable = false)
    private Account account;

    @Column(name = "name", nullable = false)
    private String profileName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "last_brushing_at")
    private LocalDateTime lastBrushingAt;

    @Column(name = "is_tutorial_done", nullable = false)
    private boolean isTutorialDone;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile", cascade = CascadeType.PERSIST)
    private List<ProfileCharacter> profileCharacters;

    public Profile(Account account, String profileName, LocalDate birthDate) {
        this.account = account;
        this.profileName = profileName;
        this.birthDate = birthDate;
        this.lastBrushingAt = null;
        this.isTutorialDone = false;
        this.profileCharacters = new ArrayList<>();
    }

    public void addProfileCharacters(ProfileCharacter profileCharacter) {
        profileCharacters.add(profileCharacter);
        profileCharacter.addProfileCharacters(this);
    }
}
