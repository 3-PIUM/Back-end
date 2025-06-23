package project.global.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Slf4j
public class ElasticSearchConfig {

    @Value("${elasticsearch.search.uris}")
    private String searchUri;

    @Value("${elasticsearch.logs.uris}")
    private String logsUri;

    // 상품 검색용 Elasticsearch 클라이언트 Bean 등록
    @Bean(name = "searchElasticsearchClient")
    @Primary
    public ElasticsearchClient searchElasticsearchClient() {
        return createElasticsearchClient(searchUri);
    }

    // 로그 분석용 Elasticsearch 클라이언트 Bean 등록
    @Bean(name = "logsElasticsearchClient")
    public ElasticsearchClient logsElasticsearchClient() {
        return createElasticsearchClient(logsUri);
    }

    // 주어진 URL로 Elasticsearch 클라이언트를 생성하는 공통 메서드
    private ElasticsearchClient createElasticsearchClient(String url) {
        // HTTP 기반 REST 클라이언트 생성
        RestClient restClient = RestClient.builder(HttpHost.create(url)).build();
        // JSON 처리 방식 지정
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        // 최종 Elasticsearch 클라이언트 생성
        return new ElasticsearchClient(transport);
    }
}
