package org.example.cafekiosk.spring.api.serveice.order;

import org.example.cafekiosk.spring.IntegrationTestSupport;
import org.example.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import org.example.cafekiosk.spring.api.serveice.order.response.OrderResponse;
import org.example.cafekiosk.spring.domain.order.OrderRepository;
import org.example.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import org.example.cafekiosk.spring.domain.product.Product;
import org.example.cafekiosk.spring.domain.product.ProductRepository;
import org.example.cafekiosk.spring.domain.product.ProductType;
import org.example.cafekiosk.spring.domain.stock.Stock;
import org.example.cafekiosk.spring.domain.stock.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.example.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static org.example.cafekiosk.spring.domain.product.ProductType.*;

class OrderServiceTest extends IntegrationTestSupport {

    @Autowired
    ProductRepository _productRepository;

    @Autowired
    OrderRepository _orderRepository;

    @Autowired
    OrderProductRepository _orderProductRepository;

    @Autowired
    StockRepository _stockRepository;

    @Autowired
    OrderService _orderService;

    @AfterEach
    void tearDown() {
        _orderProductRepository.deleteAllInBatch();
        _productRepository.deleteAllInBatch();
        _orderRepository.deleteAllInBatch();
        _stockRepository.deleteAllInBatch();
    }

    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        _productRepository.saveAll(List.of(product1, product2, product3));

        // 주문 생성을 하기 위해 요청 될 정보도 given 영역에 있는게 맞다.
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "002"))
                .build();

        // when
        OrderResponse response = _orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        // then
        assertThat(response.getId()).isNotNull();

        assertThat(response)
                .extracting("totalPrice", "registeredDateTime")
                .contains(4000, registeredDateTime);

        assertThat(response.getProducts()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactly(
                        tuple("001", 1000),
                        tuple("002", 3000)
                );
    }

    @DisplayName("재고와 관련된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrderWithStock() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct(BOTTLE, "001", 1000);
        Product product2 = createProduct(BAKERY, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        _productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        _stockRepository.saveAll(List.of(stock1, stock2));

        // 주문 생성을 하기 위해 요청 될 정보도 given 영역에 있는게 맞다.
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "001", "002", "003"))
                .build();

        // when
        OrderResponse response = _orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        // then
        assertThat(response.getId()).isNotNull();

        assertThat(response)
                .extracting("totalPrice", "registeredDateTime")
                .contains(10000, registeredDateTime);

        assertThat(response.getProducts()).hasSize(4)
                .extracting("productNumber", "price")
                .containsExactly(
                        tuple("001", 1000),
                        tuple("001", 1000),
                        tuple("002", 3000),
                        tuple("003", 5000)
                );

        List<Stock> stocks = _stockRepository.findAll();
        assertThat(stocks).hasSize(2)
                .extracting("productNumber", "quantity")
                .containsExactly(
                        tuple("001", 0),
                        tuple("002", 1)
                );
    }

    @DisplayName("재고가 부족한 상품으로 주문을 생성하려는 경우 예외가 발생한다.")
    @Test
    void createOrderWithNoStock() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct(BOTTLE, "001", 1000);
        Product product2 = createProduct(BAKERY, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        _productRepository.saveAll(List.of(product1, product2, product3));

        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        stock1.deductQuantity(1); // todo
        _stockRepository.saveAll(List.of(stock1, stock2));

        // 주문 생성을 하기 위해 요청 될 정보도 given 영역에 있는게 맞다.
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "001", "002", "003"))
                .build();

        // when & then
       assertThatThrownBy(() -> _orderService.createOrder(request.toServiceRequest(), registeredDateTime))
               .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족한 상품이 있습니다.");
    }

    @DisplayName("중복되는 상품번호 리스트로 주문을 생성할 수 있다.")
    @Test
    void createOrderWithDuplicateProductNumbers() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        _productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "001"))
                .build();

        // when
        OrderResponse response = _orderService.createOrder(request.toServiceRequest(), registeredDateTime);

        // then
        assertThat(response.getId()).isNotNull();

        assertThat(response)
                .extracting("totalPrice", "registeredDateTime")
                .contains(2000, registeredDateTime);

        assertThat(response.getProducts()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactly(
                        tuple("001", 1000),
                        tuple("001", 1000)
                );
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