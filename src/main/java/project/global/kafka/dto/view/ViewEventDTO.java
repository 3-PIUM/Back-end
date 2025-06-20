package project.global.kafka.dto.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewEventDTO {
    private Long memberId;
    private Long itemId;
    private String subCategory;
    private long eventTime;
}
