package com.tradingengine.orderservice.marketdata.models;

import org.springframework.data.elasticsearch.annotations.Document;



@Document(indexName = "amazon")
public class Amazon extends Product{
    public Amazon() {
        super();
    }


    public Amazon(String product, int quantity, double price, String side, String orderType, String exchangeUrl) {
        super(product, quantity, price, side, orderType, exchangeUrl);
    }
}