package com.food.order.service.domain.ports.output.message.publisher.restaurantapproval;

import com.food.order.service.domain.event.OrderPaidEvent;
import com.food.order.service.domain.event.publisher.DomainEventPublisher;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {

}
