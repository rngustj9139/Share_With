package SWYG3.SubdivisionSubdivision.controller;

import SWYG3.SubdivisionSubdivision.dto.ArticleRegisterRequestDto;
import SWYG3.SubdivisionSubdivision.dto.ArticleSearch;
import SWYG3.SubdivisionSubdivision.entity.Article;
import SWYG3.SubdivisionSubdivision.entity.Member;
import SWYG3.SubdivisionSubdivision.file.FileStore;
import SWYG3.SubdivisionSubdivision.file.UploadFile;
import SWYG3.SubdivisionSubdivision.login.SessionConst;
import SWYG3.SubdivisionSubdivision.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AticleController {

    private final ArticleService articleService;
    private final FileStore fileStore;

    @GetMapping("/register/article") // 글쓰기 폼 GET
    public String registerArticleForm(@ModelAttribute("articleRegisterRequestDto") ArticleRegisterRequestDto articleRegisterRequestDto, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model, @ModelAttribute("articleSearch") ArticleSearch articleSearch) {
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("articleSearch", articleSearch);

        if(StringUtils.hasText(articleSearch.getItemType())) {
            List<Article> articles = articleService.findAllBySearchWord(articleSearch);

            model.addAttribute("loginMember", loginMember);
            model.addAttribute("articles", articles);

            return "mainPage";
        }

        return "registerArticle";
    }

    @PostMapping("/register/article") // 글쓰기 폼에서 글 등록 POST
    public String registerArticle(@Validated @ModelAttribute ArticleRegisterRequestDto articleRegisterRequestDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member loginMember, @ModelAttribute("articleSearch") ArticleSearch articleSearch) throws IOException {
        if (articleRegisterRequestDto.getImageFiles().size() > 3) {
            bindingResult.reject("fileSizeError", "이미지 파일의 개수는 3개를 넘을 수 없습니다.");
        }

        log.info("이미지 첨부 체크 = {}", articleRegisterRequestDto.getImageFiles().isEmpty());

        if (articleRegisterRequestDto.getImageFiles().isEmpty()) {
            bindingResult.rejectValue("imageFiles", "jpg, png 형식의 이미지만 업로드 가능하며 최소 1개의 이미지 파일을 업로드 해야합니다.");
            log.info("jpg, png 형식의 이미지만 업로드 가능하며 최소 1개의 이미지 파일을 업로드 해야합니다.");
        } else {
            List<MultipartFile> imageFilesFromDto = articleRegisterRequestDto.getImageFiles();

            for (MultipartFile file : imageFilesFromDto) {
                String originalFilename = file.getOriginalFilename();
                int pos = originalFilename.lastIndexOf(".");
                String substring = originalFilename.substring(pos + 1);
                log.info("substring: {}", substring);

                if (!substring.equals("png")) {
                    if (!substring.equals("jpg")) {
                        bindingResult.rejectValue("imageFiles", "jpg, png 형식의 이미지만 업로드 가능하며 최소 1개의 이미지 파일을 업로드 해야합니다.");
                        break;
                    }
                }
            }
        }

        if (bindingResult.hasErrors()) {
            return "registerArticle";
        }

        Article article = new Article();
        article.setTitle(articleRegisterRequestDto.getTitle());
        article.setLocation(articleRegisterRequestDto.getLocation());
        article.setLimitPersonnel(articleRegisterRequestDto.getLimitPersonnel());
        article.setApplicationPersonnel(0);
        article.setContent(articleRegisterRequestDto.getContent());
        article.setItemType(articleRegisterRequestDto.getItemType());
        article.setMember(loginMember);

        // 파일 저장 시작
        List<MultipartFile> imageFiles = articleRegisterRequestDto.getImageFiles();
        List<UploadFile> uploadFiles = fileStore.storeFiles(imageFiles);
        article.setImageFiles(uploadFiles);
        // 파일 저장 끝

        Article savedArticle = articleService.load(article);

        redirectAttributes.addAttribute("articleId", savedArticle.getId());
        redirectAttributes.addAttribute("status", true); // url 뒤에 ?status=true로 표시된다.

        return "redirect:/articles/{articleId}";
    }

    @GetMapping("/articles/{articleId}") // 등록 완료한 글 조회
    public String oneArticle(@PathVariable("articleId") Long articleId, Model model, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, RedirectAttributes redirectAttributes, @ModelAttribute("articleSearch") ArticleSearch articleSearch) {
        if(StringUtils.hasText(articleSearch.getItemType())) {
            List<Article> articles = articleService.findAllBySearchWord(articleSearch);

            model.addAttribute("loginMember", loginMember);
            model.addAttribute("articles", articles);

            return "mainPage";
        }

        Article findedArticle = articleService.findById(articleId);

        Member ownerMemberOfArticle = findedArticle.getMember();
        List<Member> applicants = findedArticle.getMembers();

        model.addAttribute("article", findedArticle);
        model.addAttribute("member", ownerMemberOfArticle);
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("applicants", applicants);
        model.addAttribute("articleSearch", articleSearch);

        return "oneArticle";
    }

    @GetMapping("/articles/{articleId}/edit") // 작성글 편집 GET
    public String editOneArticleForm(@PathVariable("articleId") Long articleId, Model model, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, @ModelAttribute("articleSearch") ArticleSearch articleSearch) {
        if(StringUtils.hasText(articleSearch.getItemType())) {
            List<Article> articles = articleService.findAllBySearchWord(articleSearch);

            model.addAttribute("loginMember", loginMember);
            model.addAttribute("articles", articles);

            return "mainPage";
        }

        Article updatedArticle = articleService.findById(articleId);
        log.info("edit 페이지의 첫번째 이미지: {}", updatedArticle.getImageFiles().get(0).getUploadFileName());

        model.addAttribute("updatedArticle", updatedArticle);
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("articleSearch", articleSearch);

        return "editArticle";
    }

    @PostMapping("/articles/{articleId}/edit") // 작성글 편집 POST
    public String editOneArticleForm(@Validated @ModelAttribute("updatedArticle") ArticleRegisterRequestDto updatedArticle, BindingResult bindingResult, @PathVariable("articleId") Long articleId, RedirectAttributes redirectAttributes) throws IOException {
        if (updatedArticle.getImageFiles().size() > 3) {
            bindingResult.reject("fileSizeError", "이미지 파일의 개수는 3개를 넘을 수 없습니다.");
        }

        if (bindingResult.hasErrors()) {
            return "editArticle";
        }

        log.info("이미지 첨부 체크 = {}", updatedArticle.getImageFiles().isEmpty());
        log.info("이미지 첨부 사이즈 = {}", updatedArticle.getImageFiles().size());

//      bindingResult.rejectValue("imageFiles", "jpg, png 형식의 이미지만 업로드 가능하며 최소 1개의 이미지 파일을 업로드 해야합니다.");
//      log.info("jpg, png 형식의 이미지만 업로드 가능하며 최소 1개의 이미지 파일을 업로드 해야합니다.");

        List<MultipartFile> imageFilesFromDto = updatedArticle.getImageFiles();
        for (MultipartFile file : imageFilesFromDto) {
            String originalFilename = file.getOriginalFilename();
            int pos = originalFilename.lastIndexOf(".");
            String substring = originalFilename.substring(pos + 1);
            log.info("substring: {}", substring);

            if (!substring.equals("png")) {
                if (!substring.equals("jpg")) {
                    articleService.updateArticleWithOutImages(articleId, updatedArticle);

                    redirectAttributes.addAttribute("articleId", articleId);

                    return "redirect:/articles/{articleId}";
                }
            }
        }

         if (!updatedArticle.getImageFiles().isEmpty()) {
            for (MultipartFile file : imageFilesFromDto) {
                String originalFilename = file.getOriginalFilename();
                int pos = originalFilename.lastIndexOf(".");
                String substring = originalFilename.substring(pos + 1);
                log.info("substring: {}", substring);

                if (!substring.equals("png")) {
                    if (!substring.equals("jpg")) {
                        bindingResult.rejectValue("imageFiles", "jpg, png 형식의 이미지만 업로드 가능하며 최소 1개의 이미지 파일을 업로드 해야합니다.");
                        break;
                    }
                }
            }
        }

        articleService.updateArticle(articleId, updatedArticle);

        redirectAttributes.addAttribute("articleId", articleId);

        return "redirect:/articles/{articleId}";
    }

    @PostMapping("/articles/{articleId}/register") // 소분 신청하기
    public String applyArticle(@PathVariable("articleId") Long articleId, RedirectAttributes redirectAttributes, @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) {
        Article findedArticle = articleService.findById(articleId);

        Optional<Member> alreadyApplicant = findedArticle.getMembers().stream()
                .filter(m -> m.getId() == loginMember.getId())
                .findFirst();

        if (alreadyApplicant.isPresent()) { // 이미 지원 했었다면 지원 실패
            redirectAttributes.addAttribute("failedApplication", true);

            return "redirect:/articles/{articleId}";
        }

        if (findedArticle.getApplicationPersonnel() >= findedArticle.getLimitPersonnel()) { // 현재 지원자 수가 한계 지원자 수를 넘는다면
            redirectAttributes.addAttribute("isMeetTheLimitPersonnel", true);

            return "redirect:/articles/{articleId}";
        }

        articleService.updateApplicationPersonnel(articleId, loginMember);

        redirectAttributes.addAttribute("application", true);

        return "redirect:/articles/{articleId}";
    }

    @ResponseBody
    @GetMapping("/article/images/{filename}") // 이미지파일 여러개 표시하기
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

}
