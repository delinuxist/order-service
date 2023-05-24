package com.tradingengine.orderservice.exception.portfolio;

public class PortfolioNotFoundException extends Exception{
    public PortfolioNotFoundException(Long portfolioId) {
        super("Portfolio with id: "+portfolioId+" not found");
    }
}
