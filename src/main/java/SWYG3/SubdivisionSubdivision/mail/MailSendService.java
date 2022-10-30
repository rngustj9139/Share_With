package SWYG3.SubdivisionSubdivision.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service("mss")
@Transactional
@RequiredArgsConstructor
public class MailSendService {

    private final JavaMailSender javaMailSender;
    private int size;

    //인증키 생성
    private String getKey(int size) {
        this.size = size;
        return getAuthCode();
    }

    //인증코드 난수 발생
    private String getAuthCode() {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer(); // StringBuffer는 문자열을 추가하거나 변경할 때 사용합니다. ex) sb.append("Hello"); sb.toString();
        int num = 0;

        while(buffer.length() < size) {
            num = random.nextInt(10); // Random 클래스의 nextInt() 메소드에 파라미터를 입력하지 않으면 int형 표현 범위(-2147483648 ~ 2147483647)의 모든 영역에서 랜덤한 숫자 1개가 나옵니다. 75를 입력하면 0~74사이의 정수 1개가 나옵니다.
            buffer.append(num);
        }

        return buffer.toString();
    }

    // 회원가입시 이메일 인증 메일 보내기
    public String sendAuthMail(String email) {
        //6자리 난수 인증번호 생성
        String authKey = getKey(6);

        //인증메일 보내기
        try {
            MailUtils sendMail = new MailUtils(javaMailSender);
            sendMail.setSubject("회원가입 이메일 인증"); // 메일 제목
            sendMail.setText(new StringBuffer().append("<h1>[이메일 인증]</h1>") // 메일 내용
                    .append("<p>아래 링크를 클릭하시면 이메일 인증이 완료됩니다.</p>")
                    .append("<a href='http://18.181.246.79/member/signUpConfirm?email=")
                    .append(email)
                    .append("&authKey=")
                    .append(authKey)
                    .append("' target='_blenk'>이메일 인증 확인</a>")
                    .toString());
            sendMail.setFrom("ninexxxwest@gmail.com", "소분소분 관리자"); // 메일 송신자
            sendMail.setTo(email); // 메일 수신자
            sendMail.send();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return authKey;
    }

    // 이메일 기반 임시 비밀번호 찾기
    public String sendTemporaryPasswordMail(String email) {
        //6자리 난수 패스워드 생성
        String password = getKey(6);

        //인증메일 보내기
        try {
            MailUtils sendMail = new MailUtils(javaMailSender);
            sendMail.setSubject("임시 비밀번호 이메일 송신"); // 메일 제목
            sendMail.setText(new StringBuffer().append("<h1>[임시 비밀번호 이메일]</h1>") // 메일 내용
                    .append("<p>회원님의 임시비밀번호는 다음과 같습니다.</p>")
                    .append(password)
                    .toString());
            sendMail.setFrom("ninexxxwest@gmail.com", "소분소분 관리자"); // 메일 송신자
            sendMail.setTo(email); // 메일 수신자
            sendMail.send();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return password;
    }

}
