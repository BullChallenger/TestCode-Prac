package sample.cafekiosk.spring.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static sample.cafekiosk.spring.domain.product.ProductType.BOTTLE;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTypeTest {

    @DisplayName("상품 타입이 재고 관련 타입인지 체크한다.")
    @Test
    void containsStockType() {
        // given
        // when
        boolean result = ProductType.containsStockType(HANDMADE);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("상품 타입이 재고 관련 타입인지 체크한다.")
    @Test
    void containsStockType_isTrue() {
        // given
        // when
        boolean result = ProductType.containsStockType(BOTTLE);

        //then
        assertThat(result).isTrue();
    }

}