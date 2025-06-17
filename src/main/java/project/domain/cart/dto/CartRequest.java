package project.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class CartRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddItemDTO {
        private String itemOption = "default";
        @NotNull
        @Min(1)
        private Integer quantity;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateOptionDTO {
        private String changeOption;
    }
}