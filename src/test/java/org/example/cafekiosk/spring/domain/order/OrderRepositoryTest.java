package org.example.cafekiosk.spring.domain.order;

import org.assertj.core.api.Assertions;
import org.example.cafekiosk.spring.IntegrationTestSupport;
import org.example.cafekiosk.spring.domain.product.Product;
import org.example.cafekiosk.spring.domain.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.tuple;
import static org.example.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static org.example.cafekiosk.spring.domain.product.ProductType.HANDMADE;

class OrderRepositoryTest extends IntegrationTestSupport {

    @Autowired
    ProductRepository _productRepository;

    @Autowired
    OrderRepository _orderRepository;

    @Transactional
    @DisplayName("특정 기간 내에 결제 완료 된 주문 목록을 조회한다.")
    @Test
    void test() {
        // given
        LocalDateTime registeredDateTime =
                LocalDateTime.of(2024, 9, 29, 18, 0, 0);

        LocalDateTime startDateTime =
                LocalDateTime.of(2024, 9, 29, 17, 0, 0);

        LocalDateTime endDateTime =
                LocalDateTime.of(2024, 9, 29, 19, 0, 0);

        List<Product> products = List.of(
                createProduct("001", 1000),
                createProduct("002", 2000)
        );
        _productRepository.saveAll(products);

        Order order = Order.create(products, registeredDateTime);
        order.paymentComplete();
        _orderRepository.save(order);

        // when
        List<Order> orders =
                _orderRepository.findOrdersBy(startDateTime, endDateTime, OrderStatus.PAYMENT_COMPLETED);

        // then
        Assertions.assertThat(orders).hasSize(1)
                .extracting("registeredDateTime", "orderStatus")
                .containsExactly(
                        tuple(registeredDateTime, OrderStatus.PAYMENT_COMPLETED)
                );
    }

    private Product createProduct(String productNumber, int price) {
        return Product.builder()
                .type(HANDMADE)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(SELLING)
                .name("메뉴 이름" + productNumber)
                .build();
    }

}