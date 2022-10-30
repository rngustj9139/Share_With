package SWYG3.SubdivisionSubdivision.repository;

import SWYG3.SubdivisionSubdivision.dto.ArticleSearch;
import SWYG3.SubdivisionSubdivision.entity.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArticleRepository {

    private final EntityManager em;

    public Article save(Article article) {
        em.persist(article);

        return article;
    }

    public Article findById(Long id) {
        Article findedArticle = em.find(Article.class, id);

        return findedArticle;
    }

    public List<Article> findAll() {
        return em.createQuery("select a from Article a", Article.class)
                .getResultList();
    }

    public List<Article> findAllBySearchWord(@Nullable ArticleSearch articleSearch) { // queryDSL 없이 검색 구현 => 아이템타입을 입력하지 않은경우에도 모든 것을 조회 가능하다. (but 이 방법은 권장하지 않는다.)
        String jpql = "select a from Article a";
        boolean isFirstCondition = true;

        if (StringUtils.hasText(articleSearch.getItemType())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            }
            jpql += " a.itemType like :itemType";
        }

        jpql += " order by a.id desc";

        TypedQuery<Article> query = em.createQuery(jpql, Article.class)
                .setMaxResults(15);

        if (StringUtils.hasText(articleSearch.getItemType())) {
            query = query.setParameter("itemType", articleSearch.getItemType());
        }

        return query.getResultList();
    }

}
