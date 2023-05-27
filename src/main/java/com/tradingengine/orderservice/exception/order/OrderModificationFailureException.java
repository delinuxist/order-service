package com.tradingengine.orderservice.exception.order;

public class OrderModificationFailureException extends Exception{
    public OrderModificationFailureException() {
        super("product,side and type must be same as original");
    }
}
