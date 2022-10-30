package SWYG3.SubdivisionSubdivision.entity;

import SWYG3.SubdivisionSubdivision.file.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ARTICLE_ID")
    private Long id;

    private String itemType;
    private String title;
    private String location;
    private Integer limitPersonnel;
    private Integer applicationPersonnel;

    @Lob
    private String content;

    @ElementCollection
    @CollectionTable(name = "IMAGE_FILES")
    private List<UploadFile> imageFiles;

    @ManyToOne(fetch = FetchType.LAZY) // ToOne 관계는 디폴트가 즉시로딩(EAGER)이기 때문에 지연로딩으로 바꾸어준다.
    @JoinColumn(name = "MEMBER_ID") // 연관관계의 주인(외래키 관리)
    @JsonIgnore
    private Member member; // 게시글 작성자

    @ManyToMany
    @JoinTable(name = "ARTICLE_APPLICATIONMEMBER",
            joinColumns = @JoinColumn(name = "ARTICLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "MEMBER_ID")) // 중간 테이블 매핑
    @JsonIgnore
    private List<Member> members = new ArrayList<>(); // 게시글에 지원한 맴버들

}
