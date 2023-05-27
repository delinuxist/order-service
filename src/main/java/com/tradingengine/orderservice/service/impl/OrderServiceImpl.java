package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.external.service.ExchangeService;
import com.tradingengine.orderservice.repository.OrderRepository;
import com.tradingengine.orderservice.repository.PortfolioRepository;
import com.tradingengine.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final PortfolioRepository portfolioRepository;

    private final ExchangeService exchangeService;

    public OrderEntity placeOrder(Long portfolioId, OrderRequestDto orderRequestDto) throws PortfolioNotFoundException {

         UUID orderId =  exchangeService.placeOrder(orderRequestDto);

         Optional<PortfolioEntity> portfolio = portfolioRepository.findById(portfolioId);

         if(portfolio.isEmpty()) {
             throw new PortfolioNotFoundException(portfolioId);
         }

        // create order object to be saved in db
        OrderEntity order = OrderEntity.builder()
                .orderId(orderId)
                .portfolio(portfolio.get())
                .price(orderRequestDto.price())
                .product(orderRequestDto.product())
                .side(orderRequestDto.side())
                .status(OrderStatus.PENDING)
                .quantity(orderRequestDto.quantity())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .type(orderRequestDto.type())
                .clientId(null)
                .build();
        return orderRepository.save(order);
    }


    public OrderStatusResponseDto checkOrderStatus(UUID orderID) throws OrderNotFoundException {
        Optional<OrderEntity> order = orderRepository.findById(orderID);
        if(order.isEmpty()){
            throw new OrderNotFoundException(orderID);
        }
        return exchangeService.checkStatus(orderID);
    }


    public OrderEntity getOrderById(UUID orderId) throws OrderNotFoundException {
        Optional<OrderEntity> order = orderRepository.findById(orderId);
        if(order.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }

        return order.get();
    }


    public List<OrderEntity> getAllOrders(){
        return orderRepository.findAll();
    }


    public Boolean cancelOrder(UUID orderId) throws OrderNotFoundException {

        Optional<OrderEntity> order = orderRepository.findById(orderId);

        if (order.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }

        Boolean result = exchangeService.cancelOrder(orderId);
        System.out.println(result);

        if (result) {
            order.get().setStatus(OrderStatus.CANCELLED);
            order.get().setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order.get());
        }
        return result;
    }


    public Boolean modifyOrder(UUID orderId, OrderRequestDto orderRequestDto) throws OrderNotFoundException {

        Optional<OrderEntity> order = orderRepository.findById(orderId);

        if (order.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }

        if (!order.get().getProduct().equals(orderRequestDto.product()) &&
                !order.get().getSide().equals(orderRequestDto.side()) &&
                !order.get().getType().equals(orderRequestDto.type())) {
            // throw an exception
            System.out.println();
        }

        return exchangeService.modifyOrder(orderId, orderRequestDto);
    }

    @Override
    public List<OrderEntity> fetchPendingOrders() {
        return orderRepository.findPendingOrders();
    }

    @Override
    public void updateOrderStatus(OrderEntity order) {
        orderRepository.save(order);
    }
}
