package com.tradingengine.orderservice.marketdata.models;

import org.springframework.data.elasticsearch.annotations.Document;


@Document(indexName = "apple")
public class Apple extends Product{
    public Apple() {
        super();
    }

    public Apple(String product, int quantity, double price, String side, String orderType, String exchangeUrl) {
        super(product, quantity, price, side, orderType, exchangeUrl);
    }
}