package com.tradingengine.orderservice.marketdata.models;

import org.springframework.data.elasticsearch.annotations.Document;



@Document(indexName = "tesla")
public class Tesla extends Product{
    public Tesla() {
        super();
    }

    public Tesla(String product, int quantity, double price, String side, String orderType, String exchangeUrl) {
        super(product, quantity, price, side, orderType, exchangeUrl);
    }
}