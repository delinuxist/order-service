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
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.enums.OrderType;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.marketdata.models.Product;
import com.tradingengine.orderservice.marketdata.service.MarketDataService;
import com.tradingengine.orderservice.repository.OrderLegRepository;
import com.tradingengine.orderservice.repository.OrderRepository;
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
import java.util.UUID;


@Service
@Slf4j
public class OrderProcessor {

    private final MarketDataService marketDataService;
    private final OrderRepository orderRepository;
    private final OrderLegRepository orderLegRepository;
    private final WebClientService webClientService;
    private final ModelBuilder builder;

    @Value("${MalOne.url}")
    private String exchangeOne;

    //constructor
    @Autowired
    public OrderProcessor(MarketDataService marketDataService, OrderRepository orderRepository, OrderLegRepository orderLegRepository, WebClientService webClientService, ModelBuilder builder) {
        this.marketDataService = marketDataService;
        this.webClientService = webClientService;
        this.orderRepository = orderRepository;
        this.orderLegRepository = orderLegRepository;
        this.builder = builder;
    }

    public String processOrder(OrderRequestToExchange orderRequest, UUID portfolioId, UUID userId) throws IOException, PortfolioNotFoundException {
        if (orderRequest.getSide().equals(OrderSide.BUY)) {
            return buyOperation(orderRequest, portfolioId, userId);
        }
        return sellOperation(orderRequest, portfolioId, userId);
    }

