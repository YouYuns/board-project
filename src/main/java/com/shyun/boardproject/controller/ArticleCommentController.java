package com.shyun.boardproject.controller;


import com.shyun.boardproject.dto.UserAccountDto;
import com.shyun.boardproject.dto.request.ArticleCommentRequest;
import com.shyun.boardproject.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewArticleComment(ArticleCommentRequest articleCommentRequest) {
        // TODO: 인증 정보를 넣어줘야한다.
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(UserAccountDto.of("a", "1234", "shyun@dfocus.net", "shyun", "테스트")));

        return "redirect:/articles/" + articleCommentRequest.articleId();
    }

    @PostMapping("/{commentID}/delete")
    public String deleteArticleComment(@PathVariable Long commentId, Long articleId){

        articleCommentService.deleteArticleComment(commentId,"1");
        return "redirect:/articles/" + articleId;
    }
}
