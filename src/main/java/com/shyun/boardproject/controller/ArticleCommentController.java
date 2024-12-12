package com.shyun.boardproject.controller;


import com.shyun.boardproject.dto.request.ArticleCommentRequest;
import com.shyun.boardproject.dto.security.BoardPrincipal;
import com.shyun.boardproject.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String postNewArticleComment(
            ArticleCommentRequest articleCommentRequest,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/articles/" + articleCommentRequest.articleId();
    }

    @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(@PathVariable Long commentId, @AuthenticationPrincipal BoardPrincipal boardPrincipal,  Long articleId){

        articleCommentService.deleteArticleComment(commentId,boardPrincipal.getUsername());
        return "redirect:/articles/" + articleId;
    }
}
