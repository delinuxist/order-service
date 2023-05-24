package com.tradingengine.orderservice.dto;

import java.time.LocalDateTime;

public record Executions (
        LocalDateTime timestamp,
        Double price,
        Integer quantity
){ }
