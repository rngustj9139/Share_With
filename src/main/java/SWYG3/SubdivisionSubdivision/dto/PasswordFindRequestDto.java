package SWYG3.SubdivisionSubdivision.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class PasswordFindRequestDto {

    @NotBlank(message = "이메일을 입력하여 주세요")
    private String email;

}
