package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.dto.RedisOrderDto;
import com.tradingengine.orderservice.events.publisher.RabbitMQProducer;
import com.tradingengine.orderservice.events.publisher.RabbitMQSplitOrderProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    @Autowired
    private RabbitMQProducer producer;

    @Autowired
    private RabbitMQSplitOrderProducer producer2;

    @GetMapping("/publish")
    public String sendMessage(@RequestParam("message") String message) {
        producer.sendMessage(message);
        return "Message sent to RabbitMQ ...";

    }

    @PostMapping("/publish2")
    public String sendObject(@RequestBody RedisOrderDto order) {
       RedisOrderDto message = RedisOrderDto.builder().price(200.0).build();
        producer2.sendObject(order);
        return "Message sent to RabbitMQ ...";

    }
}
