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
        return stockRepository.findAllStocksByPortfolioAndTicker(portfolio, ticker);
    }

    public StockEntity fetchStockByPortfolioAndTicker(PortfolioEntity portfolio, String ticker) {
        Optional<StockEntity> stock = stockRepository.findStockEntityByPortfolioAndTicker(portfolio, ticker);
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
        Optional<StockEntity> stock = stockRepository.findStockByPortfolioAndTicker(portfolio, ticker);
        return stock.orElse(null);
    }

//    public List<StockEntity> fetchStockByClientIdAndPortfolioId(UUID portfolioId, UUID userId) {
//        List<PortfolioEntity> allPortfolios = portfolioRepository.findAll();
//        List<StockEntity> stockOwnedByPortfolio = new ArrayList<>();
//
//        for (PortfolioEntity portfolioEntity : allPortfolios) {
//            if (portfolioEntity.getPortfolioId().equals(portfolioId) && portfolioEntity.getUserId().equals(userId)) {
//                stockOwnedByPortfolio = portfolioEntity.getStocksOwned();
//            }
//            continue;
//        }
//        return stockOwnedByPortfolio;
//    }

    @Override
    public void saveStock(StockEntity stock) {
        stockRepository.save(stock);
    }
}
