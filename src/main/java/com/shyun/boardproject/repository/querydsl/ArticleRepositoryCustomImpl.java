package com.shyun.boardproject.repository.querydsl;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shyun.boardproject.domain.Article;
import com.shyun.boardproject.domain.Hashtag;
import com.shyun.boardproject.domain.QArticle;
import com.shyun.boardproject.dto.ArticleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Collection;
import java.util.List;
import java.util.Set;

//Impl은 고정이다 이름 변경시 설정변경해야됨
public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {

    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    @Override
    public List<String> findAllDistinctHashtags() {
        QArticle article = QArticle.article;
        return from(article)
                .distinct()
                .select(article.hashtags.any().hashtagName)
                .where(article.hashtags.isNotEmpty())
                .fetch();

    }

    @Override
    public Page<Article> findByHashtagNames(List<String> hashtag, Pageable pageable) {
        QArticle article = QArticle.article;

        JPQLQuery<Article> query =  from(article)
                .select(article)
                .where(article.hashtags.any().hashtagName.in(hashtag));
        List<Article> articles = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(articles, pageable, query.fetchCount());
    }
}
