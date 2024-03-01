package sample.cafekiosk.spring.api.service.order;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

@RequiredArgsConstructor
@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final StockRepository stockRepository;

	@Transactional
	public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredDateTime) {
		List<String> productNumbers = request.getProductNumbers();
		List<Product> products = findProductsBy(productNumbers);
		deductStockQuantities(products);

		Order savedOrder = orderRepository.save(Order.create(products, registeredDateTime));
		return OrderResponse.fromEntity(savedOrder);
	}

	private void deductStockQuantities(List<Product> products) {
		// 재고 차감 체크가 필요한 상품들 filter
		List<String> stockProductNumbers = extractStockProductNumbers(products);

		// 재고 엔티티 조회
		Map<String, Stock> stocksByProductNumber = collectStocksBy(stockProductNumbers);

		// 상품별 Counting
		Map<String, Long> productStock = countingQuantityBy(stockProductNumbers);

		// 재고 차감 시도
		for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
			Stock stock = stocksByProductNumber.get(stockProductNumber);
			int quantity = productStock.get(stockProductNumber).intValue();

			if (stock.isQuantityLessThan(quantity)) {
				throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
			}

			stock.deductQuantity(quantity);
		}
	}

	private static Map<String, Long> countingQuantityBy(List<String> stockProductNumbers) {
		return stockProductNumbers.stream()
				.collect(Collectors.groupingBy(productNumber -> productNumber, Collectors.counting()));
	}

	private Map<String, Stock> collectStocksBy(List<String> stockProductNumbers) {
		List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
		return stocks.stream()
				.collect(Collectors.toMap(Stock::getProductNumber, stock -> stock));
	}

	private static List<String> extractStockProductNumbers(List<Product> products) {
		return products.stream()
				.filter(product -> ProductType.containsStockType(product.getType()))
				.map(Product::getProductNumber)
				.toList();
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
