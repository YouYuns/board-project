package com.shyun.boardproject.repository.querydsl;

import com.shyun.boardproject.domain.Article;
import com.shyun.boardproject.domain.Hashtag;
import com.shyun.boardproject.dto.ArticleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ArticleRepositoryCustom {

    /**
     * @deprecated 해시태그 도메인을 새로 만들었으므로 이 코드는 더 이상 사용할 필요 없다.
     * @see HashtagRepositoryCustom#findAllHashtagNames()
     */
    @Deprecated
    List<String> findAllDistinctHashtags();
    Page<Article> findByHashtagNames(List<String> hashtagNames, Pageable pageable);
}