package com.shyun.boardproject.repository.querydsl;

import com.shyun.boardproject.domain.Hashtag;
import com.shyun.boardproject.domain.QHashtag;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class HashtagRepositoryCustomImpl extends QuerydslRepositorySupport implements HashtagRepositoryCustom {

    public HashtagRepositoryCustomImpl() {
        super(Hashtag.class);
    }

    @Override
    public List<String> findAllHashtagNames() {
        QHashtag hashtag = QHashtag.hashtag;
        return from(hashtag)
                .select(hashtag.hashtagName)
                .fetch();
    }
}
