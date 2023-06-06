package com.tradingengine.orderservice.dto;



import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class RedisOrderDto {

    private String product;
    private Integer quantity;
    private Double price;
    private OrderSide side;
    private OrderType type;
    private UUID portfolioId;
    private UUID userId;

}
