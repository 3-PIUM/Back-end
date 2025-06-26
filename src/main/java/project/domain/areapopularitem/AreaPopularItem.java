package project.domain.areapopularitem;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.domain.member.enums.Area;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaPopularItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long itemId;

    @Enumerated(EnumType.STRING)
    private Area area;
}
