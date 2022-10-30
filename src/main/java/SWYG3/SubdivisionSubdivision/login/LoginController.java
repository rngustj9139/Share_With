package SWYG3.SubdivisionSubdivision.login;

import SWYG3.SubdivisionSubdivision.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login/member")
    public String loginForm(Model model, @RequestParam(value = "redirectURL", required = false) String redirectURL, @RequestParam(value= "afterSignUpStatus", required = false) String afterSignUpStatus) {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        model.addAttribute("loginRequestDto", loginRequestDto);

        if (StringUtils.hasText(afterSignUpStatus)) { // 회원가입 후 로그인창으로 리다이렉트 될때 alert창이 뜨게함
            model.addAttribute("email", afterSignUpStatus);
        }

        return "login";
    }

    @PostMapping("/login/member")
    public String login(@Validated @ModelAttribute("loginRequestDto") LoginRequestDto loginRequestDto, BindingResult bindingResult, Model model,
                        HttpServletRequest request,
                        RedirectAttributes redirectAttributes,
                        @RequestParam(value = "afterSignUpStatus", required = false) String afterSignUpStatus,
                        @RequestParam(value = "redirectURL", defaultValue = "/") String redirectURL) {
        Member loginMember = loginService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        if (loginMember != null) { // 회원가입을 했으나 이메일 인증을 받지 않은 경우
            if (loginMember.getAuthStatus() == 0) {
                bindingResult.reject("loginFailByEmailVerification", "먼저 이메일 인증을 해주세요!");
            }
            if (loginMember.getAuthStatus() == 1) { // 로그인 성공 처리
                HttpSession session = request.getSession(true); // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
                session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember); // 세션에 로그인 회원 정보 보관, 이후 브라우저의 쿠키 저장소에 응답보냄
            }
        }

        if (loginMember == null) { // 아이디가 존재하지 않거나 아이디 또는 비밀번호가 일치하지 않는경우
//          bindingResult.reject("loginFail", "아이디 또는 비밀번호가 일치하지 않습니다.");
            model.addAttribute("loginFail", true);
            return "login";
        }

        if(bindingResult.hasErrors()) {
            return "login";
        }

        return "redirect:" + redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션이 존재하면 그 세션을 가져오고 없다면 세션을 새로 만들지 않는다.(null 이 반환됨)

        if(session != null) {
            session.invalidate(); // 세션 정보를 삭제
        }

        return "redirect:/";
    }

}
