package project.global.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import project.global.elasticsearch.document.ItemDocument;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemElasticsearchIndexer {

    // 상품 검색용 Elasticsearch 클라이언트 주입
    @Qualifier("searchElasticsearchClient")
    private final ElasticsearchClient searchClient;

    /**
     * CSV 파일을 읽어 Elasticsearch에 인덱싱하는 메서드
     *
     * @param fileName 리소스 폴더 내 CSV 파일 이름 (예: "items.csv")
     */
    public void indexItemsFromCSV(String fileName) {
        try {
            // CSV 파싱을 위한 설정: 헤더 포함
            CsvMapper csvMapper = new CsvMapper();
            // csv파일의 1번째 행을 스키마 값으로 설정
            CsvSchema schema = CsvSchema.emptySchema().withHeader();

            // 클래스패스에서 CSV 파일 불러오기 (resources 폴더 기준)
            InputStream inputStream = new ClassPathResource("datas/" + fileName).getInputStream();

            // CSV 내용을 ItemDocument 객체로 매핑
            MappingIterator<ItemDocument> iterator = csvMapper
                    .readerFor(ItemDocument.class)
                    .with(schema)
                    .readValues(inputStream);

            // 파싱된 객체들을 리스트로 수집
            List<ItemDocument> items = new ArrayList<>();
            while (iterator.hasNext()) {
                items.add(iterator.next());
            }

            // Elasticsearch에 벌크 인덱싱
            bulkIndex(items);

            log.info("✅ {}개 아이템 인덱싱 완료", items.size());

        } catch (Exception e) {
            log.error("❌ CSV 인덱싱 실패", e);
        }
    }

    /**
     * Elasticsearch에 벌크 인덱싱 수행
     *
     * @param items 인덱싱할 아이템 리스트
     */
    private void bulkIndex(List<ItemDocument> items) throws Exception {
        BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();

        for (ItemDocument item : items) {
            bulkBuilder.operations(op -> op
                    .index(idx -> idx
                            .index("items") // 인덱스 이름
                            .id(String.valueOf(item.getId())) // 도큐먼트 ID 설정
                            .document(item) // 저장할 실제 데이터
                    )
            );
        }

        // Elasticsearch에 요청 전송
        BulkResponse response = searchClient.bulk(bulkBuilder.build());

        // 에러 발생 여부 로그
        if (response.errors()) {
            log.warn("일부 인덱싱 실패");
        }
    }
}