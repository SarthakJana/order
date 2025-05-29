package com.food.order.service.domain.ports.output.message.publisher.payment;

import com.food.order.service.domain.event.OrderCreatedEvent;
import com.food.order.service.domain.event.publisher.DomainEventPublisher;

public interface OrderCreatedPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCreatedEvent> {
}
