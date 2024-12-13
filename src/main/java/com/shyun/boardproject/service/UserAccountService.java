package com.shyun.boardproject.service;


import com.shyun.boardproject.domain.UserAccount;
import com.shyun.boardproject.dto.UserAccountDto;
import com.shyun.boardproject.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public Optional<UserAccountDto> searchUser(String username) {
        return userAccountRepository.findById(username)
                .map(UserAccountDto::from);
    }


    //Oauth로 할경우 createdBy는 username이므로 username을 마지막에 넣어준다.
    public UserAccountDto saveUser(String username, String password,  String email, String nickname, String memo) {
        return UserAccountDto.from(userAccountRepository.save(UserAccount.of(username, password, email, nickname, memo, username)));
    }
}
