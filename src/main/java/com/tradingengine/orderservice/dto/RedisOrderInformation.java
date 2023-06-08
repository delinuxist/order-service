package com.tradingengine.orderservice.dto;


import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderType;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisOrderInformation  {
    private String product;
    private Integer quantity;
    private Double price;
    private OrderSide side;
    private OrderType type;
    private UUID portfolioId;
    private UUID userId;

}
