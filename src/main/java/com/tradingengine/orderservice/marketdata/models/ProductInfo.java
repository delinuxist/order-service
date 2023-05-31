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

