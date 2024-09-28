package org.example.cafekiosk.spring.api.serveice.product;

import org.example.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import org.example.cafekiosk.spring.api.serveice.product.response.ProductResponse;
import org.example.cafekiosk.spring.domain.product.Product;
import org.example.cafekiosk.spring.domain.product.ProductRepository;
import org.example.cafekiosk.spring.domain.product.ProductSellingStatus;
import org.example.cafekiosk.spring.domain.product.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.example.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static org.example.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@ActiveProfiles("test")
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService _productService;

    @Autowired
    private ProductRepository _productRepository;

    @Transactional
    @Test
    @DisplayName("신규 상품을 등록한다. 상품 번호는 가장 최근 상품 번호에서 1 증가한 값이다.")
    void createProduct() {
       // given
        Product product = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        _productRepository.save(product);

        // 요청 정보도 given이 맞다
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build();

        // when
        ProductResponse productResponse = _productService.createProduct(request);

        // then
        assertThat(productResponse)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .containsExactly("002", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = _productRepository.findAll();
        assertThat(products).hasSize(2)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", HANDMADE, SELLING, "아메리카노", 4000),
                        tuple("002", HANDMADE, SELLING, "카푸치노", 5000)
                );
    }

    @Transactional
    @Test
    @DisplayName("상품이 하나도 없는 경우 신규 상품을 등록하면 상품 번호는 001이다.")
    void createProductWhenProductIsEmpty() {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("카푸치노")
                .price(5000)
                .build();

        // when
        ProductResponse productResponse = _productService.createProduct(request);

        // then
        assertThat(productResponse)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .containsExactly("001", HANDMADE, SELLING, "카푸치노", 5000);

        List<Product> products = _productRepository.findAll();
        assertThat(products).hasSize(1)
                .extracting("productNumber", "type", "sellingStatus", "name", "price")
                .containsExactly(tuple("001", HANDMADE, SELLING, "카푸치노", 5000));

    }

    private Product createProduct(
            String productNumber, ProductType type, ProductSellingStatus sellingStatus, String name, int price) {

        return Product.builder()
                .productNumber(productNumber)
                .type(type)
                .sellingStatus(sellingStatus)
                .name(name)
                .price(price)
                .build();
    }

    private Product createAmericano() {
        return createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
    }

    private Product createLatte() {
        return createProduct("002", HANDMADE, HOLD, "카페라떼", 4500);
    }

    private Product createBingsoo() {
        return createProduct("003", HANDMADE, STOP_SELLING, "팥빙수", 7000);
    }

}