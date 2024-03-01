package sample.cafekiosk.spring.api.service.order;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.service.mail.MailService;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.order.OrderStatus;

@RequiredArgsConstructor
@Service
public class OrderStatisticService {

    private final OrderRepository orderRepository;
    private final MailService mailService;

    public boolean sendOrderStatisticMail(LocalDate orderDate, String email) {
        // 해당 일자에 결제완료된 주문들을 가져와서
        List<Order> orders = orderRepository.findOrdersBy(
                orderDate.atStartOfDay(),
                orderDate.plusDays(1).atStartOfDay(),
                OrderStatus.PAYMENT_COMPLETED
        );

        // 총 매출 합계를 계산하고
        int totalAmount = orders.stream().mapToInt(Order::getTotalPrice).sum();

        // 메일 전송
        boolean isSendingMailSuccess = mailService.sendMail("no-reply@cafekiosk.com", email,
        "[매출통계]",
                String.format("총 매출 합계는 %s원 입니다.", totalAmount));

        if (!isSendingMailSuccess) {
            throw new IllegalArgumentException("매출 통계 메일 전송에 실패했습니다.");
        }

        return true;
    }

}
