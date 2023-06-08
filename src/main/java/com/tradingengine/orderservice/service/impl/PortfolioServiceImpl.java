package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.dto.PortfolioRequestDto;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.repository.PortfolioRepository;
import com.tradingengine.orderservice.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public PortfolioEntity createPortfolio(PortfolioRequestDto portfolioRequestDto,String userId) {
        PortfolioEntity portfolio = PortfolioEntity.builder()
                .name(portfolioRequestDto.name()).clientId(UUID.fromString(userId))
                .build();
        simpMessagingTemplate.convertAndSend("/portfolio","sent");
        return portfolioRepository.save(portfolio);
    }

    public List<PortfolioEntity> fetchAllPortfolios() {
        return portfolioRepository.findAll();
    }

    public List<PortfolioEntity> fetchPortfoliosByUserId(UUID clientId) {
       return portfolioRepository.findAllByClientId(clientId);
    }

    public PortfolioEntity updatePortfolio(
            UUID portfolioId,
            PortfolioRequestDto portfolioRequestDto
    ) throws PortfolioNotFoundException {
        Optional<PortfolioEntity> portfolio = portfolioRepository.findById(portfolioId);
        if (portfolio.isEmpty()) {
            throw new PortfolioNotFoundException(portfolioId);
        }
        portfolio.get().setName(portfolioRequestDto.name());
        simpMessagingTemplate.convertAndSend("/portfolio","sent");
        return portfolioRepository.save(portfolio.get());
    }

    public void deletePortfolio(
            UUID portfolioId
    ) throws PortfolioNotFoundException {
       Optional<PortfolioEntity> portfolio = portfolioRepository.findById(portfolioId);

        if (portfolio.isEmpty()) {
            // throw exception
            throw new PortfolioNotFoundException(portfolioId);
        }

        portfolioRepository.deleteById(portfolioId);
        simpMessagingTemplate.convertAndSend("/portfolio","sent");
    }

    @Override
    public PortfolioEntity getPortfolioById(UUID portfolioId) throws PortfolioNotFoundException {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
    }

}


