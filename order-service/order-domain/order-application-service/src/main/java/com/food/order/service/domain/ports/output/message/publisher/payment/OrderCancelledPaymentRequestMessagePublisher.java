package com.food.order.service.domain.ports.output.message.publisher.payment;

import com.food.order.service.domain.event.OrderCancelledEvent;
import com.food.order.service.domain.event.publisher.DomainEventPublisher;

public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
