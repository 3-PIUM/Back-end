package project.global.redis.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public abstract class PopularWeekItemsDTO {

    @Data
    @AllArgsConstructor
    public static class ViewStats {
        private int totalViewCount;
        private int memberId;
    }

    @Data
    @AllArgsConstructor
    public static class PurchaseStats {
        private int totalPurchaseCount;
        private int memberId;
    }

    @Data
    @Builder
    public static class ConversionRateDTO {
        private Long itemId;
        private int totalViews;           // 총 조회 수
        private int uniqueViewers;        // 고유 조회자 수
        private int totalPurchases;       // 총 구매 수
        private int uniqueBuyers;         // 고유 구매자 수
        private double userConversionRate;   // 사용자 기반 전환율
        private double eventConversionRate;  // 이벤트 기반 전환율
    }
}
