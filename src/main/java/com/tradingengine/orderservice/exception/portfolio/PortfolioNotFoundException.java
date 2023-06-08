package com.tradingengine.orderservice.exception.portfolio;

import java.util.UUID;

public class PortfolioNotFoundException extends RuntimeException{
    public PortfolioNotFoundException(UUID portfolioId) {
        super("Portfolio with id: "+portfolioId+" not found");
    }
}
