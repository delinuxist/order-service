package com.tradingengine.orderservice.exception.portfolio;

import com.tradingengine.orderservice.controller.PortfolioController;
import com.tradingengine.orderservice.exception.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackageClasses = {PortfolioController.class})
public class PortfolioExceptionHandler extends ResponseEntityExceptionHandler {
        @ExceptionHandler(PortfolioNotFoundException.class)
        public ResponseEntity<ErrorMessage> portfolioNotFoundException(
                PortfolioNotFoundException exception
        ){
            ErrorMessage message = ErrorMessage.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(exception.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
}
