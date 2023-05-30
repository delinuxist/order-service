package com.tradingengine.orderservice.marketdata.models;

import org.springframework.data.elasticsearch.annotations.Document;



@Document(indexName = "microsoft")
public class Microsoft extends Product{
    public Microsoft() {
        super();
    }

    public Microsoft(String product, int quantity, double price, String side, String orderType, String exchangeUrl) {
        super(product, quantity, price, side, orderType, exchangeUrl);
    }
}
