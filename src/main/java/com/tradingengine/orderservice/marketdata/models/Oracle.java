package com.tradingengine.orderservice.marketdata.models;

import org.springframework.data.elasticsearch.annotations.Document;



@Document(indexName = "oracle")
public class Oracle extends Product{
    public Oracle() {
        super();
    }

    public Oracle(String product, int quantity, double price, String side, String orderType, String exchangeUrl) {
        super(product, quantity, price, side, orderType, exchangeUrl);
    }
}