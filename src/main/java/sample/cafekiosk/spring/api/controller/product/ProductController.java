package sample.cafekiosk.spring.api.controller.product;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;

@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@RestController
public class ProductController {

	private final ProductService productService;

	@PostMapping("/new")
	public ProductResponse createProduct(@RequestBody ProductCreateRequest request) {
		return productService.createProduct(request);
	}

	@GetMapping("/selling")
	public List<ProductResponse> getSellingProducts() {
		return productService.getSellingProducts();
	}

}
