package com.tradingengine.orderservice.dto;

import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderType;
import jakarta.validation.constraints.Min;
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
        OrderSide side,
        @NotNull
        OrderType type
) {
}
