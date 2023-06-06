package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.entity.StockEntity;
import com.tradingengine.orderservice.repository.StockRepository;
import com.tradingengine.orderservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public StockEntity fetchStockByPortfolioAndTicker(PortfolioEntity portfolio, String ticker) {
      Optional<StockEntity> stock = stockRepository.findStockEntitiesByPortfolioAndTicker(portfolio,ticker);
        return stock.orElse(null);
    }

    @Override
    public void saveStock(StockEntity stock) {
        stockRepository.save(stock);
    }

    @Override
    public Optional<StockEntity> fetchStockByPortfolioIdAndTicker(UUID portfolioId, String ticker) {
        return stockRepository.findStockEntityByPortfolio_ClientIdAndTicker(portfolioId, ticker);
    }
}
