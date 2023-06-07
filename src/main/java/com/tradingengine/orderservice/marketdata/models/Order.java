package com.tradingengine.orderservice.marketdata.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Order {
    private String product;
    private Integer quantity;
    private String side;
    private String orderType;
    private String exchangeUrl;
}
