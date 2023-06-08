package com.tradingengine.orderservice.dto;

import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLegResponseDto {

    UUID orderLegId;
    String IdFromExchange;
    private String product;
    private Double price;
    private Integer quantity;
    private OrderSide orderSide;
    private OrderType type;
    private OrderStatus orderLegStatus;

    private String exchangeUrl;
}
