package com.shyun.boardproject.repository.querydsl;

import com.shyun.boardproject.domain.Hashtag;

import java.util.List;
import java.util.Set;

public interface ArticleRepositoryCustom {
    List<Set<Hashtag>> findAllDistinctHashtags();
}
