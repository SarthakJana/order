package com.food.order.service.domain.event.publisher;

import com.food.order.service.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);

}
