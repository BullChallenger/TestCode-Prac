package sample.cafekiosk.spring.domain.order;

import static jakarta.persistence.CascadeType.*;
import static sample.cafekiosk.spring.domain.order.OrderStatus.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.domain.BaseEntity;
import sample.cafekiosk.spring.domain.orderproduct.OrderProduct;
import sample.cafekiosk.spring.domain.product.Product;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@Entity
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	private int totalPrice;

	private LocalDateTime registeredDateTime;

	@OneToMany(mappedBy = "order", cascade = ALL)
	private List<OrderProduct> orderProducts = new ArrayList<>();

	@Builder
	public Order(List<Product> products, OrderStatus orderStatus, LocalDateTime registeredDateTime) {
		this.orderStatus = orderStatus;
		this.totalPrice = calculateTotalPrice(products);
		this.registeredDateTime = registeredDateTime;
		this.orderProducts = mapToOrderProduct(products);
	}

	public static Order create(List<Product> products, LocalDateTime registeredDateTime) {
		return Order.builder()
				.orderStatus(INIT)
				.products(products)
				.registeredDateTime(registeredDateTime)
				.build();
	}

	private List<OrderProduct> mapToOrderProduct(List<Product> products) {
		return products.stream()
			.map(product -> new OrderProduct(this, product))
			.toList();
	}

	private static int calculateTotalPrice(List<Product> products) {
		return products.stream()
			.mapToInt(Product::getPrice)
			.sum();
	}

}
