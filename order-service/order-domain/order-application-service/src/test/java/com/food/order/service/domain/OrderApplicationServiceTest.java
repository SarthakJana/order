package com.food.order.service.domain;

import com.food.order.service.domain.dto.create.CreateOrderCommand;
import com.food.order.service.domain.dto.create.CreateOrderResponse;
import com.food.order.service.domain.dto.create.OrderAddressDto;
import com.food.order.service.domain.dto.create.OrderItemDto;
import com.food.order.service.domain.entity.Customer;
import com.food.order.service.domain.entity.Order;
import com.food.order.service.domain.entity.Product;
import com.food.order.service.domain.entity.Restaurant;
import com.food.order.service.domain.exception.OrderDomainException;
import com.food.order.service.domain.mapper.OrderDataMapper;
import com.food.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.order.service.domain.ports.output.repository.OrderRepository;
import com.food.order.service.domain.ports.output.repository.RestaurantRepository;
import com.food.order.service.domain.valueobject.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    // OrderApplicationService and OrderDataMapper will be created as a part
    // of the Application context as most of them are plain Java classes
    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    //OrderRepository, CustomerRepository and RestaurantRepository classes will be mocked from
    //the OrderTestConfiguration class as they are output ports and there implementation will
    //be in different layer and will involve external database calls
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;
    private final UUID CUSTOMER_ID = UUID.fromString("d325768f-0976-4156-8976-785496521");
    private final UUID RESTAURANT_ID = UUID.fromString("d325768f-0976-4785-8976-788546521");
    private final UUID ORDER_ID = UUID.fromString("d325768f-0976-8796-8976-785963521");
    private final UUID PRODUCT_ID = UUID.fromString("d325768f-0976-8756-8976-788456521");
    private final BigDecimal PRICE = new BigDecimal("200.00");

    @BeforeAll
    private void init() {

        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .orderAddress(OrderAddressDto.builder()
                        .street("street-1")
                        .city("Bangalore")
                        .postalCode("560043")
                        .build())
                .price(PRICE)
                .orderItemDtos(List.of(
                        OrderItemDto.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItemDto.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .orderAddress(OrderAddressDto.builder()
                        .street("street-1")
                        .city("Bangalore")
                        .postalCode("560043")
                        .build())
                .price(new BigDecimal("250.00"))
                .orderItemDtos(List.of(
                        OrderItemDto.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItemDto.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .orderAddress(OrderAddressDto.builder()
                        .street("street-1")
                        .city("Bangalore")
                        .postalCode("560043")
                        .build())
                .price(new BigDecimal("210.00"))
                .orderItemDtos(List.of(
                        OrderItemDto.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("60.00"))
                                .subTotal(new BigDecimal("60.00"))
                                .build(),
                        OrderItemDto.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(RESTAURANT_ID))
                .products(List.of(new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))))
                .active(true)
                .build();

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurantResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
    }

    @Test
    void testCreateOrder() {
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
        assertEquals("Order created successfully!", createOrderResponse.getMessage());
        assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    @Test
    void testCreateOrderWithWrongTotalPrice() {
        OrderDomainException exception = assertThrows(OrderDomainException.class, () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
        assertEquals("Total price 250.00 is not equal to items price 200.00", exception.getMessage());
    }

    @Test
    void testCreateOrderCommandWithWrongProductPrice() {
        OrderDomainException exception = assertThrows(OrderDomainException.class, () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
        assertEquals("Order item price 60.00 is not valid for product " + PRODUCT_ID, exception.getMessage());
    }

    @Test
    void testCreateOrderCommandWithPassiveRestaurant() {
        Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(RESTAURANT_ID))
                .products(List.of(new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))))
                .active(false)
                .build();

        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurantResponse));
        OrderDomainException exception = assertThrows(OrderDomainException.class, () -> orderApplicationService.createOrder(createOrderCommand));

        assertEquals("Restaurant with id " + RESTAURANT_ID + " is currently not active", exception.getMessage());
    }
}
