package com.tradingengine.orderservice.dto;

import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class OrderStatusResponseDto {
    String product;

    Integer quantity;

    Double price;

    UUID orderIdFromExchange;

    OrderSide orderSide;

    OrderType orderType;

    List<Executions> executions;

    Integer cumulatitiveQuantity;

    Double cumulatitivePrice;


}
