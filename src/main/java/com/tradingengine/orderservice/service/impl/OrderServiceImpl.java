package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.OrderLeg;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.exception.order.OrderModificationFailureException;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.repository.OrderLegRepository;
import com.tradingengine.orderservice.repository.OrderRepository;
import com.tradingengine.orderservice.repository.PortfolioRepository;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.utils.WebClientService;
import com.tradingengine.orderservice.utils.strategy.OrderProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
//@RequiredArgsConstructor
@Slf4j

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClientService webClientService;
    private final OrderLegRepository orderLegRepository;
    private final PortfolioRepository portfolioRepository;
    private final OrderProcessor orderProcessor;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, WebClientService webClientService, OrderLegRepository orderLegRepository, PortfolioRepository portfolioRepository, OrderProcessor orderProcessor) {
        this.orderRepository = orderRepository;
        this.webClientService = webClientService;
        this.orderLegRepository = orderLegRepository;
        this.portfolioRepository = portfolioRepository;
        this.orderProcessor = orderProcessor;
    }

    @Override
    public OrderEntity saveOrderEntity(OrderEntity order) {
        return orderRepository.save(order);
    }

    @Override
    public String processAndPlaceOrder(UUID portfolioId, OrderRequestToExchange orderRequestToExchange) throws PortfolioNotFoundException, IOException {
        Optional<PortfolioEntity> portfolio = portfolioRepository.findByPortfolioId(portfolioId);
        if (portfolio.isEmpty()) {
            System.out.println("No portfolio with such id");
            throw new PortfolioNotFoundException(portfolioId);
        }
        return orderProcessor.processOrder(orderRequestToExchange, portfolioId, portfolio.get().getUserId());

    }

    @Override
    public OrderStatusResponseDto checkOrderStatus(UUID orderId) throws OrderNotFoundException {
        Optional<OrderEntity> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }
        String exchangeUrl = order.get().getExchangeUrl();
        return webClientService.checkOrderStatus(orderId.toString(), exchangeUrl);
    }

    public OrderStatusResponseDto checkOrderLegStatus(UUID orderId) throws OrderNotFoundException {
        Optional<OrderLeg> orderLeg = orderLegRepository.findById(orderId);

        if (orderLeg.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }
        String exchangeUrl = orderLeg.get().getExchangeUrl();

        return webClientService.checkOrderStatus(orderId.toString(), exchangeUrl);
    }

    public OrderEntity fetchOrderById(UUID orderId) throws OrderNotFoundException {
        // just trying to reduce the checks
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public List<OrderEntity> fetchAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<OrderLeg> fetchAllOrderLegs() {
        return orderLegRepository.findAll();
    }

    public List<OrderLeg> fetchAllOpenOrderLegs() {
        return orderLegRepository.findAll().stream().filter(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.OPEN)).toList();
    }

    public List<OrderEntity> fetchAllOpenOrders() {
        return orderRepository.findAll().stream().filter(order -> order.getStatus().equals(OrderStatus.OPEN)).toList();
    }

    public Boolean cancelOrder(UUID orderId, String exchangeUrl) throws OrderNotFoundException {

        Optional<OrderEntity> order = orderRepository.findById(orderId);

        if (order.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }

        //cannot cancel filled orders
        if (order.get().getStatus().equals(OrderStatus.FILLED)) {
            return false;
        }

        Boolean result = webClientService.cancelOrder(orderId, exchangeUrl);

        if (result) {
            order.get().setStatus(OrderStatus.CANCELLED);
            order.get().setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order.get());

            List<OrderLeg> orderLegsOwnedByOrderEntity = order.get().getOrderLegsOwnedByEntity();

            for (OrderLeg orderLeg : orderLegsOwnedByOrderEntity) {
                if (!orderLeg.getOrderLegStatus().equals(OrderStatus.FILLED)) {
                    orderLeg.setOrderLegStatus(OrderStatus.CANCELLED);
                }
            }
        }
        return result;
    }

    public Boolean modifyOrder(UUID orderId, OrderRequestToExchange orderRequestToExchange, String exchangeUrl) throws OrderNotFoundException, OrderModificationFailureException {

        Optional<OrderEntity> order = orderRepository.findById(orderId);

        if (order.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }
        if (!order.get().getProduct().equals(orderRequestToExchange.getProduct()) &&
                !order.get().getOrderSide().equals(orderRequestToExchange.getSide()) &&
                !order.get().getType().equals(orderRequestToExchange.getType())) {
            throw new OrderModificationFailureException();
        }
        return webClientService.modifyOrderById(orderId, orderRequestToExchange, exchangeUrl);
    }

    @Override
    public List<OrderEntity> fetchAllOpenOrdersForProduct(String product) {
        //throw exception or not?
        //get all orders whose status is open
        return orderRepository.findAll()
                .stream().filter(order -> order.getStatus().equals(OrderStatus.OPEN)).toList();
    }

    public List<OrderEntity> fetchCancelledOrders() {
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

//    @Override
//    public String executeOrder(OrderRequestToExchange order, String exchangeUrl) {
//
//        log.info("Executing the order! ********************");
//        String response = (webClientService.placeOrderOnExchangeAndGetID(order, exchangeUrl)).toString();
//        log.info("Order Executed! You will be notified shortly, OrderID is ------>  {}", response);
//        return response;
//    }

    //todo: what is this for????
//    public void TryAnOrder(UUID userId, UUID portfolioId, OrderRequestToExchange orderRequest) throws StockNotAvailable, BuyLimitExceededException, BuyOrderPriceNotReasonable, SellLimitExceededException, InsufficientBalanceException, SellOrderPriceCannotBeMatched, IOException, BuyOrderPriceCannotBeMatched, PortfolioNotFoundException {
//        log.info("Processing order for user with id {}", userId);
//        orderProcessor.processOrder(orderRequest, portfolioId, userId);
//        log.info("Order submitted successfully");
//        log.info("Waiting for feedback");
//    }

    //TODO: rest call to get all stocks owned by client?
//    public List<StockEntity> getAllStocksOwnedByClient(UUID clientId) {
//        return stockRepository.findStockEntityByClientId(clientId);
//    }

    //todo: refactor this, fetch pending orders from the queue (all orders not yet sent to the exchange are pending orders)
//    public List<OrderEntity> fetchPendingOrders() {
//        return orderRepository.findPendingOrders();
//    }


}

