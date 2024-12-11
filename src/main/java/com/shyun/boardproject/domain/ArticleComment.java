package com.shyun.boardproject.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class ArticleComment extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    private Article article; // 게시글 (ID)

    @Setter
    @JoinColumn(name = "userId")
    @ManyToOne(optional = false)
    private UserAccount userAccount; // 유저 정보 (ID)

    @Setter
    @Column(updatable = false) // 부모댓글은 변경할수없게 해야된다.
    private Long parentCommentId; // 부모 댓글 ID


    //LinkedHashSet<>() 순서가 있는 Set
    @ToString.Exclude
    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL)
    private Set<ArticleComment> childComments = new LinkedHashSet<>();

    @Setter @Column(nullable = false, length = 500) private String content; // 본문


    protected ArticleComment() {}

    private ArticleComment(Article article, UserAccount userAccount, Long parentCommentId, String content) {
        this.article = article;
        this.userAccount = userAccount;
        this.parentCommentId = parentCommentId;
        this.content = content;
    }

    public static ArticleComment of(Article article, UserAccount userAccount, String content) {
        //처음 작성하는 댓글에는 parentCommentId가없으니 대댓글이 작성시 추가하는 방식
        return new ArticleComment(article, userAccount, null, content);
    }

    //대댓글을 추가
    public void addChildComment(ArticleComment child) {
        child.setParentCommentId(this.getId());

        //this.getChildComments -> private Set<ArticleComment> childComments = new LinkedHashSet<>();
        //여기다가 child를 넣으면 되는데 그냥 넣으면 안된다 왜냐하면 처음넣는 child는 부모정보가없기 때문이다.
        //그래서 child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

}