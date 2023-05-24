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
    public OrderEntity placeOrder(Long portfolioId, OrderRequestDto orderRequestDto) throws PortfolioNotFoundException;

    public OrderStatusResponseDto checkOrderStatus(UUID orderID) throws OrderNotFoundException;

    public Optional<OrderEntity> getOrder(UUID orderID);

    public List<OrderEntity> getAllOrders();

    public String cancelOrder(UUID order_id) throws OrderNotFoundException;
}
