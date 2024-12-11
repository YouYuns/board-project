package com.shyun.boardproject.service;


import com.shyun.boardproject.domain.Hashtag;
import com.shyun.boardproject.repository.ArticleRepository;
import com.shyun.boardproject.repository.HashtagRepository;
import com.shyun.boardproject.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;


    public Set<String> parseHashtagNames(String content) {
        if(content == null){
            return Set.of();
        }

        Pattern pattern = Pattern.compile("#[\\w가-힣]+");
        //strip() 앞뒤 공백문자를 잘라준다.
        Matcher matcher = pattern.matcher(content.strip());
        Set<String> result = new HashSet<>();

        //find() - matcher에서 pattern에 해당하는 내용이 발견되면 조건
        while (matcher.find()) {

            //group()
            result.add(matcher.group().replace("#", ""));
        }
        return Set.copyOf(result);
    }
    public Set<Hashtag> findHashtagsByNames(Set<String> hashtagNames) {
        return new HashSet<>(hashtagRepository.findByHashtagNameIn(hashtagNames));
    }

    //게시글이없는 해시테그인경우 지운다.
    public void deleteHashtagWithoutArticles(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.getReferenceById(hashtagId);
        if(hashtag.getArticles().isEmpty()){
            hashtagRepository.delete(hashtag);
        }

    }
}
