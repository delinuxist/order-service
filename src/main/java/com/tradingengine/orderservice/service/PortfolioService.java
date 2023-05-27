package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.dto.PortfolioRequestDto;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.exception.portfolio.PortfolioDeletionFailedException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;

import java.util.List;

public interface PortfolioService {

    PortfolioEntity createPortfolio(PortfolioRequestDto portfolioRequestDto);

    List<PortfolioEntity> fetchAllPortfolios();

    PortfolioEntity fetchPortfolioById(Long portfolioId) throws PortfolioNotFoundException;

    PortfolioEntity updatePortfolio(Long portfolioId,PortfolioRequestDto portfolioRequestDto) throws PortfolioNotFoundException;

    void deletePortfolio(Long portfolioId) throws PortfolioNotFoundException, PortfolioDeletionFailedException;

}
