package project.domain.purchasehistory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.domain.member.Member;
import project.domain.purchasehistory.dto.PurchaseHistoryResponse;
import project.domain.purchasehistory.dto.PurchaseHistoryResponse.DetailInfoListDTO;
import project.domain.purchasehistory.dto.PurchaseHistoryResponse.InfoListDTO;
import project.domain.purchasehistory.service.PurchaseHistoryService;
import project.global.response.ApiResponse;
import project.global.security.annotation.LoginMember;

@Tag(name = "구매 내역 API", description = "구매 내역 관련 API 입니다.")
@RestController
@RequestMapping("/purchase-history")
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;

    @Operation(
        summary = "구매 내역 조회",
        description = "상세 구매내역을 날짜별 그룹화하고 조회합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ApiResponse<InfoListDTO> getPurchaseHistory(
        @LoginMember Member member
    ) {
        return purchaseHistoryService.getPurchaseHistory(member);
    }

    @Operation(
        summary = "상세 구매 내역 조회",
        description = "날짜 관련 상세 구매내역을 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공",
        content = @Content(schema = @Schema(implementation = PurchaseHistoryResponse.DetailInfoListDTO.class)))
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/detail")
    public ApiResponse<PurchaseHistoryResponse.DetailInfoListDTO> getPurchaseHistoryDetail(
        @LoginMember Member member,
        @RequestParam LocalDate date
    ){
        return purchaseHistoryService.getPurchaseHistoryDetail(member,date);
    }

}
