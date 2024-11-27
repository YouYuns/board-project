package com.shyun.boardproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;

@ToString
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ArticleComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Article article; //게시글 (ID)
    @Setter
    @Column(nullable = false, length = 500)
    private String content; //본문

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt; //생성일시
    @CreatedBy
    @Column(nullable = false, length = 100)
    private String createdBy; //생성자
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt; //수정일시
    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String modifiedBy; //수정자


    private ArticleComment(Article article, String content) {
        this.article = article;
        this.content = content;
    }

    //팩토리 메서드
    public static ArticleComment of(Article article, String content){
        return new ArticleComment(article, content);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ArticleComment that = (ArticleComment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
