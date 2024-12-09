package com.shyun.boardproject.dto.request;

import com.shyun.boardproject.domain.UserAccount;
import com.shyun.boardproject.dto.ArticleDto;
import com.shyun.boardproject.dto.HashtagDto;
import com.shyun.boardproject.dto.UserAccountDto;

import java.util.Set;

public record ArticleRequest(String title, String content) {
    public static ArticleRequest of(String title, String content) {
        return new ArticleRequest(title, content);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto) {
        return toDto(userAccountDto, null);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto, Set<HashtagDto> hashtagDtos) {
        return ArticleDto.of(
                userAccountDto,
                title,
                content,
                hashtagDtos
        );
    }
}
