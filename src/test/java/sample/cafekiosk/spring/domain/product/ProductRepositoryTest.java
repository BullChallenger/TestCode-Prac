package sample.cafekiosk.spring.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.*;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
class ProductRepositoryTest {

	@Autowired
	private ProductRepository productRepository;

	@DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
	@Test
	void findAllBySellingStatusIn() {
		// given
		Product product01 = createProduct("001", HANDMADE, "아메리카노", 4000, SELLING);

		Product product03 = createProduct("002", HANDMADE, "카페라떼", 4500, HOLD);

		Product product02 = createProduct("003", HANDMADE, "팥빙수", 7000, STOP_SELLING);

		productRepository.saveAll(List.of(product01, product02, product03));

		// when
		List<Product> products = productRepository.findAllBySellingStatusIn(List.of(SELLING, HOLD));

		// then
		assertThat(products).hasSize(2)
			.extracting("productNumber", "name", "sellingStatus")
			.containsExactlyInAnyOrder(
				tuple("001", "아메리카노", SELLING),
				tuple("002", "카페라떼", HOLD)
			);
	}

	@DisplayName("원하는 물품번호를 가진 상품들을 조회한다.")
	@Test
	void findAllByProductNumbersIn() {
		// given
		Product product01 = createProduct("001", HANDMADE, "아메리카노", 4000, SELLING);

		Product product03 = createProduct("002", HANDMADE, "카페라떼", 4500, HOLD);

		Product product02 = createProduct("003", HANDMADE, "팥빙수", 7000, STOP_SELLING);

		productRepository.saveAll(List.of(product01, product02, product03));

		// when
		List<Product> products = productRepository.findAllByProductNumberIn(List.of("001", "002"));

		// then
		assertThat(products).hasSize(2)
			.extracting("productNumber", "name", "sellingStatus")
			.containsExactlyInAnyOrder(
				tuple("001", "아메리카노", SELLING),
				tuple("002", "카페라떼", HOLD)
			);
	}

	@DisplayName("가장 마지막에 생성된 상품 번호 조회.")
	@Test
	void findLatestProductNumber() {
		// given
		Product product01 = createProduct("001", HANDMADE, "아메리카노", 4000, SELLING);

		Product product02 = createProduct("002", HANDMADE, "카페라떼", 4500, HOLD);

		Product product03 = createProduct("003", HANDMADE, "팥빙수", 7000, STOP_SELLING);

		productRepository.saveAll(List.of(product01, product02, product03));

		// when
		String latestProductNumber = productRepository.findLatestProductNumber();

		// then
		assertThat(latestProductNumber).isEqualTo("003");
	}

	@DisplayName("상품이 없을 때, 가장 마지막에 생성된 상품 번호 조회.")
	@Test
	void findLatestProductNumber_With_EmptyProducts() {
		// given // when
		String latestProductNumber = productRepository.findLatestProductNumber();

		// then
		assertThat(latestProductNumber).isNull();
	}

	private Product createProduct(String productNumber, ProductType type, String productName, int price,
										 ProductSellingStatus sellingStatus) {
		return Product.builder()
				.productNumber(productNumber)
				.type(type)
				.sellingStatus(sellingStatus)
				.name(productName)
				.price(price)
				.build();
	}

}