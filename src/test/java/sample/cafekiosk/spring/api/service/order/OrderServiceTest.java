package sample.cafekiosk.spring.api.service.order;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.*;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.orderproduct.OrderProduct;
import sample.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
class OrderServiceTest {

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderProductRepository orderProductRepository;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private OrderRepository orderRepository;

	@AfterEach
	void tearDown() {
		orderProductRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		orderRepository.deleteAllInBatch();
		stockRepository.deleteAllInBatch();
	}

	@DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
	@Test
	void createOrder_Test() {
		// given
		Product product01 = createProduct(HANDMADE, "001", 1000);
		Product product02 = createProduct(HANDMADE, "002", 3000);
		Product product03 = createProduct(HANDMADE, "003", 5000);

		productRepository.saveAll(List.of(product01, product02, product03));

		OrderCreateRequest request = OrderCreateRequest.builder()
			.productNumbers(List.of("001", "002"))
			.build();

		// when
		LocalDateTime currentDateTime = LocalDateTime.of(2024, 2, 28, 17, 20);
		OrderResponse orderResponse = orderService.createOrder(request, currentDateTime);

		// then
		assertThat(orderResponse.getId()).isNotNull();
		assertThat(orderResponse)
			.extracting("registeredDateTime", "totalPrice")
			.contains(currentDateTime, 4000);
		assertThat(orderResponse.getProducts()).hasSize(2)
			.extracting("productNumber", "price")
			.containsExactlyInAnyOrder(
				tuple("001", 1000),
				tuple("002", 3000)
			);
	}

	@DisplayName("중복되는 상품번호 리스트로 주문을 생성할 수 있다.")
	@Test
	void createOrder_With_Duplicated_ProductNumbers() {
		// given
		Product product01 = createProduct(HANDMADE, "001", 1000);
		Product product02 = createProduct(HANDMADE, "002", 3000);
		Product product03 = createProduct(HANDMADE, "003", 5000);

		productRepository.saveAll(List.of(product01, product02, product03));

		OrderCreateRequest request = OrderCreateRequest.builder()
			.productNumbers(List.of("001", "001"))
			.build();

		// when
		LocalDateTime currentDateTime = LocalDateTime.of(2024, 2, 28, 17, 20);
		OrderResponse orderResponse = orderService.createOrder(request, currentDateTime);

		// then
		assertThat(orderResponse.getId()).isNotNull();
		assertThat(orderResponse)
			.extracting("registeredDateTime", "totalPrice")
			.contains(currentDateTime, 2000);
		assertThat(orderResponse.getProducts()).hasSize(2)
			.extracting("productNumber", "price")
			.containsExactlyInAnyOrder(
				tuple("001", 1000),
				tuple("001", 1000)
			);
	}

	@DisplayName("재고와 관련된 상품이 포함되어 있는 주문번호 리스트를 받아 주문을 생성한다.")
	@Test
	void createOrder_With_Stock_Test() {
		// given
		Product product01 = createProduct(BOTTLE, "001", 1000);
		Product product02 = createProduct(BAKERY, "002", 3000);
		Product product03 = createProduct(HANDMADE, "003", 5000);

		productRepository.saveAll(List.of(product01, product02, product03));

		Stock stock01 = Stock.create("001", 2);
		Stock stock02 = Stock.create("002", 2);
		stockRepository.saveAll(List.of(stock01, stock02));

		OrderCreateRequest request = OrderCreateRequest.builder()
				.productNumbers(List.of("001", "001", "002", "003"))
				.build();

		// when
		LocalDateTime currentDateTime = LocalDateTime.of(2024, 2, 28, 17, 20);
		OrderResponse orderResponse = orderService.createOrder(request, currentDateTime);

		// then
		assertThat(orderResponse.getId()).isNotNull();
		assertThat(orderResponse)
				.extracting("registeredDateTime", "totalPrice")
				.contains(currentDateTime, 10000);
		assertThat(orderResponse.getProducts()).hasSize(4)
				.extracting("productNumber", "price")
				.containsExactlyInAnyOrder(
						tuple("001", 1000),
						tuple("001", 1000),
						tuple("002", 3000),
						tuple("003", 5000)
				);

		List<Stock> stocksInOrder = stockRepository.findAll();
		assertThat(stocksInOrder).hasSize(2)
				.extracting("productNumber", "quantity")
				.containsExactlyInAnyOrder(
						tuple("001", 0),
						tuple("002", 1)
				);
	}

	@DisplayName("재고가 없는 상품으로 주문을 생성하려는 경우 예외가 발생한다.")
	@Test
	void createOrder_With_Empty_Stock_Test() {
		// given
		Product product01 = createProduct(BOTTLE, "001", 1000);
		Product product02 = createProduct(BAKERY, "002", 3000);
		Product product03 = createProduct(HANDMADE, "003", 5000);

		productRepository.saveAll(List.of(product01, product02, product03));

		Stock stock01 = Stock.create("001", 1);
		Stock stock02 = Stock.create("002", 1);
		stockRepository.saveAll(List.of(stock01, stock02));

		OrderCreateRequest request = OrderCreateRequest.builder()
				.productNumbers(List.of("001", "001", "002", "003"))
				.build();

		// when
		LocalDateTime currentDateTime = LocalDateTime.of(2024, 2, 28, 17, 20);

		// then
		assertThatThrownBy(() -> orderService.createOrder(request, currentDateTime))
				.isInstanceOf(IllegalArgumentException.class)
						.hasMessage("재고가 부족한 상품이 있습니다.");
	}

	private Product createProduct(ProductType type, String productNumber, int price) {
		return Product.builder()
			.productNumber(productNumber)
			.type(type)
			.sellingStatus(SELLING)
			.name("아메리카노")
			.price(price)
			.build();
	}

}