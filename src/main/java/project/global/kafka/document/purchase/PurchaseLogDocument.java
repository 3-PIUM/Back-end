package project.global.kafka.document.purchase;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import project.domain.member.enums.Area;
import project.domain.member.enums.Gender;
import project.global.enums.skin.PersonalType;
import project.global.enums.skin.SkinType;
import project.global.kafka.dto.KafkaMetadata;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

// ElasticSearch 문서 모델
@Document(indexName = "purchase-logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseLogDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long memberId;

    @Field(type = FieldType.Date)
    private LocalDate birth;

    @Field(type = FieldType.Keyword)
    private Gender gender;

    @Field(type = FieldType.Keyword)
    private Area area;

    @Field(type = FieldType.Keyword)
    private PersonalType personalType;

    @Field(type = FieldType.Keyword)
    private SkinType skinType;

    @Field(type = FieldType.Keyword)
    private List<String> skinIssues;

    @Field(type = FieldType.Long)
    private List<Long> cartItemIds;

    @Field(type = FieldType.Long)
    private List<Long> purchaseItemIds;

    @Field(type = FieldType.Boolean)
    private boolean success;

    @Field(type = FieldType.Text)
    private String errorMessage;

    @Field(type = FieldType.Date)
    private long timestamp;

    @Field(type = FieldType.Long)
    private long processingTimeMs;

    @Field(type = FieldType.Object)
    private KafkaMetadata kafkaMetadata;

    @Field(type = FieldType.Date)
    private Instant indexedAt;
}
