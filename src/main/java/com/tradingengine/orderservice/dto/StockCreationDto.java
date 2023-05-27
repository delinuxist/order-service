package com.tradingengine.orderservice.dto;

import com.tradingengine.orderservice.entity.PortfolioEntity;

public record StockCreationDto(
        String ticker,
        Integer quantity,
        Double price,
        PortfolioEntity portfolio
) {
}
