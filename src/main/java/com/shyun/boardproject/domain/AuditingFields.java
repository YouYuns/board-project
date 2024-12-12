package com.shyun.boardproject.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
//추상클래스 변경
//회원 도메인에서 인증이 없는 상태의 회원정보를 저장하기 위해서 createdBy, modifiedBy를 엔티티가 직접 작성해야되는
//요구사항이 생길것으므로 이것이 가능하게 접근제어자 변경
public abstract class AuditingFields {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt; //생성일시

    @CreatedBy
    @Column(nullable = false, length = 100, updatable = false)
    protected String createdBy; //생성자

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = false)
    protected LocalDateTime modifiedAt; //수정일시

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    protected String modifiedBy; //수정자
}
