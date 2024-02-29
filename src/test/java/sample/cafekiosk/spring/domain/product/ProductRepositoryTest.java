package sample.cafekiosk.spring.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.*;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;

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
		Product product01 = Product.builder()
			.productNumber("001")
			.type(ProductType.HANDMADE)
			.sellingStatus(SELLING)
			.name("아메리카노")
			.price(4000)
			.build();

		Product product03 = Product.builder()
			.productNumber("002")
			.type(ProductType.HANDMADE)
			.sellingStatus(HOLD)
			.name("카페라떼")
			.price(4500)
			.build();

		Product product02 = Product.builder()
			.productNumber("003")
			.type(ProductType.HANDMADE)
			.sellingStatus(STOP_SELLING)
			.name("팥빙수")
			.price(7000)
			.build();

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
		Product product01 = Product.builder()
			.productNumber("001")
			.type(ProductType.HANDMADE)
			.sellingStatus(SELLING)
			.name("아메리카노")
			.price(4000)
			.build();

		Product product03 = Product.builder()
			.productNumber("002")
			.type(ProductType.HANDMADE)
			.sellingStatus(HOLD)
			.name("카페라떼")
			.price(4500)
			.build();

		Product product02 = Product.builder()
			.productNumber("003")
			.type(ProductType.HANDMADE)
			.sellingStatus(STOP_SELLING)
			.name("팥빙수")
			.price(7000)
			.build();

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

}