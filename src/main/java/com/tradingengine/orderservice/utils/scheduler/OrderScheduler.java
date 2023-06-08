package com.tradingengine.orderservice.utils.scheduler;

import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.OrderLeg;
import com.tradingengine.orderservice.entity.StockEntity;
import com.tradingengine.orderservice.entity.Wallet;
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.repository.WalletRepository;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.service.StockService;
import com.tradingengine.orderservice.utils.ModelBuilder;
import com.tradingengine.orderservice.utils.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderScheduler {

    private final OrderService orderService;
    private final StockService stockService;
    private final WebClientService webClientService;
    private final WalletRepository walletRepository;

    private final ModelBuilder builder;


    @Scheduled(cron = "*/20 * * * * *")
    private void orderStatusUpdater() {
        List<OrderEntity> orderEntities = orderService.getAllOrderEntities();

        for (OrderEntity orderEntity : orderEntities) {
            List<OrderLeg> orderLegsOwnedByOrder = orderEntity.getOrderLegsOwnedByEntity();

            //if all order legs are fulfilled, order itself is fulfilled, else, etc
            boolean allOrderLegsFulfilled = orderLegsOwnedByOrder
                    .stream()
                    .allMatch(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.FILLED));

            boolean allOrderLegsFailed = orderLegsOwnedByOrder
                    .stream()
                    .allMatch(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.FAILED));

            boolean someOrderLegsSucceeded = orderLegsOwnedByOrder
                    .stream()
                    .anyMatch(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.FILLED));

            if (allOrderLegsFulfilled) {
                orderEntity.setStatus(OrderStatus.FILLED);
            } else if (allOrderLegsFailed) {
                orderEntity.setStatus(OrderStatus.PENDING);
            } else if (someOrderLegsSucceeded) {
                orderEntity.setStatus(OrderStatus.PARTIAL_FILL);
            } else {
                orderEntity.setStatus(OrderStatus.OPEN);
            }
            orderService.saveOrderEntity(orderEntity);
        }
    }

    @Scheduled(cron = "*/20 * * * * *")
    private void orderLegsStatusUpdater() {
        List<OrderLeg> orderLegs = orderService.getAllOpenOrderLegs();

        orderLegs.forEach(orderLeg -> {
                    if (orderLeg.getOrderLegStatus().equals(OrderStatus.OPEN)) {
                        OrderStatusResponseDto orderStatus = webClientService.checkOrderStatus(orderLeg.getIdFromExchange(), orderLeg.getExchangeUrl());
                        if (orderStatus.getProduct() != null) {

                            if (orderStatus.getQuantity().equals(orderStatus.getCumulatitiveQuantity())) {
                                orderService.updateOrderLegStatus(orderLeg, OrderStatus.FILLED);

                                log.info("order is filled, and of type sell, adding money to user wallet");
                                if (orderLeg.getOrderSide().equals(OrderSide.SELL)) {
                                    Optional<Wallet> checkWallet = walletRepository.findByUserId(orderLeg.getOrderEntity().getUserId());
                                    if (checkWallet.isPresent()) {
                                        Wallet wallet = checkWallet.get();
                                        wallet.setAmount(wallet.getAmount() + (orderLeg.getPrice() * orderLeg.getQuantity()));
                                    }
                                }

                                createStock(orderLeg.getOrderEntity());
                            }
                        }

                    }
                }
        );
    }

    private void createStock(OrderEntity order) {
        StockEntity stock = stockService.findByPortfolioAndTickerAndUserId(order.getPortfolio(), order.getProduct(), order.getUserId());

        if (stock != null) {
            if (order.getOrderSide().equals(OrderSide.SELL)) {
                stock.setQuantity(stock.getQuantity() - order.getQuantity());
                stock.setPrice(stock.getPrice() - order.getPrice());
            } else {
                stock.setQuantity(stock.getQuantity() + order.getQuantity());
                stock.setPrice(stock.getPrice() + order.getPrice());
            }
        } else {
            stock = builder.buildStockEntity(order);
        }
        stockService.saveStock(stock);
    }


}
