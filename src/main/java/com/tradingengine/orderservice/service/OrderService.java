package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.dto.OrderResponseDto;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.OrderLeg;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.exception.order.OrderModificationFailureException;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    void processAndPlaceOrder(UUID portfolioId, OrderRequestToExchange orderRequestToExchange) throws PortfolioNotFoundException, IOException;

    OrderEntity saveOrderEntity(OrderEntity order);

    OrderResponseDto fetchOrderById(UUID orderId);

    List<OrderEntity> fetchAllOrders();

    List<OrderLeg> fetchAllOrderLegs();

    List<OrderLeg> fetchAllOpenOrderLegs();

    List<OrderEntity> fetchAllOpenOrdersForProduct(String product);

    List<OrderEntity> fetchAllOpenOrders();

    Boolean cancelOrder(UUID orderId, String exchangeUrl) throws OrderNotFoundException;

    Boolean modifyOrder(UUID orderId, OrderRequestToExchange orderRequestToExchange, String exchangeUrl) throws OrderNotFoundException, OrderModificationFailureException;

    //todo? alr
    void updateOrderStatus(OrderEntity order, OrderStatus orderStatus);

    void updateOrderLegStatus(OrderLeg orderLeg, OrderStatus orderStatus);

//    String executeOrder(OrderRequestToExchange order, String exchangeUrl);


    List<OrderEntity> fetchCancelledOrders();

    List<OrderEntity> fetchFilledOrders();

    OrderLeg saveOrderLeg(OrderLeg orderLeg);


    //todo: what is this for????
//    void TryAnOrder(UUID userId, UUID portfolioId, OrderRequestToExchange orderRequest) throws StockNotAvailable, BuyLimitExceededException, BuyOrderPriceNotReasonable, SellLimitExceededException, InsufficientBalanceException, SellOrderPriceCannotBeMatched, IOException, BuyOrderPriceCannotBeMatched, PortfolioNotFoundException;

    //todo: refactor this, fetch pending orders from the queue (all orders not yet sent to the exchange are pending orders)
//    List<OrderEntity> fetchPendingOrders();


}
