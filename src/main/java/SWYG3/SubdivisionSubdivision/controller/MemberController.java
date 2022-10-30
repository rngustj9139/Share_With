package SWYG3.SubdivisionSubdivision.controller;

import SWYG3.SubdivisionSubdivision.dto.MemberRegisterRequestDto;
import SWYG3.SubdivisionSubdivision.dto.PasswordFindRequestDto;
import SWYG3.SubdivisionSubdivision.entity.Member;
import SWYG3.SubdivisionSubdivision.mail.MailSendService;
import SWYG3.SubdivisionSubdivision.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final MailSendService mss;

    @GetMapping("/register/member") // 회원가입 GET
    public String registerMemberForm(Model model) {
        MemberRegisterRequestDto memberRegisterRequestDto = new MemberRegisterRequestDto();
        model.addAttribute("memberRegisterRequestDto", memberRegisterRequestDto);

        return "registerMember";
    }

    @PostMapping("/register/member") // 회원가입 POST
    public String registerMember(@Validated @ModelAttribute MemberRegisterRequestDto memberRegisterRequestDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        if (!memberRegisterRequestDto.getFirstPassword().equals(memberRegisterRequestDto.getSecondPassword()) && StringUtils.hasText(memberRegisterRequestDto.getSecondPassword())) {
            bindingResult.rejectValue("secondPassword", "twoPasswordNotEqual");
        }

        if (memberRegisterRequestDto.getNickName().getBytes("EUC-KR").length > 12) { // 한글은 2바이트, 영어는 1바이트
            bindingResult.rejectValue("nickName", "nickNameByteLengthError");
        }

        if (memberService.findOneByNickName(memberRegisterRequestDto.getNickName()).isPresent()) {
            bindingResult.rejectValue("nickName", "nickNameDuplicateError");
        }

        if (bindingResult.hasErrors()) {
            return "registerMember";
        }

        // 기본 정보 저장
        Member member = new Member();
        member.setMemberName(memberRegisterRequestDto.getMemberName());
        member.setNickName(memberRegisterRequestDto.getNickName());
        member.setEmail(memberRegisterRequestDto.getEmail());
        member.setPassword(memberRegisterRequestDto.getFirstPassword());
        member.setAuthStatus(0);
        memberService.join(member);

        //임의의 authKey 생성 & 이메일 발송
        String authKey = mss.sendAuthMail(memberRegisterRequestDto.getEmail());

        Map<String, String> map = new HashMap<String, String>();
        map.put("email", memberRegisterRequestDto.getEmail());
        map.put("authKey", authKey);
        log.info("members email & authKey = {}", map);

        //DB에 authKey 업데이트
        memberService.updateAuthKey(map);

        redirectAttributes.addAttribute("afterSignUpStatus", true);

        return "redirect:/login/member/";
    }

//    @GetMapping("/register/member/preEmailSignUpConfirm/{email}") // 가입하기 버튼 누르고 난뒤 뜨는 페이지
//    public String preEmailSignUpConfirm(Model model, @PathVariable("email") String email) {
//        Member findedMember = memberService.findOneMemberByEmail(email);
//
//        model.addAttribute("email", findedMember.getEmail());
//        model.addAttribute("memberName", findedMember.getMemberName());
//
//        return "registerComplete";
//    }

    @GetMapping("/member/signUpConfirm") // 도착 이메일에 표시되는 링크
    public String signUpConfirm(@RequestParam("email") String email, @RequestParam("authKey") String authKey) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("email", email);
        map.put("authKey", authKey);

        memberService.updateAuthStatus(map);

        return "redirect:/";
    }

    @GetMapping("/find/password") // 비밀번호 찾기 GET
    public String findPasswordForm(@ModelAttribute("passwordFindRequestDto") PasswordFindRequestDto passwordFindRequestDto) {

        return "findPassword";
    }

    @PostMapping("/find/password") // 비밀번호 찾기 POST
    public String findPassword(@Validated @ModelAttribute PasswordFindRequestDto passwordFindRequestDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Optional<Member> findedMember = memberService.findOneMemberByEmail(passwordFindRequestDto.getEmail());

        if (!findedMember.isPresent()) {
            bindingResult.reject("MemberNotFound", "해당 이메일을 갖는 유저가 존재하지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            return "findPassword";
        }

        String password = mss.sendTemporaryPasswordMail(passwordFindRequestDto.getEmail());

        Map<String, String> map = new HashMap<String, String>();
        map.put("email", passwordFindRequestDto.getEmail());
        map.put("password", password);

        //DB에 password 업데이트
        memberService.updatePassword(map);

        redirectAttributes.addAttribute("after2xx", true);

        return "redirect:/login/member";
    }

}
