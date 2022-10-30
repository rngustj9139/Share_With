package SWYG3.SubdivisionSubdivision.dto;

import SWYG3.SubdivisionSubdivision.entity.Article;
import SWYG3.SubdivisionSubdivision.file.UploadFile;
import lombok.Data;

import java.util.List;

@Data
public class InfiniteScrollingArticleResponseDto {

    private Long id;
    private String itemType;
    private String title;
    private String location;
    private Integer limitPersonnel;
    private Integer applicationPersonnel;
    private String content;
    private List<UploadFile> imageFiles;

    public InfiniteScrollingArticleResponseDto(Article article) {
        this.id = article.getId();
        this.itemType = article.getItemType();
        this.title = article.getTitle();
        this.location = article.getLocation();
        this.limitPersonnel = article.getLimitPersonnel();
        this.applicationPersonnel = article.getApplicationPersonnel();
        this.content = article.getContent();
        this.imageFiles = article.getImageFiles();
    }

}
