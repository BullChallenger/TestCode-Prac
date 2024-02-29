package sample.cafekiosk.spring.domain.order;

import static org.assertj.core.api.Assertions.*;
import static sample.cafekiosk.spring.domain.order.OrderStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sample.cafekiosk.spring.domain.product.Product;

class OrderTest {

	@DisplayName("주문 생성 시 상품 리스트에서 주문의 총 금액을 계산한다.")
	@Test
	void calculate_totalPrice_Test() {
		// given
		List<Product> products = List.of(
			createProduct("001", 1000),
			createProduct("002", 3000)
		);

		// when
		LocalDateTime currentDateTime = LocalDateTime.of(2024, 2, 28, 17, 20);
		Order order = Order.create(products, currentDateTime);

		// then
		assertThat(order.getTotalPrice()).isEqualTo(4000);
	}

	@DisplayName("주문 생성 시 주문 상태는 INIT 이다.")
	@Test
	void init_Test() {
		// given
		List<Product> products = List.of(
			createProduct("001", 1000),
			createProduct("002", 3000)
		);

		// when
		LocalDateTime currentDateTime = LocalDateTime.of(2024, 2, 28, 17, 20);
		Order order = Order.create(products, currentDateTime);

		// then
		assertThat(order.getOrderStatus()).isEqualByComparingTo(INIT);
	}

	@DisplayName("주문 생성 시 주문 등록 시간을 기록한다.")
	@Test
	void registeredDateTime_Test() {
		// given
		List<Product> products = List.of(
			createProduct("001", 1000),
			createProduct("002", 3000)
		);

		// when
		LocalDateTime currentDateTime = LocalDateTime.of(2024, 2, 28, 17, 20);
		Order order = Order.create(products, currentDateTime);

		// then
		assertThat(order.getRegisteredDateTime()).isEqualTo(currentDateTime);
	}

	private Product createProduct(String productNumber, int price) {
		return Product.builder()
			.productNumber(productNumber)
			.type(HANDMADE)
			.sellingStatus(SELLING)
			.name("아메리카노")
			.price(price)
			.build();
	}

}