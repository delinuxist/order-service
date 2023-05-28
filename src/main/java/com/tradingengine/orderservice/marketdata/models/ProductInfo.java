package com.tradingengine.orderservice.marketdata.models;

import lombok.*;


public record ProductInfo(
     String TICKER,
     int SELL_LIMIT,
     double  LAST_TRADED_PRICE,
     double MAX_PRICE_SHIFT,
     double ASK_PRICE,
     double BID_PRICE,
     int BUY_LIMIT
){}

//public class ProductInfo {
//    private String TICKER;
//    private int SELL_LIMIT;
//    private double  LAST_TRADED_PRICE;
//    private double MAX_PRICE_SHIFT;
//    private double ASK_PRICE;
//    private double BID_PRICE;
//    private int BUY_LIMIT;
//}
