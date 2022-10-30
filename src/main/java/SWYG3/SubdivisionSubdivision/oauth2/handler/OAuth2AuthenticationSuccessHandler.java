package SWYG3.SubdivisionSubdivision.oauth2.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler { // OAuth2 인증 성공 핸들러

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        log.info("성공!");

        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/");  // OAuth2 인증 성공한다면 메인 페이지로 리다이렉트

//        String[] path = httpServletRequest.getRequestURI().split("/");
//        Provider provider = Provider.valueOf(path[path.length - 1].toUpperCase());
//        String oauthId = authentication.getName();
//
//        String uri = UriComponentsBuilder.fromUriString( "http://localhost:8080/social")
//                .queryParam("provider", provider)
//                .queryParam("oauthId", oauthId)
//                .build().toUriString();
//        httpServletResponse.sendRedirect(uri);
    }

}
