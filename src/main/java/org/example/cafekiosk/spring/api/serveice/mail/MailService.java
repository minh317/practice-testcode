package org.example.cafekiosk.spring.api.serveice.mail;

import lombok.RequiredArgsConstructor;
import org.example.cafekiosk.spring.client.mail.MailSendClient;
import org.example.cafekiosk.spring.domain.history.mail.MailSendHistory;
import org.example.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailService {

    final MailSendClient _mailSendClient;
    final MailSendHistoryRepository _mailSendHistoryRepository;

    public boolean send(String from, String to, String subject, String content) {

        boolean result = _mailSendClient.send(from, to, subject, content);
        if (result) {
            _mailSendHistoryRepository.save(MailSendHistory.builder()
                    .from(from)
                    .to(to)
                    .subject(subject)
                    .content(content)
                    .build());

            _mailSendClient.a();
            _mailSendClient.b();
            _mailSendClient.c();

            return true;
        }

        return false;
    }
}
