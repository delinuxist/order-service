package com.tradingengine.orderservice.marketdata.models;

import lombok.*;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public  class Trade {

    private  String product;
    private  int quantity;
    private  double price;
    private  String side;
    private  String orderType;
    private  String exchangeUrl;

}