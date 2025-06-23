package project;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import project.global.elasticsearch.service.ItemElasticsearchIndexer;

@SpringBootApplication
@EnableJpaAuditing
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class BackEndApplication implements ApplicationRunner {

    private final ItemElasticsearchIndexer indexer;

    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 애플리케이션 시작 시 한 번만 CSV 인덱싱 실행
        indexer.indexItemsFromCSV("itemData.csv");
    }

}
