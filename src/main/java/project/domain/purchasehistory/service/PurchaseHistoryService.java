package project.domain.purchasehistory.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.member.Member;
import project.domain.purchasehistory.PurchaseHistory;
import project.domain.purchasehistory.dto.PurchaseHistoryConverter;
import project.domain.purchasehistory.dto.PurchaseHistoryResponse.DetailInfoListDTO;
import project.domain.purchasehistory.dto.PurchaseHistoryResponse.InfoListDTO;
import project.domain.purchasehistory.repository.PurchaseHistoryRepository;
import project.global.response.ApiResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;

    /*
    구매내역 전체 조회
     */
    public ApiResponse<InfoListDTO> getPurchaseHistory(Member member) {
        List<PurchaseHistory> purchaseHistoryByMemberId = purchaseHistoryRepository.findByMemberId(
            member.getId());

        Map<LocalDate, List<String>> groupedByDate = purchaseHistoryByMemberId.stream()
            .collect(Collectors.groupingBy(
                ph-> ph.getCreatedAt().toLocalDate(),
                Collectors.mapping(PurchaseHistory::getImgUrl, Collectors.toList())
            ));

        InfoListDTO infoListDTO = PurchaseHistoryConverter.toInfoListDTO(groupedByDate);

        return ApiResponse.onSuccess(infoListDTO);
    }

    /*
    구매 내역 상세조회
     */
    public ApiResponse<DetailInfoListDTO> getPurchaseHistoryDetail(Member member, LocalDate date) {
        List<PurchaseHistory> byCreatedAtAndMemberId = purchaseHistoryRepository.findByCreatedAtAndMemberId(
            member.getId(), date);

        DetailInfoListDTO detailInfoListDTO = PurchaseHistoryConverter.toDetailInfoListDTO(
            byCreatedAtAndMemberId);

        return ApiResponse.onSuccess(detailInfoListDTO);
    }


}