package sample.cafekiosk.spring.api.service.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;

@RequiredArgsConstructor
@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;

	public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredDateTime) {
		List<String> productNumbers = request.getProductNumbers();
		// Product
		List<Product> products = findProductsBy(productNumbers);

		// Order
		Order savedOrder = orderRepository.save(Order.create(products, registeredDateTime));

		return OrderResponse.fromEntity(savedOrder);
	}

	private List<Product> findProductsBy(List<String> productNumbers) {
		List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);

		// Duplicated Product Cause Same Product Number
		Map<String, Product> productCheck = products.stream()
			.collect(Collectors.toMap(Product::getProductNumber, product -> product));

		return productNumbers.stream()
				.map(productCheck::get)
				.toList();
	}

}
