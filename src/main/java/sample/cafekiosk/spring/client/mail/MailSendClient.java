package sample.cafekiosk.spring.client.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailSendClient {

    public boolean sendEmail(String from, String to, String title, String content) {
        log.info("메일 전송");
        return true;
    }

}
