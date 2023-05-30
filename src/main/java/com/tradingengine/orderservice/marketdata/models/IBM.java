package com.tradingengine.orderservice.marketdata.models;

import org.springframework.data.elasticsearch.annotations.Document;


@Document(indexName = "ibm")
public class IBM extends Product{
    public IBM() {
        super();
    }

    public IBM(String product, int quantity, double price, String side, String orderType, String exchangeUrl) {
        super(product, quantity, price, side, orderType, exchangeUrl);
    }
}
