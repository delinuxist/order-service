package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.dto.PortfolioRequestDto;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.service.impl.PortfolioServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/order/portfolio")
public class PortfolioController {

    private final PortfolioServiceImpl portfolioServiceImpl;

    PortfolioController(PortfolioServiceImpl portfolioServiceImpl) {
        this.portfolioServiceImpl = portfolioServiceImpl;
    }

    // create portfolio
    @PostMapping
    public ResponseEntity<PortfolioEntity> createPortfolio(
            @RequestBody
            @Validated PortfolioRequestDto portfolioRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(portfolioServiceImpl.createPortfolio(portfolioRequestDto));
    }

    // fetch all porfolios
    @GetMapping
    public ResponseEntity<List<PortfolioEntity>> fetchAllPortfolios() {
        return ResponseEntity.ok(portfolioServiceImpl.fetchAllPortfolios());
    }

    // fetch portfolio by portfolioId
    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioEntity> fetchPortfolioById(
            @PathVariable("portfolioId")
            Long portfolioId
    ) throws PortfolioNotFoundException {
        return ResponseEntity
                .ok()
                .body(portfolioServiceImpl.fetchPortfolioById(portfolioId));
    }

    // update portfolio
    @PatchMapping("/{portfolioId}")
    public ResponseEntity<PortfolioEntity> updatePortfolio(
            @PathVariable("portfolioId")
            Long portfolioId,
            @RequestBody @Validated PortfolioRequestDto portfolioRequestDto
    ) throws PortfolioNotFoundException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        portfolioServiceImpl.updatePortfolio(portfolioId, portfolioRequestDto)
                );
    }

    // delete portfolio
    @DeleteMapping("/{portfolioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortfolio(
            @PathVariable("portfolioId")
            Long portfolioId
    ) throws PortfolioNotFoundException {
        portfolioServiceImpl.deletePortfolio(portfolioId);
    }
}
