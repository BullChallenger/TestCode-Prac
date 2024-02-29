package sample.cafekiosk.spring.domain.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	/**
	 * @query select * from product where selling_type in :@param
	 * @param sellingStatus
	 * @return sellingTypes 를 포함하는 Product 목록 반환
	 */
	List<Product> findAllBySellingStatusIn(List<ProductSellingStatus> sellingStatus);

	/**
	 * @query select * from product where product_number in :@param
	 * @param productNumbers
	 * @return productNumbers 를 포함하는 Product 목록 반환
	 */
	List<Product> findAllByProductNumberIn(List<String> productNumbers);

}
