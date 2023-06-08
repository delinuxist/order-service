package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.dto.OrderResponseDto;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.exception.order.OrderModificationFailureException;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.utils.ModelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {


    private final OrderService orderService;

    @PostMapping("/{portfolioId}")
    public void placeOrder(@PathVariable("portfolioId") UUID portfolioId,
                             @RequestBody OrderRequestToExchange orderRequestToExchange) throws Exception {
        log.info("Order Created");
        orderService.processAndPlaceOrder(portfolioId, orderRequestToExchange);
    }

    @GetMapping("/getOrder/{orderId}")
    public OrderResponseDto getOrderById(@PathVariable("orderId") UUID orderId){
        return orderService.fetchOrderById(orderId);
    }

    @GetMapping("/allOrders")
    public List<OrderEntity> getAllOrders() {
        return orderService.fetchAllOrders();
    }

    @GetMapping("/trades/{product}")
    public List<OrderEntity> getOpenTrades(@PathVariable("product") String product) {
        return orderService.fetchAllOpenOrdersForProduct(product);
    }

    @DeleteMapping("/{orderId}")
    public Boolean cancelOrder(@PathVariable("orderId") UUID orderId, String exchangeUrl) throws OrderNotFoundException {
        return orderService.cancelOrder(orderId, exchangeUrl);
    }

    @PutMapping("/{orderId}")
    public Boolean modifyExistingOrder(
            @PathVariable("orderId") UUID orderId,
            @RequestBody OrderRequestToExchange orderRequestToExchange,
            String exchangeUrl
    ) throws OrderNotFoundException, OrderModificationFailureException {
        return orderService.modifyOrder(orderId, orderRequestToExchange, exchangeUrl);
    }


}
