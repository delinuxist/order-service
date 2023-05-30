package com.tradingengine.orderservice.marketdata.models;

import org.springframework.data.elasticsearch.annotations.Document;



@Document(indexName = "google")
public class Google extends Product{
    public Google() {
        super();
    }

    public Google(String product, int quantity, double price, String side, String orderType, String exchangeUrl) {
        super(product, quantity, price, side, orderType, exchangeUrl);
    }
}