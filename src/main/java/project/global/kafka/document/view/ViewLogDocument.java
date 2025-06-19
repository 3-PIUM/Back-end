package project.global.kafka.document.view;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import project.global.kafka.dto.KafkaMetadata;

import java.time.Instant;

// ElasticSearch 문서 모델
@Document(indexName = "view-logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewLogDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long memberId;

    @Field(type = FieldType.Long)
    private Long itemId;

    @Field(type = FieldType.Date)
    private long timestamp;

    @Field(type = FieldType.Object)
    private KafkaMetadata kafkaMetadata;

    @Field(type = FieldType.Date)
    private Instant indexedAt;
}
