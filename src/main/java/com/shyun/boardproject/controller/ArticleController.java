package com.shyun.boardproject.controller;


import com.shyun.boardproject.domain.Hashtag;
import com.shyun.boardproject.domain.type.FormStatus;
import com.shyun.boardproject.domain.type.SearchType;
import com.shyun.boardproject.dto.request.ArticleRequest;
import com.shyun.boardproject.dto.response.ArticleResponse;
import com.shyun.boardproject.dto.response.ArticleWithCommentsResponse;
import com.shyun.boardproject.service.ArticleService;
import com.shyun.boardproject.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@RequestMapping("/articles")
@RequiredArgsConstructor
@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;

    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {
        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());

        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchTypes", SearchType.values());
        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map) {
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));
        map.addAttribute("article", article) ;
        map.addAttribute("articleComments", article.articleCommentsResponse());
        map.addAttribute("totalCount", articleService.getArticleCount());
        return "articles/detail";
    }

    @GetMapping("/search-hashtag")
    public String searchHashtag(
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {
        Page<ArticleResponse> articles = articleService.searchArticlesViaHashtag(searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        List<Set<Hashtag>> hastags = articleService.getHashtags();
        map.addAttribute("articles", articles);
        map.addAttribute("hashtags", hastags);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchTypes", SearchType.HASHTAG);
        return "articles/search-hashtag";
    }
    // 게시글 작성폼 이동
    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATED);
        return "articles/form";
    }

    //게시글 수정폼 이동
    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);
        return "articles/form";
    }

    //게시글 수정
    @PostMapping("/{articleId}/form")
    public String updateArticle(
            @PathVariable Long articleId,
            ArticleRequest articleRequest
    ){
        return null;
    }



    @PostMapping("/{articleId}/form")
    public String updateArticle(@PathVariable Long articleId){
        return "articles/form";
    }


    @PostMapping("/{articleId}/delete")
    public String deleteArticle(@PathVariable Long articleId) {
        //TODO : 인증정보를 넣어줘야한다.
        articleService.deleteArticle(articleId);
        return "redirect:/articles";
    }



}
