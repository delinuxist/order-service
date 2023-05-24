package com.tradingengine.orderservice.exception.order;

import java.util.UUID;

public class OrderNotFoundException extends Exception{
    public OrderNotFoundException(UUID orderId) {
        super("Order with id:" + orderId + " not found");
    }
}
