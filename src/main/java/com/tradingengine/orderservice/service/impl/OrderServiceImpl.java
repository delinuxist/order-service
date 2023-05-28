package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.external.service.ExchangeService;
import com.tradingengine.orderservice.marketdata.models.Order;
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


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private void placeAnOrder(UUID  userId, UUID portfolioId, OrderRequestDto order) throws PortfolioNotFoundException {
        log.info("Processing order for user with id {}", userId);
        if (validateOrder(portfolioId, order, userId)) {
            OrderEntity orderEntity = createOrderEntity(order, portfolioId, userId);
            List<Order> openOrders = marketDataService.getOrdersByTickerAndSideAndType(order);
            Optional<Order> currentOrder = openOrders.stream().findAny();
            currentOrder.ifPresent(myOrder -> executeOrder(order, myOrder.getExchangeUrl()));
        }
        // perform other activities before making an order eg validations
        // saving it in database first before making order
        // portfolio repo shoud be called from portfolio service
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



    private Boolean validateBuyLimit(OrderRequestDto order) {
       List<ProductInfo> products =  marketDataService.getProductByTicker(order.product());
       return products.stream().anyMatch(productInfo -> productInfo.BUY_LIMIT() > order.quantity());
    }


    private Boolean validateSellLimit(OrderRequestDto order) {
        List<ProductInfo> products =  marketDataService.getProductByTicker(order.product());
        return products.stream().anyMatch(productInfo -> productInfo.SELL_LIMIT() > order.quantity());
    }


    private Boolean validateBuyOrder(OrderRequestDto order, UUID userId) {
    // Validate whether client has sufficient amount to buy the stock
        List<ProductInfo> products =  marketDataService.getProductByTicker(order.product());
        return products.stream().anyMatch(productInfo -> order.price() > productInfo.ASK_PRICE())
                && walletService.getWalletByUserId(userId)
                .stream().anyMatch(wallet -> wallet.getAmount() > order.price());
    }


    private Boolean validateSellOrder(OrderRequestDto order, UUID portfolioId) {
        // check whether user has that particular in stock
       return stockRepository.findStockEntityByPortfolio_ClientIdAndTicker(portfolioId, order.product())
                .isPresent();
    }

    @Override
    public Boolean validateOrder(UUID portfolioId, OrderRequestDto order, UUID userId) {
        if (order.side() == OrderSide.BUY) {
            return validateBuyLimit(order) && validateBuyOrder(order, userId);
        }
        return validateSellLimit(order) && validateSellOrder(order, portfolioId);
    }

    @Override
    public String executeOrder(OrderRequestDto order, String exchangeUrl) {
        return webClient.post()
                .uri(exchangeUrl + "{privateKey}" + "/order")
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
}
