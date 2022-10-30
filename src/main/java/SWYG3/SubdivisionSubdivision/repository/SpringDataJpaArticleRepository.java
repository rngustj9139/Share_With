package SWYG3.SubdivisionSubdivision.repository;

import SWYG3.SubdivisionSubdivision.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByIdLessThanOrderByIdDesc(Long lastArticleId, PageRequest pageRequest);
    Page<Article> findByIdLessThanAndItemTypeOrderByIdDesc(Long lastArticleId, PageRequest pageRequest, String itemType);

}
