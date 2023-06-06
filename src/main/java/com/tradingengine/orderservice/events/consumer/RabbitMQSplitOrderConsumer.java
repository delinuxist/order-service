package com.tradingengine.orderservice.events.consumer;

import com.tradingengine.orderservice.dto.RedisOrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMQSplitOrderConsumer {
    @RabbitListener(queues = {"${rabbitmq.queue.split.name}"})
    public void consume(RedisOrderDto message) {
        log.info("Logging information entering Split consumer");
        log.info("Message received -> {}", message);
    }
}