    @Transactional
    private String buyOperation(OrderRequestToExchange orderRequest, UUID portfolioId, UUID userId) throws IOException, PortfolioNotFoundException {

        //gets and stores list of openOrders with the same price as it.
        log.info("Getting products from exchanges and sorting based on type");
        List<Product> allOpenSellOrders = getOpenOrdersBasedOnType(orderRequest);

        log.info("Saving created order into database");
        OrderEntity orderEntity = builder.buildOrderEntity(orderRequest, portfolioId, userId);
        orderRepository.save(orderEntity);

        if (allOpenSellOrders.isEmpty()) {
            log.info("open order is empty, placing order straight to exchange");
            //place order on exchange directly without splitting (since you're first one to transact)
            OrderLeg currentExecutedOrderLeg = executeOrder(orderRequest, exchangeOne, orderEntity);
            orderLegRepository.save(currentExecutedOrderLeg);

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
                        // todo: reduce cash balance in wallet (check calculation sample below)

                        //todo: why change orderRequest.getPrice() to openOrder.getPrice() ?
                        //in case the price of the open order is lower than the price of the order request, place cheaper price, get profit
                        orderRequest.setQuantity(quantityToSend);
                        orderRequest.setPrice(openOrder.getPrice());

                        //execute order, get order leg
                        OrderLeg currentExecutedOrderLeg = executeOrder(orderRequest, exchangeUrl, orderEntity);
                        orderLegRepository.save(currentExecutedOrderLeg);
                        log.info("multi-leg order placed!");

                        quantitySent += quantityToSend;
                    } else {
                        // todo: reduce cash balance in wallet (check calculation sample below)

                        //if currentOpenOrderQuantity is less than the quantity we want to send
                        //we make a request to send the qty of the currentOpenOrderQuantity rather to the exchange
                        //then loop over again to send the rest of the qty order request
                        orderRequest.setQuantity(currentOpenOrderQuantity);
                        orderRequest.setPrice(openOrder.getPrice());

                        log.info("order qty is less than open order qty. placing trade....");
                        OrderLeg currentExecutedOrderLeg = executeOrder(orderRequest, exchangeUrl, orderEntity);
                        orderLegRepository.save(currentExecutedOrderLeg);
                        log.info("order has been placed successfully!");


                        quantitySent += currentOpenOrderQuantity;
                    }
                }
            }
        }
        return "Order Processed. Order id is:" + orderEntity.getId();
    }

    @Transactional
    private String sellOperation(OrderRequestToExchange orderRequest, UUID portfolioId, UUID userId) throws
            IOException, PortfolioNotFoundException {

        //extract fields from order request and build the order
        log.info("Saving created order into database");
        OrderEntity orderEntity = builder.buildOrderEntity(orderRequest, portfolioId, userId);
        orderRepository.save(orderEntity);
        log.info("Order saved in database with a pending flag!");

        //todo: check orderservice note in trading engine folder

        log.info("Finding and Comparing order to various orders on exchange");
        log.info("Available open trades are .....................");
        List<Product> allOpenBuyOrders = getOpenOrdersBasedOnType(orderRequest);

        if (allOpenBuyOrders.isEmpty()) {
            //place order on exchange directly without splitting (since you're first one to transact)
            OrderLeg currentExecutedOrderLeg = executeOrder(orderRequest, exchangeOne, orderEntity);
            orderLegRepository.save(currentExecutedOrderLeg);


        } else {
            int quantitySent = 0;
            int orderRequestQuantity = orderRequest.getQuantity();

            for (Product openOrder : allOpenBuyOrders) {

                String exchangeUrl = openOrder.getExchangeUrl();
                int currentOpenOrderQuantity = openOrder.getQuantity();

                if (quantitySent < orderRequestQuantity) {

                    orderEntity.setStatus(OrderStatus.OPEN);
                    orderRepository.save(orderEntity);

                    //subtract what we sent so far from the original qty to get what's left
                    int quantityToSend = orderRequestQuantity - quantitySent;

                    if (currentOpenOrderQuantity > quantityToSend) {
                        // todo: reduce cash balance in wallet (check calculation sample below)

                        //todo: why change orderRequest.getPrice() to openOrder.getPrice() ?
                        //in case the price of the open order is lower than the price of the order request, place cheaper price, get profit
                        orderRequest.setQuantity(quantityToSend);
                        orderRequest.setPrice(openOrder.getPrice());

                        OrderLeg currentExecutedOrderLeg = executeOrder(orderRequest, exchangeUrl, orderEntity);
                        orderLegRepository.save(currentExecutedOrderLeg);


                        quantitySent += quantityToSend;
                    } else {
                        // todo: reduce cash balance in wallet (check calculation sample below)

                        //if currentOpenOrderQuantity is less than the quantity we want to send
                        //we make a request to send the qty of the currentOpenOrderQuantity rather to the exchange
                        //then loop over again to send the rest of the qty order request
                        orderRequest.setQuantity(currentOpenOrderQuantity);
                        orderRequest.setPrice(openOrder.getPrice());

                        OrderLeg currentExecutedOrderLeg = executeOrder(orderRequest, exchangeUrl, orderEntity);
                        orderLegRepository.save(currentExecutedOrderLeg);

                        quantitySent += currentOpenOrderQuantity;
                    }
                }
            }
        }
        return "Order Processed. Order id is:" + orderEntity.getId();
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


    public OrderLeg executeOrder(OrderRequestToExchange orderRequest, String exchangeUrl, OrderEntity orderEntity) {

        log.info("Executing the order! ********************");
        String orderIdFromExchange = webClientService.placeOrderOnExchangeAndGetID(orderRequest, exchangeUrl);

        OrderLeg currentExecutedOrderLeg = builder.buildOrderLeg(null, exchangeOne, orderEntity, orderRequest.getQuantity());
        if(orderIdFromExchange.equals("")){
            currentExecutedOrderLeg.setOrderLegStatus(OrderStatus.FAILED);

        } else {
            currentExecutedOrderLeg.setOrderLegStatus(OrderStatus.OPEN);
        }
        currentExecutedOrderLeg.setId(orderIdFromExchange);
        log.info("**************************** {}", currentExecutedOrderLeg);
        orderLegRepository.save(currentExecutedOrderLeg);


        log.info("Order Executed! You will be notified shortly, OrderID is ------>  {}", orderIdFromExchange);
        return currentExecutedOrderLeg;
    }
















// todo: reduce cash balance in wallet (sample)
/*
if quantitySent < orderRequestQuantity:
wallet = updateBalanceOfClientWallet(wallet, (quantityToSend + currentOpenOrderQuantity);
else:
wallet = updateBalanceOfClientWallet(wallet, currentOpenOrderQuantity);
*/
}
