package com.tradingengine.orderservice.marketdata.models;


public record TradeInfo(
     String ticker,
     int sellLimit,
     double  lastTradedPrice,
     double maxPriceShift,
     double askPrice,
     double bidPrice,
     int buyLimit,
     String exchangeUrl
)
{}

