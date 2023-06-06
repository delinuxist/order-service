package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.entity.StockEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockService {
    StockEntity fetchStockByPortfolioAndTicker(PortfolioEntity portfolio, String ticker);

    void saveStock(StockEntity stock);

    Optional<StockEntity> fetchStockByPortfolioIdAndTicker(UUID portfolioId, String ticker);

}
