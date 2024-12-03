package com.shyun.boardproject.dto;

import java.time.LocalDateTime;

public record ArticleCommentDto(LocalDateTime createdAt, String createdBy  ,String content,
                                LocalDateTime modifiedAt, String modifiedBy) {
    public static ArticleCommentDto of(LocalDateTime createdAt, String createdBy, String content, LocalDateTime modifiedAt, String modifiedBy) {
    return new ArticleCommentDto(createdAt, createdBy, content, modifiedAt, modifiedBy);
    }
}
