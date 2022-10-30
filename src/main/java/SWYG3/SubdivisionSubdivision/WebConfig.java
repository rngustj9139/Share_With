package SWYG3.SubdivisionSubdivision;

import SWYG3.SubdivisionSubdivision.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override // 인터셉터 등록
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/register/member/**", "/member/**", "/login/member/**", "/logout",
                        "/find/password", "/oauth2/authorization/google", "/oauth2/authorization/facebook",
                        "/css/**", "/js/**", "/assets/**", "/article/images/**", "/api/**", "/article/images/**");
    }

}
