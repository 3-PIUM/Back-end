package project.domain.purchasehistory.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class PurchaseHistoryResponse {


    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DateInfoListDTO{
        private LocalDate date;
        private List<String> imgUrlList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InfoListDTO{
        private List<DateInfoListDTO> dateInfoList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailInfoListDTO{
        private List<DetailInfoDTO> detailInfoList;
        private Integer totalPrice;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailInfoDTO{
        private Long itemId;
        private String itemName;
        private String imgUrl;
        private Integer price;
        private Integer quantity;
    }

}
