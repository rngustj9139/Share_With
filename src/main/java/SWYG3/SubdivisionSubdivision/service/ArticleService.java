package SWYG3.SubdivisionSubdivision.service;

import SWYG3.SubdivisionSubdivision.dto.ArticleRegisterRequestDto;
import SWYG3.SubdivisionSubdivision.dto.ArticleSearch;
import SWYG3.SubdivisionSubdivision.entity.Article;
import SWYG3.SubdivisionSubdivision.entity.Member;
import SWYG3.SubdivisionSubdivision.file.FileStore;
import SWYG3.SubdivisionSubdivision.file.UploadFile;
import SWYG3.SubdivisionSubdivision.repository.ArticleRepository;
import SWYG3.SubdivisionSubdivision.repository.MemberRepository;
import SWYG3.SubdivisionSubdivision.repository.SpringDataJpaArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final SpringDataJpaArticleRepository springDataJpaArticleRepository;
    private final FileStore fileStore;

    public Article load(Article article) {
        return articleRepository.save(article);
    }

    public Article findById(Long id) {
        return articleRepository.findById(id);
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public void updateArticle(Long originalArticleId, ArticleRegisterRequestDto updatedArticle) throws IOException {
        Article findedArticle = articleRepository.findById(originalArticleId);

        findedArticle.setTitle(updatedArticle.getTitle());
        findedArticle.setItemType(updatedArticle.getItemType());
        findedArticle.setLocation(updatedArticle.getLocation());
        findedArticle.setLimitPersonnel(updatedArticle.getLimitPersonnel());
        findedArticle.setContent(updatedArticle.getContent());
        // 이미지 파일 업데이트 시작
        List<MultipartFile> imageFiles = updatedArticle.getImageFiles();
        List<UploadFile> storeImageFiles = fileStore.storeFiles(imageFiles);
        findedArticle.setImageFiles(storeImageFiles);
        // 이미지 파일 업데이트 끝
    }

    public void updateArticleWithOutImages(Long originalArticleId, ArticleRegisterRequestDto updatedArticle) throws IOException {
        Article findedArticle = articleRepository.findById(originalArticleId);

        findedArticle.setTitle(updatedArticle.getTitle());
        findedArticle.setItemType(updatedArticle.getItemType());
        findedArticle.setLocation(updatedArticle.getLocation());
        findedArticle.setLimitPersonnel(updatedArticle.getLimitPersonnel());
        findedArticle.setContent(updatedArticle.getContent());
    }

    public void updateApplicationPersonnel(Long articleId, Member loginMember) {
        Article findedArticle = articleRepository.findById(articleId);
        Integer applicationPersonnel = findedArticle.getApplicationPersonnel();
        applicationPersonnel += 1;
        findedArticle.setApplicationPersonnel(applicationPersonnel);
        findedArticle.getMembers().add(loginMember);
        Member fromDbMember = memberRepository.findById(loginMember.getId());
        fromDbMember.getApplicationArticles().add(findedArticle);
    }

    public List<Article> findAllBySearchWord(ArticleSearch articleSearch) {
        return articleRepository.findAllBySearchWord(articleSearch);
    }

    // 무한 스크롤링 구현을 비즈니스 로직 시작 (검색어가 없는 경우)
    public List<Article> fetchArticlePagesBy(Long lastArticleId, int size) {
        Page<Article> articles = fetchPages(lastArticleId, size);
        List<Article> listArticles = new ArrayList<>();

        for (Article article : articles) {
            listArticles.add(article);
        }

        return listArticles;
    }

    private Page<Article> fetchPages(Long lastArticleId, int size) {
        PageRequest pageRequest = PageRequest.of(0, size); // 페이지네이션을 위한 PageRequest, 페이지는 0으로 고정한다.
        return springDataJpaArticleRepository.findByIdLessThanOrderByIdDesc(lastArticleId, pageRequest); // JPA 쿼리 메소드
    }
    // 무한 스크롤링 구현을 비즈니스 로직 끝 (검색어가 없는 경우)

    // 무한 스크롤링 구현을 비즈니스 로직 시작 (검색어가 존재하는 경우)
    public List<Article> fetchArticlePagesByArticleSearch(Long lastArticleId, int size, String articleSearch) {
        Page<Article> articles = fetchPagesArticleSearch(lastArticleId, size, articleSearch);
        List<Article> listArticles = new ArrayList<>();

        for (Article article : articles) {
            listArticles.add(article);
        }

        return listArticles;
    }

    private Page<Article> fetchPagesArticleSearch(Long lastArticleId, int size, String articleSearch) {
        PageRequest pageRequest = PageRequest.of(0, size); // 페이지네이션을 위한 PageRequest, 페이지는 0으로 고정한다.
        return springDataJpaArticleRepository.findByIdLessThanAndItemTypeOrderByIdDesc(lastArticleId, pageRequest, articleSearch); // JPA 쿼리 메소드
    }
    // 무한 스크롤링 구현을 비즈니스 로직 끝 (검색어가 존재하는 경우)

}
