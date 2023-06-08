package com.tradingengine.orderservice.exception.order;

public class OrderModificationFailureException extends RuntimeException {
    public OrderModificationFailureException() {
        super("product,orderSide and type must be same as original");
    }
}
