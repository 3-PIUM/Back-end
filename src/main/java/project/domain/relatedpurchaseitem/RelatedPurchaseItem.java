package project.domain.relatedpurchaseitem;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.domain.common.BaseEntity;
import project.global.enums.skin.SkinType;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatedPurchaseItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String customerSegment;

    @Enumerated(EnumType.STRING)
    private SkinType skinType;

    private Long itemId;

    private Long relatedItemId;

}
