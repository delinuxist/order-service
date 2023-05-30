package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.exception.verification.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderEntity placeOrder(UUID portfolioId, OrderRequestDto orderRequestDto) throws PortfolioNotFoundException;

    OrderStatusResponseDto checkOrderStatus(UUID orderID) throws OrderNotFoundException;

    OrderEntity getOrderById(UUID orderID) throws OrderNotFoundException;

    List<OrderEntity> getAllOrders();

    Boolean cancelOrder(UUID order_id) throws OrderNotFoundException;

    Boolean modifyOrder(UUID orderId, OrderRequestDto orderRequestDto) throws OrderNotFoundException;

    List<OrderEntity> fetchPendingOrders();

    void updateOrderStatus(OrderEntity order);

    Boolean validateOrder(UUID portfolioId, OrderRequestDto orderRequestDto, UUID userId) throws IOException, BuyLimitExceededException, InsufficientBalanceException, BuyOrderPriceCannotBeMatched, SellLimitExceededException, StockNotAvailable, SellOrderPriceCannotBeMatched;

    void executeOrder(OrderRequestDto order, String exchangeUrl);

    void placeAnOrder(UUID userId, UUID portfolioId, OrderRequestDto order) throws Exception;
    // after making necessary checks and validation we submit it based on the exchange type.


}
