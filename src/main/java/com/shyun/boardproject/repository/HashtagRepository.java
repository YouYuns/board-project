package com.shyun.boardproject.repository;

import com.shyun.boardproject.domain.Hashtag;
import com.shyun.boardproject.repository.querydsl.HashtagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Set;

@RepositoryRestResource
public interface HashtagRepository extends JpaRepository<Hashtag, Long>,
        QuerydslPredicateExecutor<Hashtag>,
        HashtagRepositoryCustom {
    Set<Hashtag> findByHashtagNameIn(Set<String> hashtagNames);
}
