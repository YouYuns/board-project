package com.shyun.boardproject.repository;

import com.shyun.boardproject.domain.UserAccount;
import com.shyun.boardproject.domain.projection.ArticleProjection;
import com.shyun.boardproject.domain.projection.UserAccountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(excerptProjection = UserAccountProjection.class)
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
}