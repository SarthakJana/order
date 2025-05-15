package com.food.order.service.domain.entity;

import com.food.order.service.domain.valueobject.Money;
import com.food.order.service.domain.valueobject.OrderId;
import com.food.order.service.domain.valueobject.OrderItemId;

public class OrderItem extends BaseEntity<OrderItemId> {

    private OrderId orderId;
    private final Product product;
    private final int quantity;
    private final Money price;
    private final Money subTotal;

    private OrderItem(Builder builder) {
        super.setId(builder.orderItemId);
        product = builder.product;
        quantity = builder.quantity;
        price = builder.price;
        subTotal = builder.subTotal;
    }

    boolean isPriceValid() {
        return price.isGreaterThanZero()
                && product.isPriceValid()
                && price.equals(product.getPrice())
                && price.multiply(quantity).equals(subTotal);
    }

    void initiatizeOrderItem(OrderId orderId, OrderItemId orderItemId) {
        this.orderId = orderId;
        super.setId(orderItemId);
    }

    public static Builder builder() {
        return new Builder();
    }


    public OrderId getOrderId() {
        return orderId;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getPrice() {
        return price;
    }

    public Money getSubTotal() {
        return subTotal;
    }

    public static final class Builder {
        private OrderItemId orderItemId;
        private Product product;
        private int quantity;
        private Money price;
        private Money subTotal;

        private Builder() {
        }

        public Builder id(OrderItemId orderItemId) {
            orderItemId = orderItemId;
            return this;
        }

        public Builder product(Product product) {
            product = product;
            return this;
        }

        public Builder quantity(int quantity) {
            quantity = quantity;
            return this;
        }

        public Builder price(Money price) {
            price = price;
            return this;
        }

        public Builder subTotal(Money subTotal) {
            subTotal = subTotal;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
