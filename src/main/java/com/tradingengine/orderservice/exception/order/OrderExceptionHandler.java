package com.tradingengine.orderservice.exception.order;

import com.tradingengine.orderservice.controller.OrderController;
import com.tradingengine.orderservice.exception.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackageClasses = {OrderController.class})
public class OrderExceptionHandler extends ResponseEntityExceptionHandler{


    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorMessage> orderNotFoundException(
            OrderNotFoundException exception){

        ErrorMessage message = ErrorMessage.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }
    @ExceptionHandler(OrderModificationFailureException.class)
    public ResponseEntity<ErrorMessage> OrderModificationFailureException(
            OrderModificationFailureException exception){

        ErrorMessage message = ErrorMessage.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }
}
