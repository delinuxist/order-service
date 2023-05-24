package com.tradingengine.orderservice.dto;

import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderType;

import java.util.List;
import java.util.UUID;

public record OrderStatusResponseDto(
        String product,
        Integer quantity,
        Double price,
        UUID orderID,
        OrderSide side,
        OrderType orderType,
        List<Executions> executions,
        Integer cumulatitiveQuantity,
        Double cumulatitivePrice
) {
}
