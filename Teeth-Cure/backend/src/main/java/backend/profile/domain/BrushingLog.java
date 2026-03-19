package backend.profile.domain;

import backend.global.domain.BaseDomain;
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
@Table(name = "brushing_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrushingLog extends BaseDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brushing_logs_id")
    private Long brushingLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profiles_id", nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "characters_id", nullable = false)
    private Character character;

    @Column(name = "is_succeed", nullable = false)
    private Boolean isSucceed;

    @Column(name = "duration_sec", nullable = false)
    private Integer durationSec;

    @Column(name = "purification_percent", nullable = false)
    private Integer purificationPercent;

    public BrushingLog(
            Profile profile,
            Character character,
            Boolean isSucceed,
            Integer durationSec,
            Integer purificationPercent
    ) {
        this.profile = profile;
        this.character = character;
        this.isSucceed = isSucceed;
        this.durationSec = durationSec;
        this.purificationPercent = purificationPercent;
    }
}
