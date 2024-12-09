package com.shyun.boardproject.repository.querydsl;

import com.shyun.boardproject.domain.Article;
import com.shyun.boardproject.domain.Hashtag;
import com.shyun.boardproject.domain.QArticle;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

//Impl은 고정이다 이름 변경시 설정변경해야됨
public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {

    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    @Override
    public List<Set<Hashtag>> findAllDistinctHashtags() {
        QArticle article = QArticle.article;
        return from(article)
                .distinct()
                .select(article.hashtags)
                .where(article.hashtags.isNotEmpty())
                .fetch();

    }
}
