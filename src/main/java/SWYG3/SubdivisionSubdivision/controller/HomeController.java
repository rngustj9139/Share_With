package SWYG3.SubdivisionSubdivision.controller;

import SWYG3.SubdivisionSubdivision.dto.ArticleSearch;
import SWYG3.SubdivisionSubdivision.dto.InfiniteScrollingArticleResponseDto;
import SWYG3.SubdivisionSubdivision.dto.InfiniteScrollingRequestDto;
import SWYG3.SubdivisionSubdivision.entity.Article;
import SWYG3.SubdivisionSubdivision.entity.Member;
import SWYG3.SubdivisionSubdivision.login.SessionConst;
import SWYG3.SubdivisionSubdivision.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final ArticleService articleService;

    @GetMapping("/")
    public String home(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model, @ModelAttribute("articleSearch") ArticleSearch articleSearch) {
        List<Article> articles = articleService.findAllBySearchWord(articleSearch);

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("articles", articles);

        return "mainPage";
    }

    // 무한 스크롤링 구현을 위한 Rest API
    @PostMapping("/api/articles")
    @ResponseBody
    public List<InfiniteScrollingArticleResponseDto> getArticlePages(@RequestBody InfiniteScrollingRequestDto infiniteScrollingRequestDto, Model model) {
        String stringLastArticleId = infiniteScrollingRequestDto.getLastArticleId();
        String stringSize = infiniteScrollingRequestDto.getSize();
        String stringPageNum = infiniteScrollingRequestDto.getPageNum();
        Long lastArticleId = Long.parseLong(stringLastArticleId);
        int size = Integer.parseInt(stringSize);
        int pageNum = Integer.parseInt(stringPageNum);
        int totalArticleSize = articleService.findAll().size();

        if (totalArticleSize / size < pageNum) { // 유저의 페이지 맨 아래로 스크롤한 횟수가 (전체 소분글 / 페이지를 맨 아래로 내릴 때 새로 생기는 소분글) 보다 많으면 빈 객체를 리턴함
            List<InfiniteScrollingArticleResponseDto> nullArticleResponseDto = new ArrayList<>();
            return nullArticleResponseDto;
        }

        List<Article> articles = new ArrayList<>();

        if (StringUtils.hasText(infiniteScrollingRequestDto.getArticleSearch())) {
            articles = articleService.fetchArticlePagesByArticleSearch(lastArticleId, size, infiniteScrollingRequestDto.getArticleSearch());
            log.info("articleSearch : " + infiniteScrollingRequestDto.getArticleSearch());
        }else {
            articles = articleService.fetchArticlePagesBy(lastArticleId, size);
        }

        log.info("=========== lastArticleId = {} ===========", lastArticleId);
        log.info("=========== size = {} ===========", size);

        List<InfiniteScrollingArticleResponseDto> additionalArticles = articles.stream()
                .map(a -> new InfiniteScrollingArticleResponseDto(a))
                .collect(Collectors.toList());

        return additionalArticles;
    }

}
