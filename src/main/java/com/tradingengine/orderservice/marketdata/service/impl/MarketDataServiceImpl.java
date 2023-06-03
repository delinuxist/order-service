package com.tradingengine.orderservice.marketdata.service.impl;

import com.tradingengine.orderservice.marketdata.models.Trade;
import com.tradingengine.orderservice.marketdata.models.TradeInfo;
import com.tradingengine.orderservice.marketdata.repository.ElasticSearchQuery;
import com.tradingengine.orderservice.marketdata.service.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MarketDataServiceImpl implements MarketDataService {
    @Autowired
    private ElasticSearchQuery elasticSearchQuery;
    @Override
    public Stream<TradeInfo> getProductByTicker(String ticker) throws IOException {
       return elasticSearchQuery.findProductByTicker(ticker);
    }

    @Override
    public Stream<Trade> findOpenTrades(String product, String side) throws IOException {
        return elasticSearchQuery.findOrders(product, side);
    }

    @Override
    public Stream<Trade> findOpenTrades(String product, String side, String orderType, String exchangeUrl) throws IOException {
        return elasticSearchQuery.findOrders(product, side, orderType, exchangeUrl);
    }

    @Override
    public Stream<Trade> findOpenTrades(String product, String side, String orderType) throws IOException {
        return elasticSearchQuery.findOrders(product, side, orderType);
    }

    @Override
    public List<Trade> findOpenTrades(String product) throws IOException {
        return elasticSearchQuery.findOrders(product);
    }


}
