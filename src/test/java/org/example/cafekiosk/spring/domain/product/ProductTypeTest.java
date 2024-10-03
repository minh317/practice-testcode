package org.example.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTypeTest {

    /**
     * 분기문과 반복문으로 인해 하나의 테스트 안에서 여러 주제가 들어있는 경우를 예시로 든 코드.
     * 이런 경우 여러 생각의 전환이 이루어지기 때문에, 맥락을 이해 하는데 있어 방해 요소가 될 수 있다.
     * 테스트 코드는 문서로서의 역할도 한다고 했었는데, 이런 코드들은 문서로의 역할을 제대로 수행하기엔 어렵다.
     */
    @DisplayName("상품 타입이 재고 관련 타입인지 체크한다.")
    @Test
    void containStockType() {
        // given
        ProductType[] productTypes = ProductType.values();

        for (ProductType productType : productTypes) {

            if (productType == ProductType.HANDMADE) {
                // when
                boolean result = ProductType.containsStockType(productType);

                // then
                assertThat(result).isFalse();
            }

            if (productType == ProductType.BAKERY || productType == ProductType.BOTTLE) {
                // when
                boolean result = ProductType.containsStockType(productType);

                // then
                assertThat(result).isTrue();
            }
        }
    }


    @DisplayName("상품의 타입이 재고가 차감 되는 타입인지 확인한다.")
    @Test
    void containsStockType1() {
        // given
        ProductType givenType = ProductType.HANDMADE;

        // when
        boolean result = ProductType.containsStockType(givenType);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("상품의 타입이 재고가 차감 되는 타입인지 확인한다.")
    @Test
    void containsStockType22() {
        // given
        ProductType givenType = ProductType.BAKERY;

        // when
        boolean result = ProductType.containsStockType(givenType);

        // then
        assertThat(result).isTrue();
    }

}