/*
    - Use order request passed from Topic
    - Performs multi-leg split based on order type, order orderSide.
    - If OPenOrders.isEmpty, Order & OrderLeg automatically is OPEN. Else Order is Open, OrderLeg is FULFILLED/FAILED depending on response from exchange
    - Place split Order request on exchange, returns OrderId from exchange
    - Assigns/saves each split as individual Order Legs, related to the Order Entity they were split from, with individual orderIds
    - If orderId from exchange is empty string "", OrderLeg FAILED. Else, OrderLeg is FULFILLED

 */


package com.tradingengine.orderservice.utils.strategy;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.OrderLeg;
import com.tradingengine.orderservice.entity.Wallet;
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.enums.OrderType;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.marketdata.models.Product;
import com.tradingengine.orderservice.marketdata.service.MarketDataService;
import com.tradingengine.orderservice.repository.OrderLegRepository;
import com.tradingengine.orderservice.repository.OrderRepository;
import com.tradingengine.orderservice.repository.StockRepository;
import com.tradingengine.orderservice.repository.WalletRepository;
import com.tradingengine.orderservice.utils.ModelBuilder;
import com.tradingengine.orderservice.utils.WebClientService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
public class OrderProcessor {

    private final MarketDataService marketDataService;
    private final OrderRepository orderRepository;
    private final OrderLegRepository orderLegRepository;
    private final WebClientService webClientService;
    private final ModelBuilder builder;
    private final WalletRepository walletRepository;
    private final StockRepository stockRepository;

    @Value("${MalOne.url}")
    private String exchangeOne;

    //constructor
    @Autowired
    public OrderProcessor(MarketDataService marketDataService, OrderRepository orderRepository, OrderLegRepository orderLegRepository, WebClientService webClientService, ModelBuilder builder, WalletRepository walletRepository, StockRepository stockRepository) {
        this.marketDataService = marketDataService;
        this.webClientService = webClientService;
        this.orderRepository = orderRepository;
        this.orderLegRepository = orderLegRepository;
        this.builder = builder;
        this.walletRepository = walletRepository;
        this.stockRepository = stockRepository;
    }

    public void processOrder(OrderRequestToExchange orderRequest, UUID portfolioId, UUID userId) throws IOException, PortfolioNotFoundException {
        if (orderRequest.getSide().equals(OrderSide.BUY)) {
             buyOperation(orderRequest, portfolioId, userId);
        }
         sellOperation(orderRequest, portfolioId, userId);
    }

    @Transactional
    private void buyOperation(OrderRequestToExchange orderRequest, UUID portfolioId, UUID userId) throws IOException, PortfolioNotFoundException {

        //gets and stores list of openOrders with the same price as it.
        log.info("Getting products from exchanges and sorting based on type");
        List<Product> allOpenSellOrders = getOpenOrdersBasedOnType(orderRequest);

        log.info("Saving created order into database");
        OrderEntity orderEntity = builder.buildOrderEntity(orderRequest, portfolioId, userId);
        orderRepository.save(orderEntity);

        setCashBalance(orderRequest, userId);

        if (allOpenSellOrders.isEmpty()) {
            log.info("open order is empty, first to transact! placing order straight to exchange");
            OrderLeg currentExecutedOrderLeg = executeOrder(orderRequest, exchangeOne, orderEntity);

            orderEntity.setStatus(OrderStatus.OPEN);
            orderRepository.save(orderEntity);
            log.info("order placed successfully!");

        } else {
            int quantitySent = 0;
            int orderRequestQuantity = orderRequest.getQuantity();

            log.info("open order is not empty! Performing multi-leg split!");
            for (Product openOrder : allOpenSellOrders) {

                String exchangeUrl = openOrder.getExchangeUrl();
                int currentOpenOrderQuantity = openOrder.getQuantity();

                if (quantitySent < orderRequestQuantity) {

                    orderEntity.setStatus(OrderStatus.OPEN);
                    orderRepository.save(orderEntity);

                    //subtract what we sent so far from the original qty to get what's left
                    int quantityToSend = orderRequestQuantity - quantitySent;

                    if (currentOpenOrderQuantity > quantityToSend) {
                        orderRequest.setQuantity(quantityToSend);

                        quantitySent += quantityToSend;
                    } else {
                        orderRequest.setQuantity(currentOpenOrderQuantity);

                        quantitySent += currentOpenOrderQuantity;
                    }
                    orderRequest.setPrice(openOrder.getPrice());
                    OrderLeg currentExecutedOrderLeg = executeOrder(orderRequest, exchangeUrl, orderEntity);
                    log.info("multi-leg order placed!");
                }
            }
        }
        log.info("order has been placed successfully!");
    }

