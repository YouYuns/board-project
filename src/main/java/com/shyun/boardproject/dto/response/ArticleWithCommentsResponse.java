package com.shyun.boardproject.dto.response;
import com.shyun.boardproject.dto.ArticleCommentDto;
import com.shyun.boardproject.dto.ArticleWithCommentsDto;
import com.shyun.boardproject.dto.HashtagDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        Set<String> hashtags,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Set<ArticleCommentResponse> articleCommentsResponse
) {

    public static ArticleWithCommentsResponse of(Long id, String title, String content, Set<String> hashtags, LocalDateTime createdAt, String email, String nickname, String userId, Set<ArticleCommentResponse> articleCommentResponses) {
        return new ArticleWithCommentsResponse(id, title, content, hashtags, createdAt, email, nickname, userId, articleCommentResponses);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentsResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtagDtos().stream()
                        .map(HashtagDto::hashtagName)
                        .collect(Collectors.toUnmodifiableSet())
                ,
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.userAccountDto().userId(),
                organizeChildComments(dto.articleCommentDtos())
        );
    }



    private static Set<ArticleCommentResponse> organizeChildComments(Set<ArticleCommentDto> dtos) {
        //1. 원래 dto받은것을 map으로 매핑을 한다.
        Map<Long, ArticleCommentResponse> map = dtos.stream()
                .map(ArticleCommentResponse::from)
                .collect(Collectors.toMap(ArticleCommentResponse::id, Function.identity()));
        //Map으로 바꾸는데 key는 id값을 넣고 , value는 동일한 값을 넣을때는 Function.identity()가있다.

        //2. 부모댓글만 hasParentComment로 부모가있는 자식댓글들만 뽑아서 그 parentComment에 자식댓글들을 다 저장한다.
        map.values().stream()
                .filter(ArticleCommentResponse::hasParentComment)
                .forEach(comment -> {
                    ArticleCommentResponse parentComment = map.get(comment.parentCommentId());
                    parentComment.childComments().add(comment);
                });


        //3. 그다음에 부모가있는 자식댓글들을 이제 지운다(filter)를 하면 root부모댓글들만 남게해야된다.
        // 그래서 다음으로 map을 한번더 stream해서 filter로 hasParentComment()가 null인경우만 나타나게filter한다
        return map.values().stream()
                .filter(comment -> !comment.hasParentComment())
                //정렬을 해줘야되서 Colloectors.toCollection()사용해야된다.
                //먼저 createdAt을 기준으로 내림차순하고 (만약 오름차순하고싶으면 reverse()라는게있음
                //그다음에 혹시 모르니 id로 내림차순 정렬한다. (createdAt이 우연히 같을 경우)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator
                                .comparing(ArticleCommentResponse::createdAt)
                                .reversed()
                                .thenComparingLong(ArticleCommentResponse::id)
                        )
                ));
    }
}