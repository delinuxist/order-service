package com.tradingengine.orderservice.utils.scheduler;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.entity.OrderLeg;
import com.tradingengine.orderservice.entity.Wallet;
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.repository.WalletRepository;
import com.tradingengine.orderservice.service.OrderService;
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
public class FailedOrdersProcessor {
    private final OrderService orderService;
    private final WebClientService webClientService;
    private final ModelBuilder builder;
    private final WalletRepository walletRepository;

    @Scheduled(cron = "*/20 * * * * *")
    public void processFailedOrderLegs() {
        List<OrderLeg> orderLegList = orderService.getAllOrderLegs()
                .stream().filter(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.FAILED))
                .toList();

        for (OrderLeg orderLeg : orderLegList) {

            for(int i = 0; i <= 2; i++) { placeFailedOrderAgainOnExchange(orderLeg); }

            if (orderLeg.getOrderLegStatus().equals(OrderStatus.FAILED)) {
                if (orderLeg.getOrderSide().equals(OrderSide.BUY)) {
                    Optional<Wallet> checkWallet = walletRepository.findByUserId(orderLeg.getOrderEntity().getUserId());
                    if (checkWallet.isPresent()) {
                        Wallet wallet = checkWallet.get();
                        wallet.setAmount(wallet.getAmount() + (orderLeg.getPrice() * orderLeg.getQuantity()));
                    }
                }
            }

        }
    }

    private void placeFailedOrderAgainOnExchange(OrderLeg orderLeg) {
            OrderRequestToExchange orderRequest = builder.rebuildOrderRequest(orderLeg.getProduct(), orderLeg.getQuantity(), orderLeg.getPrice(), orderLeg.getOrderSide(), orderLeg.getType());
            log.info("{}", orderRequest);
            log.info("{}", orderLeg.getExchangeUrl());
            String response = webClientService.placeOrderOnExchangeAndGetID(orderRequest, orderLeg.getExchangeUrl());

            OrderStatus orderStatus = response.equals("") ? OrderStatus.FAILED : OrderStatus.OPEN;
            orderLeg.setOrderLegStatus(orderStatus);
            orderService.saveOrderLeg(orderLeg);
    }
}
