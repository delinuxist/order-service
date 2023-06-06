package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.exception.order.OrderModificationFailureException;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    

    @GetMapping("/getOrder/{orderId}")
    public OrderEntity getOrderById(@PathVariable("orderId") UUID orderId) throws OrderNotFoundException {
        return orderService.fetchOrderById(orderId);
    }

    @GetMapping("/checkStatus/{orderId}")
    public OrderStatusResponseDto checkOrderStatus(@PathVariable("orderId") UUID orderId) throws OrderNotFoundException {
        return orderService.checkOrderStatus(orderId);
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


//    @PostMapping("/{userId}/{portfolioId}")
//    public void createAnOrder(@PathVariable("portfolioId") UUID portfolioId, @PathVariable("userId") UUID userId,
//                              @Validated @RequestBody OrderRequestToExchange orderRequestToExchange) throws Exception {
//        orderService.TryAnOrder(userId, portfolioId, orderRequestToExchange);
//    }


}
