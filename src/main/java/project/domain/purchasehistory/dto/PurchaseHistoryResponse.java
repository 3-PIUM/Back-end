package project.domain.purchasehistory.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class PurchaseHistoryResponse {


    /*
        날짜 별 구매내역 전체 보기
     */
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DateInfoListDTO {

        private LocalDate date;
        private List<HistoryDTO> historys;
    }

    /*
        구매내역 전체 보기 이미지 Url과 아이템 Id
     */
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HistoryDTO {

        private Long id;
        private String imgUrl;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InfoListDTO {

        private List<DateInfoListDTO> dateInfoList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailInfoListDTO {

        private List<DetailInfoDTO> detailInfoList;
        private Integer totalPrice;
    }


    /*
        구매내약 상세보기 아이템 정보
     */
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailInfoDTO {

        private Long itemId;
        private String itemName;
        private String imgUrl;
        private Integer price;
        private Integer quantity;
        private String itemOption;
    }

}
