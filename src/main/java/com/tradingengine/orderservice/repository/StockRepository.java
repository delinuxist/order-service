package com.tradingengine.orderservice.repository;

import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long> {

    Optional<StockEntity> findStockEntitiesByPortfolioAndTicker(PortfolioEntity portfolio, String ticker);

//    Optional<StockEntity> findStockEntityByPortfolio_ClientIdAndTicker(UUID portfolioId, String ticker);
//    List<StockEntity> findStockEntityByClientId(UUID clientId);


}
