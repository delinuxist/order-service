package com.tradingengine.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MapCollectionBean {

    @Bean
    public Map<String, String> getTickerIndexMap () {
        HashMap<String, String> map = new HashMap<>();
        map.put("MSFT", "microsoft");
        map.put("NFLX", "netflix");
        map.put("GOOGL", "google");
        map.put("AAPL", "apple");
        map.put("TSLA", "tesla");
        map.put("IBM", "ibm");
        map.put("ORCL", "orcl");
        map.put("AMZN", "amazon");
        return map;
    }

}
