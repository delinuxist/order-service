package com.tradingengine.orderservice.utils;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
//@RequiredArgsConstructor
@Slf4j
public class WebClientService {
    private final WebClient webClient;

    @Value("${MalOne.url}")
    private String urlOne;
    @Value("${MalTwo.url}")
    private String urlTwo;

    @Value("${MalOne.apiKey}")
    private String apiKeyOne;
    @Value("${MalTwo.apiKey}")
    private String apiKeyTwo;

    @Autowired
    public WebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String placeOrderOnExchangeAndGetID(OrderRequestToExchange orderRequestToExchange, String exchangeUrl) {
        String apiKey = getApiKeyForExchange(exchangeUrl);
        return webClient.post()
                .uri(exchangeUrl + apiKey + "/order")
                .body(Mono.just(orderRequestToExchange), OrderRequestToExchange.class)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> response.replaceAll("\"", ""))
                .doOnError(throwable -> log.info("Error occurred during executing order "))
                .onErrorReturn("")
                .block();
    }


    //todo: should return just the status of the order? save orderStatus response dto to db?
    public OrderStatusResponseDto checkOrderStatus(String orderId, String exchangeUrl) {
        String apiKey = getApiKeyForExchange(exchangeUrl);
        log.info("{}", apiKey);
               return webClient.get()
                .uri(exchangeUrl + apiKey + "/order/" + orderId.replaceAll("\"", ""))
                .retrieve()
                .bodyToMono(OrderStatusResponseDto.class)
                .doOnError(throwable -> log.info("Error when checking order status"))
                .onErrorReturn(null)
                .block();

    }


    public Boolean modifyOrderById(UUID orderId, OrderRequestToExchange orderRequestToExchange, String exchangeUrl) {
        String apiKey = getApiKeyForExchange(exchangeUrl);
        log.info("Order to be sent    {}", orderRequestToExchange);
        return webClient.put()
                .uri(exchangeUrl + apiKey + "/order/" + orderId)
                .body(Mono.just(orderRequestToExchange), OrderRequestToExchange.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnError(throwable -> log.info("Error occurred during order update"))
                .onErrorReturn(false)
                .block();
    }


    public Boolean cancelOrder(String orderId, String exchangeUrl) {
        String apiKey = getApiKeyForExchange(exchangeUrl);
        return webClient.get()
                .uri(exchangeUrl + apiKey + "/order/" + orderId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnError(throwable -> log.info("Error occurred during order cancellation"))
                .onErrorReturn(false).block();

    }

    //get api key for each exchange
    private String getApiKeyForExchange(String exchangeUrl) {
        if (exchangeUrl.equals(urlOne)) {
            return apiKeyOne;
        } else {
            return apiKeyTwo;
        }
    }
}



















