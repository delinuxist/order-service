package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.dto.PorfolioRequestDto;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.service.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/order/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // create portfolio
    @PostMapping
    public ResponseEntity<PortfolioEntity> createPortfolio(
            @RequestBody
            @Validated PorfolioRequestDto porfolioRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(portfolioService.createPorfolio(porfolioRequestDto));
    }

    // fetch all porfolios
    @GetMapping
    public ResponseEntity<List<PortfolioEntity>> fetchAllPortfolios() {
        return ResponseEntity.ok(portfolioService.fetchAllPorfolios());
    }

    // fetch portfolio by portfolioId
    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioEntity> fetchPortfolioById(
            @PathVariable("portfolioId")
            Long portfolioId
    ) throws PortfolioNotFoundException {
        return ResponseEntity
                .ok()
                .body(portfolioService.fetchPortfolioById(portfolioId));
    }

    // update portfolio
    @PatchMapping("/{portfolioId}")
    public ResponseEntity<PortfolioEntity> updatePortfolio(
            @PathVariable("portfolioId")
            Long portfolioId,
            @RequestBody @Validated PorfolioRequestDto porfolioRequestDto
    ) throws PortfolioNotFoundException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        portfolioService.updatePortfolio(portfolioId,porfolioRequestDto)
                );
    }

    // delete portfolio
    @DeleteMapping("/{portfolioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortfolio(
            @PathVariable("portfolioId")
            Long portfolioId
    ) throws PortfolioNotFoundException {
        portfolioService.deletePortfolio(portfolioId);
    }
}
