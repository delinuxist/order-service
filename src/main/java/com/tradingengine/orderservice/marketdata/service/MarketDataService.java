package com.tradingengine.orderservice.marketdata.service;

import com.tradingengine.orderservice.marketdata.models.Trade;
import com.tradingengine.orderservice.marketdata.models.TradeInfo;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public interface MarketDataService {
    Stream<TradeInfo> getProductByTicker(String ticker) throws IOException;
    Stream<Trade> findOpenTrades(String product, String side) throws IOException;
    Stream<Trade> findOpenTrades(String product, String side, String orderType, String exchangeOne) throws IOException;

    Stream<Trade> findOpenTrades(String product, String side, String orderType) throws IOException;

    List<Trade> findOpenTrades(String product) throws IOException;

}
