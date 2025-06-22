package project.global.kafka.service.purchase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import project.global.kafka.document.purchase.PurchaseLogDocument;
import project.global.kafka.dto.KafkaMetadata;
import project.global.kafka.dto.purchase.PurchaseEventDTO;

import java.time.Instant;

// ElasticSearch 저장 서비스
@Service
@Slf4j
public class PurchaseLogService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public void savePurchaseLog(PurchaseEventDTO purchaseEventDTO, String topic, int partition, long offset) {
        try {
            // ElasticSearch에 저장할 로그 문서 생성
            PurchaseLogDocument purchaseLogDocument = createPurchaseLog(purchaseEventDTO, topic, partition, offset);

            // ElasticSearch에 저장
            String documentId = generateDocumentId(purchaseEventDTO, topic, partition, offset);
            purchaseLogDocument.setId(documentId);
            elasticsearchTemplate.save(purchaseLogDocument);

            log.info("ElasticSearch 구매 로그 저장 성공: documentId={}, userId={}, documentId={}",
                    documentId, purchaseEventDTO.getMemberId(), documentId);


        } catch (Exception e) {
            log.error("ElasticSearch 구래 로그 저장 실패: userId={}, topic={}, partition={}, offset={}",
                    purchaseEventDTO.getMemberId(), topic, partition, offset);

            saveToBackupStorage(purchaseEventDTO, topic, partition, offset, e);
        }
    }

    private PurchaseLogDocument createPurchaseLog(PurchaseEventDTO purchaseEventDTO, String topic, int partition, long offset) {
        return PurchaseLogDocument.builder()
                .memberId(purchaseEventDTO.getMemberId())
                .birth(purchaseEventDTO.getBirth())
                .gender(purchaseEventDTO.getGender())
                .area(purchaseEventDTO.getArea())
                .personalType(purchaseEventDTO.getPersonalType())
                .skinType(purchaseEventDTO.getSkinType())
                .skinIssues(purchaseEventDTO.getSkinIssues())
                .cartItemIds(purchaseEventDTO.getCartItemIds())
                .purchaseItemIds(purchaseEventDTO.getPurchaseItemIds())
                .timestamp(purchaseEventDTO.getEventTime())
                .kafkaMetadata(KafkaMetadata.builder()
                        .topic(topic)
                        .partition(partition)
                        .offset(offset)
                        .consumedAt(Instant.now())
                        .build())
                .indexedAt(Instant.now())
                .build();
    }

    private String generateDocumentId(PurchaseEventDTO dto, String topic, long partition, long offset) {
        return String.format("purchase_%s_%d_%d_%d_%d",
                topic, partition, offset, dto.getEventTime(), dto.getMemberId());
    }

    private void saveToBackupStorage(PurchaseEventDTO dto, String topic, int partition, long offset, Exception e) {
        log.error("백업 저장소에 구매 로그 저장: userId={}, topic={}, partition={}, offset={}",
                dto.getMemberId(), topic, partition, offset, e);
        // TODO: 파일 시스템 또는 DB에 백업 저장
    }
}
