package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.entity.StockEntity;
import com.tradingengine.orderservice.repository.PortfolioRepository;
import com.tradingengine.orderservice.service.PortfolioService;
import com.tradingengine.orderservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;
    private final PortfolioService portfolioService;
    private final PortfolioRepository portfolioRepository;

    @GetMapping("/{portfolioId}/{ticker}")
    public StockEntity getStockByPortfolioIdAndTicker(@PathVariable("portfolioId")
                                       UUID portfolioId, @PathVariable("ticker") String ticker ) {
        Optional<PortfolioEntity> checkPortfolio = portfolioRepository.findByPortfolioId(portfolioId);
        if (checkPortfolio.isPresent()) {
            return stockService.fetchStockByPortFolioAndTicker(checkPortfolio.get(), ticker);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such stock Available");
    }
}


