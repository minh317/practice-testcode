package org.example.cafekiosk.spring.domain.orderproduct;

import org.example.cafekiosk.spring.IntegrationTestSupport;
import org.example.cafekiosk.spring.domain.order.Order;
import org.example.cafekiosk.spring.domain.order.OrderRepository;
import org.example.cafekiosk.spring.domain.product.Product;
import org.example.cafekiosk.spring.domain.product.ProductRepository;
import org.example.cafekiosk.spring.domain.product.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.example.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static org.example.cafekiosk.spring.domain.product.ProductType.HANDMADE;

class OrderProductTest extends IntegrationTestSupport {

    @Autowired
    OrderRepository _orderRepository;

    @Autowired
    ProductRepository _productRepository;

    @DisplayName("주문 생성 시 주문 하려는 상품이 주문 상품 리스트로 잘 등록되었는지 검증한다.")
    @Test
    void orderProduct() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct(HANDMADE, "001", 7000);
        Product product2 = createProduct(HANDMADE, "002", 5000);
        _productRepository.saveAll(List.of(product1, product2));

        List<Product> products = _productRepository.findAllByProductNumberIn(
                List.of(product1.getProductNumber(), product2.getProductNumber()));

        // when
        Order order = Order.create(products, registeredDateTime);

        // then
        assertThat(order.getOrderProducts())
                .extracting("product.productNumber", "product.price")
                .containsExactly(
                        tuple("001", 7000),
                        tuple("002", 5000));
    }

    private Product createProduct(ProductType type, String productNumber, int price) {
        return Product.builder()
                .type(type)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(SELLING)
                .name("메뉴 이름" + productNumber)
                .build();
    }

}