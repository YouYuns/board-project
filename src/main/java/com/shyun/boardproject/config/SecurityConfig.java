package com.shyun.boardproject.config;

import com.shyun.boardproject.dto.UserAccountDto;
import com.shyun.boardproject.dto.security.KakaoOAuth2Response;
import com.shyun.boardproject.repository.UserAccountRepository;
import com.shyun.boardproject.dto.security.BoardPrincipal;
import com.shyun.boardproject.service.UserAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/",
                                "/articles",
                                "/articles/search-hashtag"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(oAuth -> oAuth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .build();
    }

    // 예전 버전에는 정적파일들을 무시할걸 아래처럼 설정했지만 버전이 바뀌고 위에 PathRequest.toStaticResources(0.atCommonLocations()로 수정
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        // static resource, css - js
//        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService) {
        return username -> userAccountService
                .searchUser(username)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username: " + username));
    }

    /**
     * Oauth 2.0기술을 이용한 인증 정보를 처리한다.
     * 카카오 인증 방식을 선택
     *
     * TODO: 카카오 도메인에 결합되어있는 코드. 확장을 고려하면 별도 인증 처리 서비스 클래스로 분리하는 것이 좋지만, 현재 다른 OAuth 인증
     * @param userAccountService 게시판 서비스의 사용자 계정을 다룬느 서비스 로직
     * @param passwordEncoder 패스워드 암호화 도구
     * @return {@link OAuth2UserService} OAuth2 인증 사용자 정보를 읽어들이고 처리하는 서비스 인스턴스 변환
     */
    //OAuth 에서 UserDetailsService와같은 기능을 만들려고하는거다
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ) {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            KakaoOAuth2Response kakaoOAuth2Response = KakaoOAuth2Response.from(oAuth2User.getAttributes());

            /**
             * ID만들기 부분
             */
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            //고유값 ( 해당값은 application.yaml에서 client.registration.kakao.client-id값 )
            //"kakao"가 나올것이다.
            String provier = String.valueOf(kakaoOAuth2Response.id());
            String username = registrationId + "_" + provier;

            /**
             * PASSWORD만들기 부분
             * KAKAO로할경우 PASSWORD는 필요없지만 UserAccount 는 비밀번호는 필수값이므로 넣어줘야된다.
             */
            String dummyPassword = passwordEncoder.encode("{bcrypt}" + UUID.randomUUID());

            return userAccountService.searchUser(username)
                    .map(BoardPrincipal::from)
                    .orElseGet(() ->
                            BoardPrincipal.from(
                                    userAccountService.saveUser(
                                            username,
                                            dummyPassword,
                                            kakaoOAuth2Response.email(),
                                            kakaoOAuth2Response.nickname(),
                                            null
                                    )
                            )
                    );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
