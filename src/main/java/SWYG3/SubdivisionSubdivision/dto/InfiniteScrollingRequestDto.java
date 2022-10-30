package SWYG3.SubdivisionSubdivision.dto;

import lombok.Data;

@Data
public class InfiniteScrollingRequestDto {

    private String articleSearch;
    private String lastArticleId;
    private String size;
    private String pageNum;

}
