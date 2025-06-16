package project.domain.purchasehistory.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import project.domain.purchasehistory.PurchaseHistory;
import project.domain.purchasehistory.dto.PurchaseHistoryResponse.DateInfoListDTO;
import project.domain.purchasehistory.dto.PurchaseHistoryResponse.DetailInfoDTO;
import project.domain.purchasehistory.dto.PurchaseHistoryResponse.DetailInfoListDTO;
import project.domain.purchasehistory.dto.PurchaseHistoryResponse.HistoryDTO;
import project.domain.purchasehistory.dto.PurchaseHistoryResponse.InfoListDTO;

public abstract class PurchaseHistoryConverter {

    private static DateInfoListDTO toDateInfoList(
        List<HistoryDTO> historys,
        LocalDate localDate
    ) {
        return DateInfoListDTO.builder()
            .date(localDate)
            .historys(historys)
            .build();
    }

    public static InfoListDTO toInfoListDTO(
        Map<LocalDate, List<HistoryDTO>> groupedByDate
    ) {
        List<DateInfoListDTO> dateInfoList = groupedByDate.entrySet().stream()
            .map(entry -> toDateInfoList(entry.getValue(), entry.getKey()))
            .collect(Collectors.toList());

        return InfoListDTO.builder()
            .dateInfoList(dateInfoList)
            .build();
    }

    public static DetailInfoDTO toDetailInfoDTO(PurchaseHistory purchaseHistory) {
        return DetailInfoDTO.builder()
            .itemId(purchaseHistory.getItemId())
            .itemName(purchaseHistory.getItemName())
            .quantity(purchaseHistory.getQuantity())
            .price(purchaseHistory.getPrice())
            .imgUrl(purchaseHistory.getImgUrl())
            .itemOption(purchaseHistory.getItemOption())
            .build();
    }

    public static DetailInfoListDTO toDetailInfoListDTO(List<PurchaseHistory> list) {
        List<DetailInfoDTO> detailinfoList = list.stream()
            .map(PurchaseHistoryConverter::toDetailInfoDTO)
            .toList();

        int totalPrice = list.stream().mapToInt(ph -> ph.getPrice() * ph.getQuantity()).sum();

        return DetailInfoListDTO.builder()
            .totalPrice(totalPrice)
            .detailInfoList(detailinfoList)
            .build();
    }

}
