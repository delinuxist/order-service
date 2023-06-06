package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping(path = "/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/{portfolioId}/{ticker}")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void getStockByPortfolioId( @PathVariable("portfolioId")
                                       UUID portfolioId, @PathVariable("userId") String ticker ) {
        stockService.fetchStockByPortfolioIdAndTicker(portfolioId, ticker);
    }
}


