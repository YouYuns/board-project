package com.shyun.boardproject.service;


import com.shyun.boardproject.domain.Article;
import com.shyun.boardproject.domain.ArticleComment;
import com.shyun.boardproject.domain.UserAccount;
import com.shyun.boardproject.dto.ArticleCommentDto;
import com.shyun.boardproject.repository.ArticleCommentRepository;
import com.shyun.boardproject.repository.ArticleRepository;
import com.shyun.boardproject.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ArticleCommentService {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;


    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComment(Long articleId) {
        return articleCommentRepository.findByArticle_Id(articleId)
                .stream()
                .map(ArticleCommentDto::from)
                .toList();

    }


    //댓글 저장
    public void saveArticleComment(ArticleCommentDto dto) {
        try{
            Article article = articleRepository.getReferenceById(dto.articleId());
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
            ArticleComment articleComment = dto.toEntity(article, userAccount);

            if(dto.parentCommentId() != null){
                ArticleComment parentComment = articleCommentRepository.getReferenceById(dto.parentCommentId());
                parentComment.addChildComment(articleComment);
            }else{
                articleCommentRepository.save(articleComment);
            }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 저장 실패, 댓글 작성에 필요한 정보를 찾을 수 없습니다 - {}", e.getLocalizedMessage());
        }
    }

    public void deleteArticleComment(Long articleCommentId, String userId) {

        articleCommentRepository.deleteByIdAndUserAccount_UserId(articleCommentId, userId);
    }
}
