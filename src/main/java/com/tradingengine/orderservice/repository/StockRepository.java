package com.tradingengine.orderservice.repository;

import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, UUID> {

    Optional<StockEntity> findStockEntityByPortfolioAndTicker(PortfolioEntity portfolio, String ticker);

    List<StockEntity> findAllStocksByPortfolioAndTicker(PortfolioEntity portfolio, String Ticker);

    Optional<StockEntity> findStocksByPortfolioAndUserId(PortfolioEntity portfolio, String Ticker);

    List<StockEntity> findAllStocksByUserId(UUID userId);

    Optional<StockEntity> findStockByPortfolioAndTicker(PortfolioEntity portfolio, String Ticker);


//    Optional<StockEntity> findStockEntityByPortfolio_ClientIdAndTicker(UUID portfolioId, String ticker);
//    List<StockEntity> findStockEntityByClientId(UUID clientId);


}
