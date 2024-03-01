package sample.cafekiosk.spring.api.service.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.BAKERY;
import static sample.cafekiosk.spring.domain.product.ProductType.BOTTLE;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.MailSendHistory;
import sample.cafekiosk.spring.domain.history.MailSendHistoryRepository;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.order.OrderStatus;
import sample.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;

@SpringBootTest
class OrderStatisticServiceTest {

    @Autowired
    private OrderStatisticService orderStatisticService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MailSendHistoryRepository mailSendHistoryRepository;

    @MockBean
    private MailSendClient mailSendClient;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        mailSendHistoryRepository.deleteAllInBatch();
    }
    
    @DisplayName("결제완료 주문들을 조회하여 매출 통계 메일을 전송한다.")
    @Test
    void sendOrderStatisticMail() {
        // given
        LocalDateTime currentDatetime = LocalDateTime.of(2024, 3, 5, 18, 0);

        Product product01 = createProduct(BOTTLE, "001", 1000);
        Product product02 = createProduct(BAKERY, "002", 3000);
        Product product03 = createProduct(HANDMADE, "003", 5000);
        List<Product> products = List.of(product01, product02, product03);
        productRepository.saveAll(products);

        Order order01 = createPaymentCompletedOrder(currentDatetime, products);
        Order order02 = createPaymentCompletedOrder(currentDatetime, products);
        Order order03 = createPaymentCompletedOrder(currentDatetime, products);

        when(mailSendClient.sendEmail(any(String.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(true);

        // when
        boolean result = orderStatisticService.sendOrderStatisticMail(LocalDate.of(2024, 3, 5), "test@test.com");

        //then
        assertThat(result).isTrue();

        List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
        assertThat(histories).hasSize(1)
                .extracting("content")
                .contains("총 매출 합계는 27000원 입니다.");
    }

    private Order createPaymentCompletedOrder(LocalDateTime currentDatetime, List<Product> products) {
        Order order = Order.builder()
                .products(products)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .registeredDateTime(currentDatetime)
                .build();
        return orderRepository.save(order);
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