package backend.profile.domain;

import backend.global.domain.SoftDeletedDomain;
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
import backend.region.domain.Character;

@Entity
@Table(name = "profile_characters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileCharacter extends SoftDeletedDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_characters_id")
    private Long profileCharacterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profiles_id", nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "characters_id", nullable = false)
    private Character character;

    @Column(name = "is_selected", nullable = false)
    private boolean isSelected;

    public ProfileCharacter(Profile profile, Character character) {
        this.profile = profile;
        this.character = character;
        this.isSelected = false;
    }

    public void addProfileCharacters(Profile profile) {
        this.profile = profile;
    }
}
