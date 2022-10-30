package SWYG3.SubdivisionSubdivision.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.convert.DataSizeUnit;

import javax.validation.constraints.*;

@Getter @Setter
@NoArgsConstructor
public class MemberRegisterRequestDto {

    @NotBlank(message = "성함 입력은 필수 입니다.")
    private String memberName;

    @NotBlank(message = "닉네임 입력은 필수 입니다.")
    private String nickName;

    @Email
    @NotBlank(message = "이메일 입력은 필수 입니다.")
    private String email;

    @Pattern(regexp = "^.*(?=^.{8,15}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$", message = "비밀번호는 8~15자리의 숫자,문자,특수문자로 이루어져야합니다.")
    private String firstPassword;

    @NotBlank(message = "비밀번호 확인칸 입력은 필수 입니다.")
    private String secondPassword;

}
