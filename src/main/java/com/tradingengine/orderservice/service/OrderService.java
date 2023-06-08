package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.dto.OrderResponseDto;
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

    OrderResponseDto getOrderById(UUID orderId);

    List<OrderEntity> getAllOrderEntities();

    List<OrderLeg> getAllOrderLegs();

    List<OrderLeg> getAllOpenOrderLegs();

    List<OrderEntity> getAllOpenOrdersForProduct(String product);

    public List<OrderLeg> getAllOpenOrderLegsForAnEntity(UUID orderId);

    public List<OrderLeg> getAllFilledOrderLegsForAnEntity(UUID orderId);

    public List<OrderLeg> getAllOrderLegsForAnOrderEntity(UUID orderId);

    List<OrderEntity> getAllOpenOrderEntities();

    Boolean cancelOrder(UUID orderId) throws OrderNotFoundException;

    Boolean modifyOrderLeg(UUID orderId, OrderRequestToExchange orderRequestToExchange) throws OrderNotFoundException, OrderModificationFailureException;

    //todo? alr
    void updateOrderStatus(OrderEntity order, OrderStatus orderStatus);

    void updateOrderLegStatus(OrderLeg orderLeg, OrderStatus orderStatus);

//    String executeOrder(OrderRequestToExchange order, String exchangeUrl);


    List<OrderEntity> getAllCancelledOrderEntities();

    List<OrderEntity> fetchFilledOrders();

    OrderLeg saveOrderLeg(OrderLeg orderLeg);


    //todo: what is this for????
//    void TryAnOrder(UUID userId, UUID portfolioId, OrderRequestToExchange orderRequest) throws StockNotAvailable, BuyLimitExceededException, BuyOrderPriceNotReasonable, SellLimitExceededException, InsufficientBalanceException, SellOrderPriceCannotBeMatched, IOException, BuyOrderPriceCannotBeMatched, PortfolioNotFoundException;

    //todo: refactor this, fetch pending orders from the queue (all orders not yet sent to the exchange are pending orders)
//    List<OrderEntity> fetchPendingOrders();


}
