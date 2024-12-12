package com.shyun.boardproject.config;


import com.shyun.boardproject.dto.security.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware(){
        //SecurityContextHolder : 인증정보를 모두 가지고있는 클래스
        // SecurityContext를 불러오는데 그안에 Authentication 정보를 불러오고
        // filter로 Authentication 으로 인증한 상태인지를 확인
        // Security Principal정보를 가져온다.
        // 우리는 Principal정보를 갖고있는 UserDetails를 구현했는데 그게 바로 BoardPrincipal이다 그래서 BoardPrincipal로 타입캐스팅후
        // username을 가져와서 실제 유저정보를 가져온다.
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(BoardPrincipal.class::cast) // BoardPrincipal로 타입 cast
                .map(BoardPrincipal::getUsername);
    }
}
