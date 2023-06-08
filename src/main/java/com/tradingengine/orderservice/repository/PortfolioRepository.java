package com.tradingengine.orderservice.repository;

import com.tradingengine.orderservice.entity.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, UUID> {
    Optional<PortfolioEntity> findById(UUID portfolioId);
    // The function above is redundant because it's already implement by the jpa
    List<PortfolioEntity> findAllByClientId(UUID clientId);

}
