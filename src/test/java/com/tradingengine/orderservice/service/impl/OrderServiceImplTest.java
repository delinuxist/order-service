package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.enums.OrderType;
import com.tradingengine.orderservice.repository.OrderRepository;
import com.tradingengine.orderservice.utils.WebClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    OrderServiceImpl orderService;
    @Mock
    private PortfolioEntity portfolio;

    @Mock
    WebClientService webClientService;

    @Mock
    OrderRequestToExchange orderRequest;

    @Value("${MalOne.url}")
    String exchangeUrl;

    @Test
    void saveOrderEntityToDb() {
        OrderEntity orderEntity = OrderEntity.builder()
                .portfolio(portfolio)
                .type(OrderType.MARKET)
                .price(12.2)
                .quantity(10)
                .build();
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);
        var entity = orderService.saveOrderEntity(orderEntity);

        assertEquals(orderEntity, entity);
        verify(orderRepository, times(1)).save(orderEntity);

    }

    @Test
    void checkOrderStatus() {

    }

    @Test
    void getOrderById() {
    }

    @Test
    void executeOrder() {
        String actualId = (UUID.randomUUID()).toString();
        when(webClientService.placeOrderOnExchangeAndGetID(orderRequest, exchangeUrl)).thenReturn(actualId);

        var returnedUUID = webClientService.placeOrderOnExchangeAndGetID(orderRequest, exchangeUrl);
        assertEquals(actualId, returnedUUID);
    }
}