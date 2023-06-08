package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.dto.OrderResponseDto;
import com.tradingengine.orderservice.entity.*;
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.exception.order.OrderCancellationException;
import com.tradingengine.orderservice.exception.order.OrderModificationFailureException;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.repository.*;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.service.StockService;
import com.tradingengine.orderservice.utils.ModelBuilder;
import com.tradingengine.orderservice.utils.WebClientService;
import com.tradingengine.orderservice.utils.strategy.OrderProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClientService webClientService;
    private final OrderLegRepository orderLegRepository;
    private final PortfolioRepository portfolioRepository;
    private final StockRepository stockRepository;
    private final OrderProcessor orderProcessor;
    private final ModelBuilder builder;
    private final WalletRepository walletRepository;
    private final StockService stockService;


    @Override
    public OrderEntity saveOrderEntity(OrderEntity order) {
        return orderRepository.save(order);
    }

    @Override
    public void processAndPlaceOrder(UUID portfolioId, OrderRequestToExchange orderRequestToExchange) throws PortfolioNotFoundException, IOException {
        Optional<PortfolioEntity> portfolio = portfolioRepository.findByPortfolioId(portfolioId);
        if (portfolio.isEmpty()) {
            System.out.println("No portfolio with such id");
            throw new PortfolioNotFoundException(portfolioId);
        }
        orderProcessor.processOrder(orderRequestToExchange, portfolioId, portfolio.get().getUserId());

    }

    @Override
    public OrderResponseDto getOrderById(UUID orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return builder.buildOrderResponse(orderEntity);
    }

    @Override
    public List<OrderEntity> getAllOrderEntities() {
        return orderRepository.findAll();
    }

    @Override
    public List<OrderLeg> getAllOrderLegs() {
        return orderLegRepository.findAll();
    }

    public List<OrderLeg> getAllOpenOrderLegs() {
        return orderLegRepository.findAll().stream().filter(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.OPEN)).toList();
    }

    public List<OrderLeg> getAllOrderLegsForAnOrderEntity(UUID orderId){
        Optional<OrderEntity> checkOrder = orderRepository.findById(orderId);
        return checkOrder.map(OrderEntity::getOrderLegsOwnedByEntity).orElse(null);
    }

    public List<OrderLeg> getAllOpenOrderLegsForAnEntity(UUID orderId){
        Optional<OrderEntity> checkOrder = orderRepository.findById(orderId);
        return checkOrder.map(orderEntity -> orderEntity.getOrderLegsOwnedByEntity()
                .stream().filter(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.OPEN))
                .toList()).orElse(null);
    }

    public List<OrderLeg> getAllFilledOrderLegsForAnEntity(UUID orderId){
        Optional<OrderEntity> checkOrder = orderRepository.findById(orderId);
        return checkOrder.map(orderEntity -> orderEntity.getOrderLegsOwnedByEntity()
                .stream().filter(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.FILLED))
                .toList()).orElse(null);
    }

    public List<OrderLeg> getAllFailedOrderLegsForAnEntity(UUID orderId){
        Optional<OrderEntity> checkOrder = orderRepository.findById(orderId);
        return checkOrder.map(orderEntity -> orderEntity.getOrderLegsOwnedByEntity()
                .stream().filter(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.FAILED))
                .toList()).orElse(null);
    }

    public List<OrderEntity> getAllOpenOrderEntities() {
        return orderRepository.findAll()
                .stream().filter(order -> order.getStatus().equals(OrderStatus.OPEN))
                .toList();
    }

    public Boolean cancelOrder(UUID orderId) throws OrderNotFoundException {

        Optional<OrderEntity> order = orderRepository.findById(orderId);

        if (order.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }

        //cannot cancel filled orders
        if (order.get().getStatus().equals(OrderStatus.FILLED)) {
            return false;
        }

        List<OrderLeg> orderLegsOwnedByOrderEntity = order.get().getOrderLegsOwnedByEntity();

        for (OrderLeg orderLeg : orderLegsOwnedByOrderEntity) {

            if (orderLeg.getOrderLegStatus().equals(OrderStatus.FILLED)) {
                throw new OrderCancellationException(orderId);
            }
            String exchangeUrl = orderLeg.getExchangeUrl();
            Boolean result = webClientService.cancelOrder(orderLeg.getIdFromExchange(), orderLeg.getExchangeUrl());
            orderLeg.setOrderLegStatus(OrderStatus.CANCELLED);
            orderLegRepository.save(orderLeg);

            order.get().setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order.get());
        }

        //todo: updateWalletAfterCancellation
        updateAccountAfterCancellation(order.get());
        return true;
    }

    private void updateAccountAfterCancellation(OrderEntity order) {
        List<OrderLeg> orderLegsOwnedByOrderEntity = order.getOrderLegsOwnedByEntity();

        for (OrderLeg orderLeg : orderLegsOwnedByOrderEntity) {

            Optional<Wallet> checkWallet = walletRepository.findByUserId(order.getUserId());
            StockEntity stock = stockService.findByPortfolioAndTickerAndUserId(order.getPortfolio(), order.getProduct(), order.getUserId());

            if (checkWallet.isPresent()) {
                Wallet wallet = checkWallet.get();

                if (order.getOrderSide().equals(OrderSide.BUY)) {
                    log.info("refunding cash money");
                    wallet.setAmount(wallet.getAmount() + (order.getPrice() * orderLeg.getQuantity()));

                    log.info("taking stocks back");
                    stock.setQuantity(stock.getQuantity() - order.getQuantity());

                } else {
                    log.info("getting cash money");
                    wallet.setAmount(wallet.getAmount() - (order.getPrice() * orderLeg.getQuantity()));

                    log.info("giving stocks back");
                    stock.setQuantity(stock.getQuantity() + order.getQuantity());
                }
                walletRepository.save(wallet);
                stockService.saveStock(stock);
            }
        }
    }

    public Boolean modifyOrderLeg(UUID orderId, OrderRequestToExchange orderRequestToExchange) throws OrderNotFoundException, OrderModificationFailureException {

        Optional<OrderLeg> order = orderLegRepository.findById(orderId);

        if (order.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }

        if (!order.get().getProduct().equals(orderRequestToExchange.getProduct()) &&
                !order.get().getOrderSide().equals(orderRequestToExchange.getSide()) &&
                !order.get().getType().equals(orderRequestToExchange.getType())) {
            throw new OrderModificationFailureException();
        }

        String exchangeUrl = order.get().getExchangeUrl();
        return webClientService.modifyOrderById(orderId, orderRequestToExchange, exchangeUrl);
    }

    @Override
    public List<OrderEntity> getAllOpenOrdersForProduct(String product) {
        //throw exception or not?
        //get all orders whose status is open
        return orderRepository.findAll()
                .stream().filter(order -> order.getStatus().equals(OrderStatus.OPEN)).toList();
    }

    public List<OrderEntity> getAllCancelledOrderEntities() {
        //throw exception or not?
        return orderRepository.findAll()
                .stream().filter(order -> order.getStatus().equals(OrderStatus.CANCELLED)).toList();
    }

    public List<OrderEntity> fetchFilledOrders() {
        return orderRepository.findAll()
                .stream().filter(order -> order.getStatus().equals(OrderStatus.FILLED)).toList();
    }

    public OrderLeg saveOrderLeg(OrderLeg orderLeg) {
        return orderLegRepository.save(orderLeg);
    }

    public void updateOrderStatus(OrderEntity order, OrderStatus orderStatus) {
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }

    @Override
    public void updateOrderLegStatus(OrderLeg orderLeg, OrderStatus orderStatus) {
        orderLeg.setOrderLegStatus(orderStatus);
        orderLegRepository.save(orderLeg);
    }

    public List<StockEntity> getAllStocksOwnedByUser(UUID userId) {
        return stockRepository.findAllByUserId(userId);
    }



}

