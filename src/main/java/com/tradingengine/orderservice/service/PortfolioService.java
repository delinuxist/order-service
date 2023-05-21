package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.dto.PorfolioRequestDto;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }


    public PortfolioEntity createPorfolio(PorfolioRequestDto porfolioRequestDto) {
        PortfolioEntity portfolio = PortfolioEntity.builder()
                .name(porfolioRequestDto.name())
                .build();
        return portfolioRepository.save(portfolio);
    }

    public List<PortfolioEntity> fetchAllPorfolios() {
        return portfolioRepository.findAll();
    }

    public PortfolioEntity fetchPortfolioById(Long portfolioId) throws PortfolioNotFoundException {
       Optional<PortfolioEntity> portfolio = portfolioRepository.findById(portfolioId);
       if(portfolio.isEmpty()) {
           throw new PortfolioNotFoundException("Portfolio with id: "+portfolioId+" not found");
       }
       return portfolio.get();
    }

    public PortfolioEntity updatePortfolio(Long portfolioId, PorfolioRequestDto porfolioRequestDto) throws PortfolioNotFoundException {
      Optional<PortfolioEntity> portfolio = portfolioRepository.findById(portfolioId);
      if(portfolio.isEmpty()){
          throw new PortfolioNotFoundException("Portfolio with id: "+portfolioId+" not found");
      }
      portfolio.get().setName(porfolioRequestDto.name());
      return portfolioRepository.save(portfolio.get());
    }

    public void deletePortfolio(Long portfolioId) throws PortfolioNotFoundException {
        if(portfolioRepository.findById(portfolioId).isEmpty()){
            // throw exception
            throw new PortfolioNotFoundException("Portfolio with id: "+portfolioId+" not found");
        }
        portfolioRepository.deleteById(portfolioId);
    }


}


