package com.tradingengine.orderservice.marketdata.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "marketdata")
public class MarketData {
    @Id
    @GeneratedValue
    private String id;
    private String ticker;
    private int sellLimit;
    private double lastTradedPrice;
    private double maxPriceShift;
    private double askPrice;
    private double bidPrice;
    private int buyLimit;
    private String exchangeUrl;

}

