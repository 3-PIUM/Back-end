package project.global.kafka.service.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import project.global.kafka.document.view.ViewLogDocument;
import project.global.kafka.dto.KafkaMetadata;
import project.global.kafka.dto.view.ViewEventDTO;

import java.time.Instant;

// ElasticSearch 저장 서비스
@Service
@Slf4j
@RequiredArgsConstructor
public class ViewLogService {

    private final ElasticsearchTemplate elasticsearchTemplate;

    public void saveViewLog(ViewEventDTO viewEventDTO, String topic, int partition, long offset) {
        try {
            // ElasticSearch에 저장할 로그 문서 생성
            ViewLogDocument viewLogDocument = createViewLog(viewEventDTO, topic, partition, offset);

            // ElasticSearch에 저장
            String documentId = generateDocumentId(viewEventDTO, topic);
            viewLogDocument.setId(documentId);
            elasticsearchTemplate.save(viewLogDocument);

            log.info("ElasticSearch 조회 로그 저장 성공: documentId={}, userId={}, documentId={}",
                    documentId, viewEventDTO.getMemberId(), documentId);


        } catch (Exception e) {
            log.error("ElasticSearch 조회 로그 저장 실패: userId={}, topic={}, partition={}, offset={}",
                    viewEventDTO.getMemberId(), topic, partition, offset);
        }
    }

    private ViewLogDocument createViewLog(ViewEventDTO viewEventDTO, String topic, int partition, long offset) {
        return ViewLogDocument.builder()
                .memberId(viewEventDTO.getMemberId())
                .itemId(viewEventDTO.getItemId())
                .timestamp(viewEventDTO.getEventTime())
                .kafkaMetadata(KafkaMetadata.builder()
                        .topic(topic)
                        .partition(partition)
                        .offset(offset)
                        .consumedAt(Instant.now())
                        .build())
                .indexedAt(Instant.now())
                .build();
    }

    private String generateDocumentId(ViewEventDTO dto, String topic) {
        return String.format("%s_%d_%d_%d",
                topic, dto.getEventTime(), dto.getMemberId(), dto.getItemId());
    }
}

