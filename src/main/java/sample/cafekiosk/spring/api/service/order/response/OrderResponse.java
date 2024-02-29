package sample.cafekiosk.spring.api.service.order.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.orderproduct.OrderProduct;

@Getter
public class OrderResponse {

	private Long id;
	private int totalPrice;
	private LocalDateTime registeredDateTime;
	private List<ProductResponse> products;

	@Builder
	public OrderResponse(Long id, int totalPrice, LocalDateTime registeredDateTime, List<ProductResponse> products) {
		this.id = id;
		this.totalPrice = totalPrice;
		this.registeredDateTime = registeredDateTime;
		this.products = products;
	}

	public static OrderResponse fromEntity(Order entity) {
		return OrderResponse.builder()
			.id(entity.getId())
			.totalPrice(entity.getTotalPrice())
			.registeredDateTime(entity.getRegisteredDateTime())
			.products(entity.getOrderProducts().stream().map(OrderProduct::getProduct).map(ProductResponse::fromEntity).toList())
			.build();
	}

}
