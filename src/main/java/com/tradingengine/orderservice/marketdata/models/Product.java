package com.tradingengine.orderservice.marketdata.models;

import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderType;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Product {
    private String product;
    private Integer quantity;
    private Double price;
    private OrderSide side;
    private OrderType orderType;
    private String exchangeUrl;

}