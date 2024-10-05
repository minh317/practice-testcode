package org.example.cafekiosk.spring.domain.product;

import org.example.cafekiosk.spring.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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


//@ActiveProfiles("test")
//@DataJpaTest // @SpringBootTest 보다 가볍다. JPA와 관련된 설정들만 주입을 받아서 서버를 띄워준다.
//@SpringBootTest // 통합 테스트를 위해 제공하는 어노테이션
@Transactional
class ProductRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ProductRepository _productRepository;

    @Transactional
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

    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어온다.")
    @Test
    void findLatestProductNumber() {
        // given
        String targetProductNumber = "003";
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", HANDMADE, HOLD, "카페라떼", 4500);
        Product product3 = createProduct(targetProductNumber, HANDMADE, STOP_SELLING, "팥빙수", 5000);
        _productRepository.saveAll(List.of(product1, product2, product3));

        // when
        String latestProductNumber = _productRepository.findLatestProduct();

        // then
        assertThat(latestProductNumber).isEqualTo(targetProductNumber);

    }

    @DisplayName("가장 마지막으로 저장한 상품의 상품번호를 읽어올 때, 상품이 하나도 없는 경우에는 null을 반환한다.")
    @Test
    void findLatestProductNumberWhenProductIsEmpty() {
        // given

        // when
        String latestProductNumber = _productRepository.findLatestProduct();

        // then
        assertThat(latestProductNumber).isNull();

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