package org.example.cafekiosk.spring.client.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailSendClient {
    public boolean send(String from, String to, String subject, String content) {
        // 메일 전송
        log.info("{} -> {} 전송, 제목: {}, 내용: {}", from, to, subject, content);
        throw new IllegalArgumentException("메일 전송");
    }
}
