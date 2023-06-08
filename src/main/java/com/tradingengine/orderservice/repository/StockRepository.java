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

    List<StockEntity> findAllByPortfolioAndTicker(PortfolioEntity portfolio, String Ticker);
    Optional<StockEntity> findByPortfolioAndUserId(PortfolioEntity portfolio, String Ticker);
    List<StockEntity> findAllByUserId(UUID userId);
    Optional<StockEntity> findByPortfolioAndTickerAndUserId(PortfolioEntity portfolio, String ticker, UUID userId);

    Optional<StockEntity> findByPortfolioAndTicker(PortfolioEntity portfolio, String Ticker);


}
