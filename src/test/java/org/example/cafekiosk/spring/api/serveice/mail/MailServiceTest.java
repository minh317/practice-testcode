package org.example.cafekiosk.spring.api.serveice.mail;

import org.example.cafekiosk.spring.client.mail.MailSendClient;
import org.example.cafekiosk.spring.domain.history.mail.MailSendHistory;
import org.example.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
//    @Spy
    private MailSendClient _mailSendClient;

    @Mock
    private MailSendHistoryRepository _mailSendHistoryRepository;

    /**
     * MailService 객체의 생성자를 보고 위 @Mock으로 생성 된 Mock 객체를 주입한다. (DI)
     */
    @InjectMocks
    private MailService mailService;

    // 순수 Mockito에 대한 테스트
    @DisplayName("메일 전송 테스트")
    @Test
    void test() {
        // given

        /** @MockBean을 통해 Mock 객체를 주입받지 않고 순수한 Mockito로만 테스트 **/
        /** 아래 방식으로 해도 되고, 아니면 위에서 @Mock 어노테이션을 작성하면 Mock 객체를 만들어 준다. 단, @ExtendsWith(MockitoException.class) 필요 **/
        // Mockito.mock() 내부 로직을 파고 들어가보면, withSettings() 메서드에 RETURN_DEFAULTS를 defaultAnswer() 하도록 되어 있다.
        // 이는 int나 long 같은 것들은 primitive type을, 오브젝트는 Null을, Collection은 Empty를 리턴하도록 하는 정책이다.
//        MailSendClient mailSendClient = mock(MailSendClient.class); // @Mock
//        MailSendHistoryRepository mailSendHistoryRepository = mock(MailSendHistoryRepository.class); // @Mock

//        MailService mailService = new MailService(mailSendClient, mailSendHistoryRepository); // @InjectMocks
        // mailSendClient Mock 객체에 대한 Stubbing을 한다.
        // any(String.class) -> anyString()도 있음

        /** @Spy 어노테이션을 붙이면 when 절을 사용하면 안된다. Mockito의 spy는 실제 객체를 기반으로 만들어진다.
         * 즉, mailSendClient 실제 객체의 send() 메서드를 Mocking 하려고 함. 즉, Stubbing이 되지 않음. 대신 다른 걸 사용해야 함 **/
//        when(_mailSendClient.send(anyString(), any(String.class), any(String.class), any(String.class)))
//                .thenReturn(true);

        BDDMockito.given(_mailSendClient.send(anyString(), anyString(), anyString(), anyString()))
                .willReturn(true);

//        doReturn(true)
//                .when(_mailSendClient)
//                .send(any(String.class), any(String.class), any(String.class), any(String.class));

        // 위 RETURN_DEFAULTS에 의해 null을 리턴하므로, 아래 코드는 필요 없다.
//        when(mailSendHistoryRepository.save(any(MailSendHistory.class)))
//                .thenReturn();

        // when
        boolean result = mailService.send("", "", "", "");

        // then
        assertThat(result).isTrue();

        // mailSendHistoryRepository가 몇 번 호출 됐는지에 횟수에 대한 검증을 할 수 있다.
        // verify() 메서드는 인자 값을 times 만큼 호출할 것이고, 인자 값의 메서드(save())를 호출한다. (메서드는 MailSendHistory 객체를 인자로 받는다.)
        verify(_mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));
    }

}