    @Transactional
    private void sellOperation(OrderRequestToExchange orderRequest, UUID portfolioId, UUID userId) throws
            IOException, PortfolioNotFoundException {

        //gets and stores list of openOrders with the same price as it.
        log.info("Getting products from exchanges and sorting based on type");
        List<Product> allOpenBuyOrders = getOpenOrdersBasedOnType(orderRequest);

        log.info("Saving created order into database");
        OrderEntity orderEntity = builder.buildOrderEntity(orderRequest, portfolioId, userId);
        orderRepository.save(orderEntity);

        setCashBalance(orderRequest, userId);

        if (allOpenBuyOrders.isEmpty()) {
            log.info("open order is empty, first to transact! placing order straight to exchange");
            OrderLeg currentExecutedOrderLeg = executeOrder(orderRequest, exchangeOne, orderEntity);

            orderEntity.setStatus(OrderStatus.OPEN);
            orderRepository.save(orderEntity);
            log.info("order placed successfully!");

        } else {
            int quantitySent = 0;
            int orderRequestQuantity = orderRequest.getQuantity();

            log.info("open order is not empty! Performing multi-leg split!");
            for (Product openOrder : allOpenBuyOrders) {

                String exchangeUrl = openOrder.getExchangeUrl();
                int currentOpenOrderQuantity = openOrder.getQuantity();

                if (quantitySent < orderRequestQuantity) {

                    orderEntity.setStatus(OrderStatus.OPEN);
                    orderRepository.save(orderEntity);

                    //subtract what we sent so far from the original qty to get what's left
                    int quantityToSend = orderRequestQuantity - quantitySent;

                    if (currentOpenOrderQuantity > quantityToSend) {
                        orderRequest.setQuantity(quantityToSend);

                        quantitySent += quantityToSend;
                    } else {
                        orderRequest.setQuantity(currentOpenOrderQuantity);

                        quantitySent += currentOpenOrderQuantity;
                    }
                    orderRequest.setPrice(openOrder.getPrice());
                    OrderLeg currentExecutedOrderLeg = executeOrder(orderRequest, exchangeUrl, orderEntity);
                    log.info("multi-leg order placed!");
                }
            }
        }
        log.info("order has been placed successfully!");
    }

    // frequently used methods
    public List<Product> getOpenOrdersBasedOnType(OrderRequestToExchange orderRequest) throws IOException {
        List<Product> allOpenSellOrders;
        String side = orderRequest.getSide().name();

        //if order is a limit buy order, get only open orders that match its price
        if (orderRequest.getType().equals(OrderType.LIMIT)) {
            allOpenSellOrders = marketDataService.findOrders(orderRequest.getProduct())
                    .stream()
                    .filter(product -> product.getPrice().equals(orderRequest.getPrice())).toList();
        } else {
            //get and sort open orders (market orders match only to limit orders)
            allOpenSellOrders = marketDataService.findOrders(orderRequest.getProduct())
                    .stream()
                    .filter(product -> product.getOrderType().equals(OrderType.LIMIT))
                    .sorted(Comparator.comparingDouble(Product::getPrice)).toList();
        }
        return allOpenSellOrders;
    }

    public OrderLeg executeOrder(OrderRequestToExchange orderRequest, String exchangeUrl, OrderEntity order) {

        log.info("Executing the order! ********************");
        String orderIdFromExchange = webClientService.placeOrderOnExchangeAndGetID(orderRequest, exchangeUrl);

        OrderLeg currentExecutedOrderLeg = builder.buildOrderLeg(orderIdFromExchange, exchangeOne, order, orderRequest.getQuantity());
        if(orderIdFromExchange.equals("")){
            currentExecutedOrderLeg.setOrderLegStatus(OrderStatus.FAILED);
        } else {
            currentExecutedOrderLeg.setOrderLegStatus(OrderStatus.OPEN);
        }
        log.info("**************************** {}", currentExecutedOrderLeg);
        orderLegRepository.save(currentExecutedOrderLeg);


        log.info("Order Executed! You will be notified shortly, OrderID is ------>  {}", orderIdFromExchange);
        return currentExecutedOrderLeg;
    }


    public void setCashBalance(OrderRequestToExchange orderRequest, UUID userId){
        Optional<Wallet> checkWallet = walletRepository.findByUserId(userId);
        if(checkWallet.isPresent()){
            Wallet wallet = checkWallet.get();
            if(orderRequest.getSide().equals(OrderSide.BUY)) {
                wallet.setAmount(wallet.getAmount() - (orderRequest.getPrice() * orderRequest.getQuantity()));
            } else {
                wallet.setAmount(wallet.getAmount() + (orderRequest.getPrice() * orderRequest.getQuantity()) );
            }
            walletRepository.save(wallet);
        }

    }

}
