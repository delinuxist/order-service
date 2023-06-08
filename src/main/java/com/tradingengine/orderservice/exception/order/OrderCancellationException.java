package com.tradingengine.orderservice.exception.order;

import java.util.UUID;

public class OrderCancellationException extends RuntimeException {
        public OrderCancellationException(UUID orderId) {
            super("Order with id:" + orderId + " cannot be cancelled");
        }
    }