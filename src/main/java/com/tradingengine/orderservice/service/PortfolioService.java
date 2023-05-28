package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.dto.PortfolioRequestDto;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.exception.portfolio.PortfolioDeletionFailedException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioService {

    PortfolioEntity createPortfolio(PortfolioRequestDto portfolioRequestDto);

    List<PortfolioEntity> fetchAllPortfolios();

    PortfolioEntity fetchPortfolioById(UUID portfolioId) throws PortfolioNotFoundException;

    PortfolioEntity updatePortfolio(UUID portfolioId,PortfolioRequestDto portfolioRequestDto) throws PortfolioNotFoundException;

    void deletePortfolio(UUID portfolioId) throws PortfolioNotFoundException, PortfolioDeletionFailedException;

    PortfolioEntity getPortfolioById(UUID portfolioId) throws PortfolioNotFoundException;
}
