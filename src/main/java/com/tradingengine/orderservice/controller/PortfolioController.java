package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.dto.PortfolioRequestDto;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.exception.portfolio.PortfolioDeletionFailedException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.service.PortfolioService;
import com.tradingengine.orderservice.service.StockService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/order/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;


    // create portfolio
    @PostMapping
    public ResponseEntity<PortfolioEntity> createPortfolio(
            @RequestBody
            @Validated PortfolioRequestDto portfolioRequestDto, HttpServletRequest request) {
        String userId = request.getHeader("userid");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(portfolioService.createPortfolio(portfolioRequestDto,userId));
    }

    // fetch all portfolios
//    @GetMapping
//    public ResponseEntity<List<PortfolioEntity>> fetchAllPortfolios() {
//        return ResponseEntity.ok(portfolioService.fetchAllPortfolios());
//    }

    // fetch portfolio by portfolioId
    @GetMapping("")
    public ResponseEntity<List<PortfolioEntity>> fetchPortfoliosById(
           HttpServletRequest request
    )  {
        String userId = request.getHeader("userid");
        return ResponseEntity
                .ok()
                .body(portfolioService.fetchPortfoliosByUserId(UUID.fromString(userId)));
    }

    // update portfolio
    @PatchMapping("/{portfolioId}")
    public ResponseEntity<PortfolioEntity> updatePortfolio(
            @PathVariable("portfolioId")
            UUID portfolioId,
            @RequestBody @Validated PortfolioRequestDto portfolioRequestDto
    ) throws PortfolioNotFoundException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        portfolioService.updatePortfolio(portfolioId, portfolioRequestDto)
                );
    }

    // delete portfolio
    @DeleteMapping("/{portfolioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortfolio(
            @PathVariable("portfolioId")
            UUID portfolioId
    ) throws PortfolioNotFoundException, PortfolioDeletionFailedException {
        portfolioService.deletePortfolio(portfolioId);
    }

}
