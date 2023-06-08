package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.entity.StockEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockService {

    List<StockEntity> fetchAllStocksByPortfolioIdAndTicker(UUID portfolioId, String ticker);

    StockEntity findByPortfolioAndTickerAndUserId(PortfolioEntity portfolio, String ticker, UUID userId);

    List<StockEntity> fetchAllStocks();

    StockEntity fetchStockById(UUID stockId);

    void saveStock(StockEntity stock);

    StockEntity fetchStockByPortFolioAndTicker(PortfolioEntity portfolioEntity, String Ticker);

}
