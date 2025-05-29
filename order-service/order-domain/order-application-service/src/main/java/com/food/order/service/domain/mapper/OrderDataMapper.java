package com.food.order.service.domain.mapper;

import com.food.order.service.domain.dto.create.CreateOrderCommand;
import com.food.order.service.domain.dto.create.CreateOrderResponse;
import com.food.order.service.domain.dto.create.OrderAddressDto;
import com.food.order.service.domain.dto.create.OrderItemDto;
import com.food.order.service.domain.entity.Order;
import com.food.order.service.domain.entity.OrderItem;
import com.food.order.service.domain.entity.Product;
import com.food.order.service.domain.entity.Restaurant;
import com.food.order.service.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getOrderItemDtos().stream()
                        .map(item -> new Product(new ProductId(item.getProductId())))
                        .collect(Collectors.toList()))
                .build();
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .price(new Money(createOrderCommand.getPrice()))
                .deliveryAddress(orderAddressToDeliveryAddress(createOrderCommand.getOrderAddress()))
                .items(orderItemsToOrderItemsEntity(createOrderCommand.getOrderItemDtos()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemsEntity(List<OrderItemDto> orderItemDtos) {
        return orderItemDtos.stream()
                .map(item ->
                        OrderItem.builder()
                                .product(new Product(new ProductId(item.getProductId())))
                                .quantity(item.getQuantity())
                                .price(new Money(item.getPrice()))
                                .subTotal(new Money(item.getSubTotal()))
                                .build())
                .collect(Collectors.toList());
    }

    private StreetAddress orderAddressToDeliveryAddress(OrderAddressDto orderAddress) {
        return new StreetAddress(UUID.randomUUID(),
                orderAddress.getCity(),
                orderAddress.getPostalCode(),
                orderAddress.getStreet());
    }
}
