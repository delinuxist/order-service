package com.tradingengine.orderservice.events.publisher;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.dto.RedisOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQSplitOrderProducer {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.split.key}")
    private String newRoutingKey;

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void sendObject(RedisOrderDto order) {
        log.info("Our java object received  {}", order);
        rabbitTemplate.convertAndSend(exchange, newRoutingKey, order);
    }


}
