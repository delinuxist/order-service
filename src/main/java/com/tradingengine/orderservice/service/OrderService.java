package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    OrderEntity placeOrder(Long portfolioId, OrderRequestDto orderRequestDto) throws PortfolioNotFoundException;

    OrderStatusResponseDto checkOrderStatus(UUID orderID) throws OrderNotFoundException;

    OrderEntity getOrderById(UUID orderID) throws OrderNotFoundException;

    List<OrderEntity> getAllOrders();

    Boolean cancelOrder(UUID order_id) throws OrderNotFoundException;

    Boolean modifyOrder(UUID orderId, OrderRequestDto orderRequestDto) throws OrderNotFoundException;

    List<OrderEntity> fetchPendingOrders();

    void updateOrderStatus(OrderEntity order);
}
