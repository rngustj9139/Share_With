package SWYG3.SubdivisionSubdivision.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class ArticleRegisterRequestDto {

    @NotBlank(message = "상품 타입을 입력 해주세요")
    private String itemType;

    @NotBlank(message = "제목을 입력 해주세요")
    private String title;

    @NotBlank(message = "지역 및 동네를 입력 해주세요")
    private String location;

    private Integer limitPersonnel;

    @NotBlank(message = "본문을 입력해 주세요")
    private String content;

    private List<MultipartFile> imageFiles; // 이미지파일(여러개)

}
