package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.entity.StockEntity;

import java.util.List;

public interface StockService {
    StockEntity fetchStockByPortfolioAndTicker(PortfolioEntity portfolio, String ticker);

    void saveStock(StockEntity stock);

}
