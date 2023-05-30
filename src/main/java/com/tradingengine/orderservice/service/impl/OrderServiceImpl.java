package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.enums.APIKEY;
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.exception.verification.*;
import com.tradingengine.orderservice.external.service.ExchangeService;
import com.tradingengine.orderservice.marketdata.models.Order;
import com.tradingengine.orderservice.marketdata.models.Product;
import com.tradingengine.orderservice.marketdata.models.ProductInfo;
import com.tradingengine.orderservice.marketdata.service.MarketDataService;
import com.tradingengine.orderservice.repository.OrderRepository;
import com.tradingengine.orderservice.repository.PortfolioRepository;
import com.tradingengine.orderservice.repository.StockRepository;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.service.PortfolioService;
import com.tradingengine.orderservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    private final PortfolioRepository portfolioRepository;

    private final ExchangeService exchangeService;

    private final MarketDataService marketDataService;

    private final StockRepository stockRepository;

    private final WebClient webClient;

    private final WalletService walletService;

    private final PortfolioService portfolioService;
    // call the portfolio repository from the portfolio service

    public OrderEntity placeOrder(UUID portfolioId, OrderRequestDto orderRequestDto) throws PortfolioNotFoundException {

         UUID orderId =  exchangeService.placeOrder(orderRequestDto);

         Optional<PortfolioEntity> portfolio = portfolioRepository.findById(portfolioId);

         if(portfolio.isEmpty()) {
             throw new PortfolioNotFoundException(portfolioId);
         }

        // create order object to be saved in db
        OrderEntity order = OrderEntity.builder()
//                .orderId(orderId)
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

    public OrderEntity getOrderByIdB(UUID orderId) throws OrderNotFoundException {
        // just trying to reduce the checks
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
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



    private Boolean validateBuyLimit(OrderRequestDto order) throws IOException, BuyLimitExceededException {
        log.info("Validating Buy Limit Order against the current Market Data BuyLimit!");
       Stream<ProductInfo> products =  marketDataService.getProductByTicker(order.product());
       log.info("Current Market Buy Limit");
       boolean verified = products.anyMatch(productInfo -> productInfo.buyLimit() > order.quantity());
       if (verified) {
           return true;
       } throw new BuyLimitExceededException();
    }


    private Boolean validateSellLimit(OrderRequestDto order) throws IOException, SellLimitExceededException {
        log.info("Validating Sell Limit Order against the current Market Data Sell Limit!");
        Stream<ProductInfo> products =  marketDataService.getProductByTicker(order.product());
        log.info("Current Market Sell limit");
        boolean verified = products.anyMatch(productInfo -> productInfo.sellLimit() >= order.quantity());
        if (verified) {
            return true;
        } log.info("You can't sell your stock at that threshold");
        throw new SellLimitExceededException();

    }


    private Boolean validateBuyOrder(OrderRequestDto order, UUID userId) throws IOException, InsufficientBalanceException, BuyOrderPriceCannotBeMatched {
    // Validate whether client has sufficient amount to buy the stock
        log.info("Validating Buy Order against the current Market Data Buy Order!");
        log.info("Validating whether client has sufficient money to buy the stock!");
        Stream<ProductInfo> products =  marketDataService.getProductByTicker(order.product());
        log.info("Current Market Buy Order");
        boolean verifyPrice =  products.anyMatch(productInfo -> order.price() == productInfo.askPrice());
        if (verifyPrice) {
            boolean verifyAmountInWallet =  walletService.getWalletByUserId(userId)
                    .stream().anyMatch(wallet -> wallet.getAmount() > order.price());
            if (verifyAmountInWallet) {
                return true;
            }
            log.info("Insufficient funds to make a Buy Order");
            throw new InsufficientBalanceException();
        }
        log.info("Your price doesn't meet the the Ask Price on the exchange");
        throw new BuyOrderPriceCannotBeMatched();

    }


    private Boolean validateSellOrder(OrderRequestDto order, UUID portfolioId) throws IOException, SellOrderPriceCannotBeMatched, StockNotAvailable {
        // check whether user has that particular in stock
        log.info("Validating Sell Order By checking if client Owns That Stock");
        log.info("Validating Sell Order by checking the price tag the user wants to sell the stock");
        Stream<ProductInfo> products =  marketDataService.getProductByTicker(order.product());
        log.info("Current Market Buy Order");
        boolean  verifyStockOwner = stockRepository.findStockEntityByPortfolio_ClientIdAndTicker(portfolioId, order.product())
                .isPresent();
        if (verifyStockOwner) {
            boolean verifyBidPrice = products.anyMatch(productInfo -> productInfo.bidPrice() > order.price());
            if (verifyBidPrice) {
                return true;

            }
            log.info("Your selling price is too high can't be matched on the exchange");
            throw new SellOrderPriceCannotBeMatched();
        }
        log.info("You don't own such stock");
        throw new StockNotAvailable();
    }

    @Override
    public Boolean validateOrder(UUID portfolioId, OrderRequestDto order, UUID userId) throws IOException, BuyLimitExceededException,
            InsufficientBalanceException, BuyOrderPriceCannotBeMatched,
            SellLimitExceededException, StockNotAvailable, SellOrderPriceCannotBeMatched {
        if (order.side() == OrderSide.BUY) {
            return validateBuyLimit(order) && validateBuyOrder(order, userId);
        }
        return validateSellLimit(order) && validateSellOrder(order, portfolioId);

    }

    @Override
    public void executeOrder(OrderRequestDto order, String exchangeUrl) {

        webClient.post()
                .uri(exchangeUrl + APIKEY.KEY.getKey() + "/order")
                .body(Mono.just(order), order.getClass())
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(throwable -> log.info("Error occurred during executing order "))
                .onErrorReturn("").block();
    }

    private OrderEntity createOrderEntity(OrderRequestDto order, UUID portfolioId, UUID userId)
            throws PortfolioNotFoundException {
        return OrderEntity.builder()
                .price(order.price())
                .product(order.product())
                .side(order.side())
                .type(order.type())
                .quantity(order.quantity())
                .portfolio(portfolioService.getPortfolioById(portfolioId))
                .status(OrderStatus.PENDING)
                .clientId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void placeAnOrder(UUID  userId, UUID portfolioId, OrderRequestDto order) throws Exception {
        log.info("Processing order for user with id {}", userId);
        if (validateOrder(portfolioId, order, userId)) {
            log.info("Client order is valid! Processing order");
            OrderEntity orderEntity = createOrderEntity(order, portfolioId, userId);
            log.info("Saving crested order into database");
            orderRepository.save(orderEntity);
            log.info("Order saved in database with a pending flag!");
            log.info("Finding and Comparing order to various orders on exchange");
            Stream<Product> openOrders = marketDataService.findOrders(order.product(), order.side().name());
            log.info("Available open trades are .....................");
            Optional<Product> currentOrder = openOrders.findAny();
            log.info("Successful match found processing order on that exchange!");
            currentOrder.ifPresent(myOrder -> executeOrder(order, myOrder.getExchangeUrl()));
            log.info("Order submitted successfully");
            log.info("Waiting for feed back");
        }

    }
}
