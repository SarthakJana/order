package com.food.order.service.domain.valueobject;

import java.util.UUID;

public class OrderId extends BaseId<UUID>{

    public OrderId(UUID value) {
        super(value);
    }
}
