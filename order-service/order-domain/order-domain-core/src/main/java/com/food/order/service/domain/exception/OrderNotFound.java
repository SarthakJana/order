package com.food.order.service.domain.exception;

public class OrderNotFound extends DomainException{
    public OrderNotFound(String message) {
        super(message);
    }

    public OrderNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
