package project.global.redis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.global.redis.service.popular.PopularItemsBatchService;
import project.global.response.ApiResponse;
import project.global.response.status.ErrorStatus;

@Tag(name = "레디스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
public class RedisController {

    private final PopularItemsBatchService popularItemsBatchService;

    @Operation(
            summary = "수동으로 인기 상품 배치 실행"
    )
    @PostMapping("/batch/popular-items")
    public ApiResponse<String> batchPopularItems() {
        try {
            popularItemsBatchService.calculatePopularItems();
            return ApiResponse.onSuccess("인기 상품 배치 성공");
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
