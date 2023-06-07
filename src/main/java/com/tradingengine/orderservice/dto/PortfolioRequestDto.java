package com.tradingengine.orderservice.dto;

import jakarta.validation.constraints.NotEmpty;

public record PortfolioRequestDto(
        @NotEmpty
        String name
) {}
