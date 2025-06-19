package project.global.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMetadata {
    private String topic;
    private int partition;
    private long offset;
    private Instant consumedAt;
}
