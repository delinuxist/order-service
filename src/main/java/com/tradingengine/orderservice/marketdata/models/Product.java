package com.tradingengine.orderservice.marketdata.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;




@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public  class Product {

    private  String product;
    private  int quantity;
    private  double price;
    private  String side;
    private  String orderType;
    private  String exchangeUrl;

}