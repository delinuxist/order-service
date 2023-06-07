package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.entity.StockEntity;

import java.util.List;
import java.util.UUID;

public interface StockService {

    List<StockEntity> fetchAllStocksByPortfolioIdAndTicker(UUID portfolioId, String ticker);

    StockEntity fetchStockByPortfolioAndTicker(PortfolioEntity portfolio, String ticker);

    List<StockEntity> fetchAllStocks();

    StockEntity fetchStockById(UUID stockId);

    void saveStock(StockEntity stock);

    StockEntity fetchStockByPortFolioAndTicker(PortfolioEntity portfolioEntity, String Ticker);

}
