package com.tradingengine.orderservice.marketdata.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tradingengine.orderservice.marketdata.models.Product;
import com.tradingengine.orderservice.marketdata.models.ProductInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Repository
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticSearchQuery {
    private Map<String, String> tickerIndexMap;
    @Autowired
    public void setTickerIndexMap(Map<String, String> map) {
        this.tickerIndexMap = map;

    }

    public String getIndexByTicker(String ticker) {
        return tickerIndexMap.get(ticker);
    }

    @Autowired
    private ElasticsearchClient elasticsearchClient;


    public Stream<Product> findOrders(String product, String side) throws  IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(getIndexByTicker(product))
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m.match(t -> t.field("side").query(side))
                        )
                )));
        SearchResponse<Product> search = elasticsearchClient.search(searchRequest, Product.class);
        return  search.hits().hits().stream().map(Hit::source);
    }

    public Stream<Product> findOrders(String product, String side, String orderType) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(getIndexByTicker(product))
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m.match(t -> t.field("side").query(side))
                                ).must(m -> m.match(t -> t.field("orderType").query(orderType)))
                        )));
        SearchResponse<Product> search = elasticsearchClient.search(searchRequest, Product.class);
        return search.hits().hits().stream().map(Hit::source);
    }

    public Stream<ProductInfo> findProductByTicker(String ticker) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("marketdata")
                .query(q -> q
                        .bool(b -> b
                                .must(m -> m.match(t -> t.field("ticker").query(ticker)))
                        )));
        SearchResponse<ProductInfo> search = elasticsearchClient.search(searchRequest, ProductInfo.class);
        return search.hits().hits().stream().map(Hit::source);
    }

}
