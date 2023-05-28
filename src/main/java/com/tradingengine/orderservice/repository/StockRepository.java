package com.tradingengine.orderservice.repository;

import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<StockEntity,Long> {

    Optional<StockEntity> findStockEntitiesByPortfolioAndTicker(PortfolioEntity portfolio, String ticker);
    Optional<StockEntity> findStockEntityByPortfolio_ClientIdAndTicker(UUID portfolioId, String ticker);


}
