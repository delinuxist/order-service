package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.entity.Wallet;
import com.tradingengine.orderservice.enums.APIKEY;
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.enums.OrderType;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.exception.verification.*;
import com.tradingengine.orderservice.external.service.ExchangeService;
import com.tradingengine.orderservice.marketdata.models.Trade;
import com.tradingengine.orderservice.marketdata.models.TradeInfo;
import com.tradingengine.orderservice.marketdata.service.MarketDataService;
import com.tradingengine.orderservice.repository.OrderRepository;
import com.tradingengine.orderservice.repository.PortfolioRepository;
import com.tradingengine.orderservice.repository.StockRepository;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.service.PortfolioService;
import com.tradingengine.orderservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
                .price(orderRequestDto.getPrice())
                .product(orderRequestDto.getProduct())
                .side(orderRequestDto.getSide())
                .status(OrderStatus.PENDING)
                .quantity(orderRequestDto.getQuantity())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .type(orderRequestDto.getType())
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

        if (!order.get().getProduct().equals(orderRequestDto.getProduct()) &&
                !order.get().getSide().equals(orderRequestDto.getSide()) &&
                !order.get().getType().equals(orderRequestDto.getType())) {
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


    private OrderEntity createOrderEntity(OrderRequestDto order, UUID portfolioId, UUID userId)
            throws PortfolioNotFoundException {
        return OrderEntity.builder()
                .price(order.getPrice())
                .product(order.getProduct())
                .side(order.getSide())
                .type(order.getType())
                .quantity(order.getQuantity())
                .portfolio(portfolioService.getPortfolioById(portfolioId))
                .status(OrderStatus.PENDING)
                .clientId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .legId(order.getLegId())
                .build();
    }

    @Override
    public List<Trade> getOpenTrades(String product) throws IOException {
        return marketDataService.findOpenTrades(product);
    }

    private boolean isQuotedPriceValid(OrderRequestDto order) throws  IOException, BuyOrderPriceNotReasonable {
        log.info("Checking whether your bid price is valid! Not too low or high");
        Stream<TradeInfo> products =  marketDataService.getProductByTicker(order.getProduct());
        // make sure client's price is greater than the minimum bid price ie bidPrice-maxPriceShift
        // make sure client's price is less than the maximum bid price ie bidPrice + maxPriceShift
        boolean isQuotedPriceValid = products.anyMatch(product -> order.getPrice() >=  product.bidPrice() - product.maxPriceShift()
                && order.getPrice() <= product.bidPrice() + product.maxPriceShift());
        if (isQuotedPriceValid) {
            log.info("Your BUY price is Reasonable and can be matched on Exchange");
            return true;
        }
        log.info("Your BUY price is not Reasonable");
        throw new BuyOrderPriceNotReasonable();

    }

    private boolean isWalletAmountSufficient(OrderRequestDto order, UUID userId) throws InsufficientBalanceException {
        log.info("Checking whether Wallet Balance is sufficient");
        // check if yor wallet balance can match your bidPrice walletAccount >= Order.Price
        boolean isClientWalletSufficient = walletService.getWalletByUserId(userId)
                .stream().anyMatch(wallet -> wallet.getAmount() >= order.getPrice());
        if (isClientWalletSufficient) {
            log.info("Wallet balance is sufficient");
            return true;
        } log.info("Insufficient funds to make a Buy Order");
        throw new InsufficientBalanceException();
    }

    private Boolean isBuyQuantityValid(OrderRequestDto order) throws IOException, BuyLimitExceededException {
        log.info("Validating Buy Limit Order against the current Market Data BuyLimit!");
        Stream<TradeInfo> products =  marketDataService.getProductByTicker(order.getProduct());
        log.info("Current Market Buy Limit");
        // check if the quantity you want to buy is available
        boolean verified = products.anyMatch(tradeInfo -> tradeInfo.buyLimit() >= order.getQuantity());
        if (verified) {
            log.info("The quantity you want to buy is valid and  may likely be possible");
            return true;
        };
        log.info("Yor Buy quantity can't be matched");
        throw new BuyLimitExceededException();
    }

    public boolean validateBuyOrderWithLimit(OrderRequestDto order, UUID userId) throws IOException,
            InsufficientBalanceException, BuyOrderPriceNotReasonable, BuyLimitExceededException {
        log.info("Validating a BUY order with LIMIT");
        Stream<TradeInfo> products =  marketDataService.getProductByTicker(order.getProduct());
        log.info("Current Market data for {}", order.getProduct());
        products.forEach(System.out::println);
        return isQuotedPriceValid(order) && isWalletAmountSufficient(order, userId) && isBuyQuantityValid(order);
    }


    private Optional<Wallet> getWalletBalance(UUID userId) {
        log.info("Obtaining amount in wallet");
        return walletService.getWalletByUserId(userId);
    }



    // No price validation
    public boolean isWalletBalanceSufficientForMarketOrder(OrderRequestDto order, UUID userId) throws IOException, BuyOrderPriceCannotBeMatched, NoWalletFound {
        Optional<Wallet> userWallet = getWalletBalance(userId);
        double walletBalance;
        if (userWallet.isPresent()) {
            walletBalance = userWallet.get().getAmount();
            // since it's a MARKET ORDER you are going all in based on your account balance
            order.setPrice(walletBalance);
            log.info("Market data based on Product Ticker");
            marketDataService.getProductByTicker(order.getProduct()).forEach(System.out::println);
            Stream<TradeInfo> products =  marketDataService.getProductByTicker(order.getProduct());
            boolean isQuotedPriceValid = products.anyMatch(product -> order.getPrice() >= product.bidPrice() - product.maxPriceShift());
            if (isQuotedPriceValid) {
                log.info("Your MARKET order has chance of being fulfilled, Account Balance is sufficient for {} Stock", order.getProduct());
                return true;
            }
            log.info("Your MARKET account balanced can't be matched on Exchange");
            throw new BuyOrderPriceCannotBeMatched();

        }
        log.info("No wallet found for User {}", userId);
        throw  new NoWalletFound(userId);
    }

    public boolean validateBuyOrderWithMarket(OrderRequestDto order, UUID userId) throws IOException, BuyOrderPriceCannotBeMatched, BuyLimitExceededException, NoWalletFound {
        // Check the quantity is available and check the wallet has sufficient amount
        log.info("Validating a BUY order with MARKET");
        Stream<TradeInfo> products =  marketDataService.getProductByTicker(order.getProduct());
        log.info("Current Market data for {}", order.getProduct());
        products.forEach(System.out::println);
        return  isBuyQuantityValid(order) && isWalletBalanceSufficientForMarketOrder(order, userId);
    }


    public void makeAnOrder(UUID  userId, UUID portfolioId, OrderRequestDto order) throws Exception {
        log.info("Processing order for user with id {}", userId);
        if (order.getSide() == OrderSide.BUY) {
            if (order.getType() == OrderType.LIMIT) {
                makeLimitBuyOrder(order, userId, portfolioId);
            }
            makeMarketBuyOrder(order, userId, portfolioId);
        }

        if (order.getSide() == OrderSide.SELL) {
            if (order.getType() == OrderType.LIMIT) {
                makeSellLimitOrder(order, userId, portfolioId);
            }
            makeSellMarketOrder(order, userId, portfolioId);
        }
        makeLimitBuyOrder(order, userId, portfolioId);

    }

    public String makeLimitBuyOrder(OrderRequestDto order, UUID userId, UUID portfolioId) throws IOException,
            BuyLimitExceededException, BuyOrderPriceNotReasonable, InsufficientBalanceException, PortfolioNotFoundException {
        if (validateBuyOrderWithLimit(order, userId)) {
            log.info("Getting Data for LIMIT ORDER,  Open Trades");
           return performBuyTrade(order, userId, portfolioId);
        }

        return "";
    }


    public String makeMarketBuyOrder(OrderRequestDto order, UUID userId, UUID portfolioId) throws BuyLimitExceededException,
            IOException, NoWalletFound, BuyOrderPriceCannotBeMatched, BuyOrderPriceNotReasonable, InsufficientBalanceException, PortfolioNotFoundException {
        if (validateBuyOrderWithMarket(order, userId)) {
            log.info("Getting Data for MARKET ORDER,  Open Trades");
            return performBuyTrade(order, userId, portfolioId);

        }
        return "";
    }


    public String makeSellMarketOrder(OrderRequestDto order, UUID userId, UUID portfolioId) throws StockNotAvailable,
            SellLimitExceededException, IOException {
        if (validateSellMarketOrder(order, userId)) {
            return performSellTrade(order, userId, portfolioId);
        }
        return "";
    }

    public String makeSellLimitOrder(OrderRequestDto order, UUID userId, UUID portfolioId) throws StockNotAvailable, SellLimitExceededException, IOException {
        if (validateSellLimitOrder(order, userId)) {
            return performSellTrade(order, userId, portfolioId);
        }
        return "";
    }



    public  boolean validateSellMarketOrder(OrderRequestDto order, UUID userId) throws IOException, StockNotAvailable, SellLimitExceededException {
        if (canUserSellStock(userId, order)) {
            log.info("Current Market data: ");
            marketDataService.getProductByTicker(order.getProduct()).forEach(System.out::println);
            if (validateSellLimit(order)) {
                log.info("Sell Quantity is possible");
                return true;
            }
        } log.info("You don't have such stock");
        throw new StockNotAvailable();
    }



    public boolean validateSellLimitOrder(OrderRequestDto order, UUID userId) throws StockNotAvailable, IOException, SellLimitExceededException {
        // Selling: check whether client owns the stock
        if (canUserSellStock(userId, order)) {
            log.info("Current Market data: ");
            marketDataService.getProductByTicker(order.getProduct()).forEach(System.out::println);
            if (isSellPriceLimitOrderValid(order)) {
                log.info("Sell Price is possible to be bought since it's between the range");
                return validateSellLimit(order);
            }

        } log.info("You don't have such stock");
        throw new StockNotAvailable();

    }



    private boolean canUserSellStock(UUID portfolioId, OrderRequestDto order) {
        log.info("Validating Sell LIMIT Order By checking if client Owns That Stock");
        log.info("Validating Sell LIMIT Order by checking the price tag the user wants to sell the stock");
        log.info("Current Market Buy Order");
        return stockRepository.findStockEntityByPortfolio_ClientIdAndTicker(portfolioId, order.getProduct())
                .isPresent();
    }

    private boolean isSellPriceLimitOrderValid(OrderRequestDto order) throws IOException {
        Stream<TradeInfo> products =  marketDataService.getProductByTicker(order.getProduct());
        return products.anyMatch(product ->  order.getPrice() >= product.askPrice() - product.maxPriceShift()
                && order.getPrice() <= product.askPrice() + product.maxPriceShift());
    }





    private Boolean validateSellLimit(OrderRequestDto order) throws IOException, SellLimitExceededException {
        log.info("Validating Sell Limit Order against the current Market Data Sell Limit!");
        Stream<TradeInfo> products =  marketDataService.getProductByTicker(order.getProduct());
        log.info("Current Market Sell limit");
        boolean verified = products.anyMatch(tradeInfo -> tradeInfo.sellLimit() >= order.getQuantity());
        if (verified) {
            log.info("Sell Quantity is valid");
            return true;
        } log.info("You can't sell your stock at that threshold");
        throw new SellLimitExceededException();

    }



    public void executeOrder(OrderRequestDto order, String exchangeUrl, UUID portfolioId, UUID userId) throws PortfolioNotFoundException {
        log.info("Executing the order! ********************");
        if (order.getLegId().isEmpty()) {
            order.setLegId(UUID.randomUUID().toString());
        }
        log.info("Saving created order into database");
        OrderEntity orderEntity = createOrderEntity(order, portfolioId, userId);
        orderRepository.save(orderEntity);
        log.info("Saved");
       String response = webClient.post()
                .uri(exchangeUrl + APIKEY.KEY.getKey() + "/order")
                .body(Mono.just(order), order.getClass())
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> {
                    if (e instanceof WebClientResponseException) {
                        log.info(
                                "Response body:    " + ((WebClientResponseException) e).getResponseBodyAsString()
                        );
                    }
                })
                .onErrorReturn("").block();
       log.info("Order Executed! You will be notified shortly, OrderID is ------>  {}", response);
    }


    public String performBuyTrade(OrderRequestDto order, UUID userId, UUID portfolioId) throws IOException, BuyLimitExceededException, BuyOrderPriceNotReasonable, InsufficientBalanceException, PortfolioNotFoundException {
            marketDataService.findOpenTrades(order.getProduct(),
                    OrderSide.SELL.name(), order.getType().name()).forEach(System.out::println);

            // check the exchange to perform Trade on
            List<String> tradeInfo =  marketDataService.getProductByTicker(order.getProduct()).
                    filter(product -> order.getPrice() >=  product.bidPrice() - product.maxPriceShift()
                            && order.getPrice() <= product.bidPrice() + product.maxPriceShift()).
                    filter(product -> product.buyLimit() >= order.getQuantity()).map(TradeInfo::exchangeUrl).toList();

            Stream<Trade> openTrades = null;
            if (tradeInfo.size() == 2) {
                // the two exchanges are valid so get Trades from both
                openTrades = marketDataService.findOpenTrades(order.getProduct(),
                        OrderSide.SELL.name(), order.getType().name());
            } else if (tradeInfo.size() == 1) {
                openTrades = marketDataService.findOpenTrades(order.getProduct(), OrderSide.SELL.name(),
                        order.getType().name(), tradeInfo.get(0));
            }
            tradeInfo.forEach(System.out::println);
            // since it is a limit price make sure the available orders that have at most the price the client has
            Optional<Trade>  trade = openTrades.filter(product ->  product.getPrice() <= order.getPrice())
                    // get the minimum price from open trades
                    .min(Comparator.comparingDouble(Trade::getPrice));

            // check if order can be made
            if (trade.isPresent()) {
                Trade openOrder = trade.get();
                log.info("Order can be made on this exchange {}", openOrder);
                log.info("URL  {}", openOrder.getExchangeUrl());
                return openOrder.getExchangeUrl();


            }
            log.info("Order can't be made, there is no match with your price");
            log.info("Holding order for best Trade,  You cancel Cancel or Update to increase chance of fulfilling");
            return "";
    }

    public String performSellTrade(OrderRequestDto order, UUID userId, UUID portfolioId) throws IOException {
        marketDataService.findOpenTrades(order.getProduct(),
                OrderSide.BUY.name(), order.getType().name()).forEach(System.out::println);

        // check the exchange to perform Trade on
        List<String> tradeInfo =  marketDataService.getProductByTicker(order.getProduct()).
                filter(product -> order.getPrice() >=  product.askPrice() - product.maxPriceShift()
                        && order.getPrice() <= product.askPrice() + product.maxPriceShift()).
                filter(product -> product.sellLimit() >= order.getQuantity()).map(TradeInfo::exchangeUrl).toList();

        Stream<Trade> openTrades = null;
        if (tradeInfo.size() == 2) {
            // the two exchanges are valid so get Trades from both
            openTrades = marketDataService.findOpenTrades(order.getProduct(),
                    OrderSide.BUY.name(), order.getType().name());
        } else if (tradeInfo.size() == 1) {
            openTrades = marketDataService.findOpenTrades(order.getProduct(), OrderSide.BUY.name(),
                    order.getType().name(), tradeInfo.get(0));
        }
        tradeInfo.forEach(System.out::println);
        // since it is a limit price make sure the available orders that have at most the price the client has
        Optional<Trade>  trade = openTrades.filter(product ->  product.getPrice() <= order.getPrice())
                // get the minimum price from open trades
                .max(Comparator.comparingDouble(Trade::getPrice));

        // check if order can be made
        if (trade.isPresent()) {
            Trade openOrder = trade.get();
            log.info("Order can be made on this exchange {}", openOrder);
            log.info("URL  {}", openOrder.getExchangeUrl());
            return openOrder.getExchangeUrl();


        }
        log.info("Order can't be made, there is no match with your price");
        log.info("Holding order for best Trade,  You cancel Cancel or Update to increase chance of fulfilling");
        return "";

    }



}
