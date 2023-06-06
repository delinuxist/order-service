package com.tradingengine.orderservice.utils;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientService {
    private final WebClient webClient;
    @Value("${exchange.one.url}")
    private String urlOne;
    @Value("${exchange.two.url}")
    private String urlTwo;

    @Value("${exchange.one.apiKey}")
    private String apiKeyOne;
    @Value("${exchange.two.apiKey}")
    private String apiKeyTwo;


    public UUID placeOrderOnExchangeAndGetID(OrderRequestToExchange orderRequestToExchange, String exchangeUrl) {
        String apiKey = getApiKeyForExchange(exchangeUrl);
        return webClient.post()
                .uri(exchangeUrl + apiKey + "/order")
                .body(Mono.just(orderRequestToExchange), orderRequestToExchange.getClass())
                .retrieve()
                .bodyToMono(UUID.class)
                .doOnError(throwable -> log.info("Error occurred during executing order "))
                .onErrorReturn(UUID.fromString(""))
                .block();
    }


    //todo: what should fallback value be instead of null?
    public OrderStatusResponseDto checkOrderStatus(UUID orderId, String exchangeUrl) {
        String apiKey = getApiKeyForExchange(exchangeUrl);
        return webClient.post()
                .uri(exchangeUrl + apiKey + "/order/{orderId}")
                .retrieve()
                .bodyToMono(OrderStatusResponseDto.class)
                .doOnError(throwable -> log.info("Error when checking order status"))
                .block();
    }


    public Boolean modifyOrderById(UUID orderId, OrderRequestToExchange orderRequestToExchange, String exchangeUrl) {
        String apiKey = getApiKeyForExchange(exchangeUrl);
        return webClient.post()
                .uri(exchangeUrl + apiKey + "/order/{orderId}")
                .body(Mono.just(orderRequestToExchange), orderRequestToExchange.getClass())
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnError(throwable -> log.info("Error occurred during order update"))
                .onErrorReturn(false).block();
    }


    public Boolean cancelOrder(UUID orderId, String exchangeUrl) {
        String apiKey = getApiKeyForExchange(exchangeUrl);
        return webClient.post()
                .uri(exchangeUrl + apiKey + "/order/{orderId}")
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnError(throwable -> log.info("Error occurred during order update"))
                .onErrorReturn(false).block();

    }

    //get api key for each exchange
    private String getApiKeyForExchange(String exchangeUrl) {
        String apiKey;
        if (exchangeUrl.equals(urlOne)) {
            return apiKey = apiKeyOne;
        } else {
            return apiKey = apiKeyTwo;
        }
    }
}



















