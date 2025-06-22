package project.global.kafka.dto.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.domain.member.enums.Area;
import project.domain.member.enums.Gender;
import project.global.enums.skin.PersonalType;
import project.global.enums.skin.SkinType;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseEventDTO {
    private Long memberId;
    private LocalDate birth;
    private Gender gender;
    private Area area;
    private SkinType skinType;
    private List<String> skinIssues;
    private PersonalType personalType;
    private List<Long> cartItemIds;
    private List<Long> purchaseItemIds;
    private long eventTime;
}
