package com.tradingengine.orderservice.marketdata.models;

import org.springframework.data.elasticsearch.annotations.Document;


@Document(indexName = "netflix")
public class Netflix extends Product{

    public Netflix() {
        super();
    }

    public Netflix(String product, int quantity, double price, String side, String orderType, String exchangeUrl) {
        super(product, quantity, price, side, orderType, exchangeUrl);
    }
}