package com.shyun.boardproject.repository;

import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.shyun.boardproject.domain.ArticleComment;
import com.shyun.boardproject.domain.QArticleComment;
import com.shyun.boardproject.domain.projection.ArticleCommentProjection;
import com.shyun.boardproject.domain.projection.ArticleProjection;
import com.shyun.boardproject.dto.ArticleCommentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(excerptProjection = ArticleCommentProjection.class)
public interface ArticleCommentRepository extends
        JpaRepository<ArticleComment, Long>,
        QuerydslPredicateExecutor<ArticleComment>,
        QuerydslBinderCustomizer<QArticleComment> {

    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.content, root.createdAt, root.createdBy);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }

    void deleteByIdAndUserAccount_UserId(Long articleCommentId, String userId);

    List<ArticleComment> findByArticle_Id(Long articleId);
}