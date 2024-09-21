package org.example.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.example.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static org.example.cafekiosk.spring.domain.product.ProductType.HANDMADE;

/**
 * Repository의 경우 Database에 Access하는 역할만 가지고 있기 때문에 단위 테스트의 성격을 가지고 있다.
 */

/**
 * @DataJpaTest보다 @SpringBootTest가 좀 더 선호되는 어노테이션이다.
 */


@ActiveProfiles("test")
@DataJpaTest // @SpringBootTest 보다 가볍다. JPA와 관련된 설정들만 주입을 받아서 서버를 띄워준다.
//@SpringBootTest // 통합 테스트를 위해 제공하는 어노테이션
class ProductRepositoryTest {

    @Autowired
    private ProductRepository _productRepository;

    @Test
    @DisplayName("원하는 판매 상태를 가진 상품들을 조회한다.")
    void test() {

        // given
        Product product1 = createAmericano();
        Product product2 = createLatte();
        Product product3 = createBingsoo();

        _productRepository.saveAll(List.of(product1, product2, product3));

        // when
        List<Product> products = _productRepository.findAllBySellingStatusIn(List.of(SELLING, HOLD));

        // then
        assertThat(products).hasSize(2)
                .extracting("productNumber", "name", "sellingStatus")
                .containsExactly(
                        tuple("001", "아메리카노", SELLING),
                        tuple("002", "카페라떼", HOLD)
                );
    }

    @DisplayName("주문 하려는 상품번호 리스트로 상품들을 조회한다.")
    @Test
    void findAllProductNumberIn() {

        // given
        Product product1 = createAmericano();
        Product product2 = createLatte();
        Product product3 = createBingsoo();

        _productRepository.saveAll(List.of(product1, product2, product3));

        // when
        List<Product> products = _productRepository.findAllByProductNumberIn(List.of("001", "002"));

        // then
        assertThat(products).hasSize(2)
                .extracting("productNumber", "name", "sellingStatus")
                .containsExactly(
                        tuple("001", "아메리카노", SELLING),
                        tuple("002", "카페라떼", HOLD)
                );
    }


    private Product createBingsoo() {
        return Product.builder()
                .productNumber("003")
                .type(HANDMADE)
                .sellingStatus(STOP_SELLING)
                .name("팥빙수")
                .price(7000)
                .build();
    }

    private Product createLatte() {
        return Product.builder()
                .productNumber("002")
                .type(HANDMADE)
                .sellingStatus(HOLD)
                .name("카페라떼")
                .price(4500)
                .build();
    }

    private Product createAmericano() {
        return Product.builder()
                .productNumber("001")
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
    }
}