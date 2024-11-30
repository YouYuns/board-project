package com.shyun.boardproject.repository;

import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.shyun.boardproject.domain.Article;
import com.shyun.boardproject.domain.QArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>,
        QuerydslBinderCustomizer<QArticle> {

    //원래 인터페이스라서 구현에 넣을수 없는데 default를 추가함으로써 default method로 만든다.
    @Override
    default void customize(QuerydslBindings bindings, QArticle root){
        bindings.excludeUnlistedProperties(true);
        //현재 QuerydslPredicateExecutor에의해서 모든 필드에 대해서 모든 검색이 열려있는데
        //선택적으로 검색이 가능하게끔 하기위한것이다. list되지않은건 검색이되지않게 true로 설정
        //그리고 원하는 힘드를 including으로 넣는다.
        bindings.including(root.title, root.content, root.hashtag, root.createdAt, root.createdBy);
        //bindings.bind(root.title).first(StringExpression::likeIgnoreCase); // like '${v}'
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase); //like '%s{v}%'
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase); //like '%s{v}%'
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq); //시분초는 일치해야된다.
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}