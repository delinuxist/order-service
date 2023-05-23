package com.tradingengine.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(
        @NotNull
        String product,
        @NotNull
        @Min(value = 1)
        Integer quantity,
        @NotNull
        Double price,
        @NotNull
        String side,
        @NotNull
        String type
) {
}
