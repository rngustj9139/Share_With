package SWYG3.SubdivisionSubdivision.oauth2;

import SWYG3.SubdivisionSubdivision.oauth2.exception.OAuth2RegistrationException;
import SWYG3.SubdivisionSubdivision.oauth2.vo.Provider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
public class OAuth2Attributes {

    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String oauthId;
    private final String nickname;
    private final String email;
    private final String picture;
    private final Provider provider;

    @Builder // 빌더패턴사용(롬봄어노테이션) => https://peace-log.tistory.com/m/entry/Builder%ED%8C%A8%ED%84%B4-%EA%B7%B8%EB%A6%AC%EA%B3%A0-Builder-annotation%EB%8A%94-%EB%AD%98%EA%B9%8C%F0%9F%A7%90 참고
    public OAuth2Attributes(Map<String, Object> attributes, String nameAttributeKey, String oauthId, String nickname, String email, String picture, Provider provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.oauthId = oauthId;
        this.nickname = nickname;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
    }

    @SneakyThrows // 논란의 여지가 있는 롬봄의 어노테이션이라고 소개되고 있다. 메소드 선언부에 사용되는 throws 키워드 대신 사용하는 어노테이션으로 예외 클래스를 파라미터로 입력받는다.
    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) { // userNameAttributeName은 Primary key값을 의미한다. 특정 값 혹은 객체{ }를 의미한다 중복되지 않은 json데이터의 rootName을 알려준다 구글같은 경우는 sub, 네이버같은 경우는 id로 들어온다
        // 객체를 JSON 형태로 보게한다.
        log.info("userNameAttributeName = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userNameAttributeName));
        log.info("attributes = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(attributes));

        String registrationIdToLower = registrationId.toLowerCase();
        switch (registrationIdToLower) {
            case "google": return ofGoogle(userNameAttributeName, attributes);
            case "facebook": return ofFacebook(userNameAttributeName, attributes);
            default: throw new OAuth2RegistrationException("해당 소셜 로그인은 현재 지원하지 않습니다.");
        }
    }

    private static OAuth2Attributes ofFacebook(String userNameAttributeName,
                                               Map<String, Object> attributes) throws JsonProcessingException {
        return OAuth2Attributes.builder()
                .oauthId((String) attributes.get("id"))
                .nickname((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .provider(Provider.FACEBOOK)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .oauthId((String) attributes.get(userNameAttributeName))
                .nickname((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .provider(Provider.GOOGLE)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

}
