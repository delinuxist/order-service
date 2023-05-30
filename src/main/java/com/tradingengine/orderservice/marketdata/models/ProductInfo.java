package com.tradingengine.orderservice.marketdata.models;

import lombok.*;


public record ProductInfo(
     String ticker,
     int sellLimit,
     double  lastTradedPrice,
     double maxPriceShift,
     double askPrice,
     double bidPrice,
     int buyLimit
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
