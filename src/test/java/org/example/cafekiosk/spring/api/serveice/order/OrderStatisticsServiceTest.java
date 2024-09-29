package org.example.cafekiosk.spring.api.serveice.order;

import org.example.cafekiosk.spring.client.mail.MailSendClient;
import org.example.cafekiosk.spring.domain.history.mail.MailSendHistory;
import org.example.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;
import org.example.cafekiosk.spring.domain.order.Order;
import org.example.cafekiosk.spring.domain.order.OrderRepository;
import org.example.cafekiosk.spring.domain.order.OrderStatus;
import org.example.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import org.example.cafekiosk.spring.domain.product.Product;
import org.example.cafekiosk.spring.domain.product.ProductRepository;
import org.example.cafekiosk.spring.domain.product.ProductType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static org.example.cafekiosk.spring.domain.product.ProductType.HANDMADE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderStatisticsServiceTest {

    @Autowired
    OrderStatisticsService orderStatisticsService;

    @Autowired
    OrderRepository _orderRepository;

    @Autowired
    ProductRepository _productRepository;

    @Autowired
    OrderProductRepository _orderProductRepository;

    @Autowired
    MailSendHistoryRepository _mailSendHistoryRepository;

    @MockBean
    MailSendClient _mailSendClient;

    @AfterEach
    void tearDown() {
        _orderProductRepository.deleteAllInBatch();;
        _orderRepository.deleteAllInBatch();
        _productRepository.deleteAllInBatch();
        _mailSendHistoryRepository.deleteAllInBatch();
    }

    @DisplayName("결제완료 주문들을 조회하여 매출 통계 메일을 전송한다.")
    @Test
    void sendOrderStatisticsMail() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 9, 29, 0, 0);

        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 2000);
        Product product3 = createProduct(HANDMADE, "002", 3000);
        List<Product> products = List.of(product1, product2, product3);
        _productRepository.saveAll(products);

        Order order1 = createPaymenrCompleteOrder(products, LocalDateTime.of(2024, 9, 28, 23, 59, 59));
        Order order2 = createPaymenrCompleteOrder(products, now);
        Order order3 = createPaymenrCompleteOrder(products, LocalDateTime.of(2024, 9, 29, 23, 59, 59));
        Order order4 = createPaymenrCompleteOrder(products, LocalDateTime.of(2024, 9, 30, 0, 0));

        when(_mailSendClient.send(any(String.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(true);

        // when
        boolean result = orderStatisticsService.sendOrderStatisticsMail(LocalDate.of(2024, 9, 29), "minh317@naver.com");

        // then
        assertThat(result).isTrue();

        List<MailSendHistory> histories = _mailSendHistoryRepository.findAll();
        assertThat(histories).hasSize(1)
                .extracting("content")
                .containsExactly("총 매출 합계는 12,000원 입니다.") ;
    }

    private Order createPaymenrCompleteOrder(List<Product> products, LocalDateTime now) {
        Order order = Order.builder()
                .products(products)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .registeredDateTime(now)
                .build();
        return _orderRepository.save(order);
    }

    private Product createProduct(ProductType type, String productNumber, int price) {

        return Product.builder()
                .type(type)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .build();
    }
}