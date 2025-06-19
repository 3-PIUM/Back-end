package project.global.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers; // kafka 주소

    // 구매 로그용 Producer (신뢰성 우선)
    @Bean("purchaseProducerFactory")
    public ProducerFactory<String, String> purchaseProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // key값 String 직렬화
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // value값 String 직렬화

        // 신뢰성 최우선 설정
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // 모든 replica 확인
        props.put(ProducerConfig.RETRIES_CONFIG, 10); // 메세지 발행 실패시 10번 재시도
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000); // 재시도 간격 1초
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 중복 방지
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1); // 순서 보장
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 작은 배치(빠른 전송) - 메세지가 쌓여 16KB가 되면 한번에 메세지 전송
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1); // 대기 시간 - 배치 크기만큼 메세지가 안쌓여도 1ms가 지나면 메세지를 전송
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000); // 2분 타임아웃

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean("purchaseKafkaTemplate")
    public KafkaTemplate<String, String> purchaseKafkaTemplate() {
        KafkaTemplate<String, String> template = new KafkaTemplate<>(purchaseProducerFactory());
        template.setDefaultTopic("purchase-events"); // sendDefault 사용했을때 자동으로 토픽이 purchase-logs가 됨
        return template;
    }


    // 조회 로그용 Prodcuer(처리량 우선)
    @Bean("viewProducerFactory")
    public ProducerFactory<String, String> viewProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // 처리량 최우선 설정
        props.put(ProducerConfig.ACKS_CONFIG, "1"); // 리더만 확인
        props.put(ProducerConfig.RETRIES_CONFIG, 1); // 1번만 재시도
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 65536); // 메세지가 64KB 쌓이면 전송
        props.put(ProducerConfig.LINGER_MS_CONFIG, 50); // 50ms가 지나면 메세지가 64KB 쌓이지 않아도 전송
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4"); // 빠른 압축
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 134217728); // 버퍼 크기 128MB
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // 하나의 브로커가 동시에 5개 메세지를 받을 수 있음

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean("viewKafkaTemplate")
    public KafkaTemplate<String, String> viewKafkaTemplate() {
        KafkaTemplate<String, String> template = new KafkaTemplate<>(viewProducerFactory());
        template.setDefaultTopic("view-events");
        return template;
    }
}
