package SWYG3.SubdivisionSubdivision.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "MEMBER")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    private String memberName;
    private String nickName;
    private String email;
    private String password;
    private String authKey;
    private Integer authStatus; // 0 or 1만 가능 (0은 회원가입시 이메일 인증 안받은 회원, 1은 회원가입시 이메일 인증 받은 회원)

    @OneToMany(mappedBy = "member") // 본인이 직접 작성한 글
    private List<Article> articles = new ArrayList<>();

    @ManyToMany(mappedBy = "members") // 본인이 지원한 글
    private List<Article> applicationArticles = new ArrayList<>();

}
