package com.tradingengine.orderservice.exception.order;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(UUID orderId) {
        super("Order with id:" + orderId + " not found");
    }
}
