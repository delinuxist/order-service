package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.entity.StockEntity;
import com.tradingengine.orderservice.repository.PortfolioRepository;
import com.tradingengine.orderservice.repository.StockRepository;
import com.tradingengine.orderservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;

    public List<StockEntity> fetchAllStocksByPortfolioIdAndTicker(UUID portfolioId, String ticker) {
        PortfolioEntity portfolio = (portfolioRepository.findById(portfolioId)).orElseThrow();
        return stockRepository.findAllByPortfolioAndTicker(portfolio, ticker);
    }

    public StockEntity findByPortfolioAndTickerAndUserId(PortfolioEntity portfolio, String ticker, UUID userId) {
        Optional<StockEntity> stock = stockRepository.findByPortfolioAndTickerAndUserId(portfolio, ticker, userId);
        return stock.orElse(null);
    }

    public List<StockEntity> fetchAllStocks() {
        return stockRepository.findAll();
    }

    public StockEntity fetchStockById(UUID stockId) {
        Optional<StockEntity> stock = stockRepository.findById(stockId);
        return stock.orElse(null);
    }

    public StockEntity fetchStockByPortFolioAndTicker(PortfolioEntity portfolio, String ticker) {
        Optional<StockEntity> stock = stockRepository.findByPortfolioAndTicker(portfolio, ticker);
        return stock.orElse(null);
    }


    @Override
    public void saveStock(StockEntity stock) {
        stockRepository.save(stock);
    }
}
