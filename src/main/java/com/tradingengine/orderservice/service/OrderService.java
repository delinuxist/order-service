package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.repository.OrderRepository;
import com.tradingengine.orderservice.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Value("${exchange.one}/order")
    private String exchange1;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PortfolioRepository portfolioRepository;


    public OrderEntity placeOrder(Long portfolioId, OrderRequestDto orderRequestDto) throws PortfolioNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<OrderRequestDto> entity = new HttpEntity<OrderRequestDto>(orderRequestDto,headers);

         UUID orderId =  restTemplate.exchange(exchange1,HttpMethod.POST,entity,UUID.class).getBody();

         Optional<PortfolioEntity> portfolio = portfolioRepository.findById(portfolioId);

         if(portfolio.isEmpty()) {
             throw new PortfolioNotFoundException("Portfolio with id: "+portfolioId+" not found");
         }

        // create order object to be saved in db
        OrderEntity order = OrderEntity.builder()
                .order_id(orderId)
                .portfolio(portfolio.get())
                .price(orderRequestDto.price())
                .product(orderRequestDto.product())
                .side(orderRequestDto.side())
                .status(OrderStatus.PENDING)
                .quantity(orderRequestDto.quantity())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .type(orderRequestDto.type())
                .client_id(null)
                .build();
        return orderRepository.save(order);
    }


    public List<OrderEntity> getAllOrders(){
        return orderRepository.findAll();
    }

    public String cancelOrder(UUID order_id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<UUID> entity = new HttpEntity<UUID>(order_id);

        if (!orderRepository.existsById(order_id)){
            throw new IllegalArgumentException("Order not found with this id:" + order_id);
        }
        Boolean result = restTemplate.exchange(exchange1, HttpMethod.DELETE, entity, Boolean.class).getBody();
        if(result.equals(true)){
            return "Order with id:" + order_id + " has been cancelled";
        }
        return "Cancellation failed";
    }


//    public OrderEntity placeNewOrder (Long portfolio_id, OrderRequestDto orderRequestDto){
//        Http
//
//    }
}
