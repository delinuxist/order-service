package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.dto.PortfolioRequestDto;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.repository.PortfolioRepository;
import com.tradingengine.orderservice.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioEntity createPortfolio(PortfolioRequestDto portfolioRequestDto) {
        PortfolioEntity portfolio = PortfolioEntity.builder()
                .name(portfolioRequestDto.name())
                .build();
        return portfolioRepository.save(portfolio);
    }

    public List<PortfolioEntity> fetchAllPortfolios() {
        return portfolioRepository.findAll();
    }

    public PortfolioEntity fetchPortfolioById(Long portfolioId) throws PortfolioNotFoundException {
        Optional<PortfolioEntity> portfolio = portfolioRepository.findById(portfolioId);
        if (portfolio.isEmpty()) {
            throw new PortfolioNotFoundException(portfolioId);
        }
        return portfolio.get();
    }

    public PortfolioEntity updatePortfolio(
            Long portfolioId,
            PortfolioRequestDto portfolioRequestDto
    ) throws PortfolioNotFoundException {
        Optional<PortfolioEntity> portfolio = portfolioRepository.findById(portfolioId);
        if (portfolio.isEmpty()) {
            throw new PortfolioNotFoundException(portfolioId);
        }
        portfolio.get().setName(portfolioRequestDto.name());
        return portfolioRepository.save(portfolio.get());
    }

    public void deletePortfolio(
            Long portfolioId
    ) throws PortfolioNotFoundException {
       Optional<PortfolioEntity> portfolio = portfolioRepository.findById(portfolioId);

        if (portfolio.isEmpty()) {
            // throw exception
            throw new PortfolioNotFoundException(portfolioId);
        }

        portfolioRepository.deleteById(portfolioId);
    }


}


