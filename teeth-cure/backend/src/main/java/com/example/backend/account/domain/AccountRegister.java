package com.example.backend.account.domain;

import com.example.backend.global.exception.GlobalException;
import com.example.backend.global.exception.errorcode.AccountExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountRegister {

    private final AccountRepository accountRepository;

    public Account registerAccount(String userId, String plainPassword) {
        if (accountRepository.existsByUserId(userId)) {
            throw new GlobalException(AccountExceptionCode.DUPLICATED_USER_ID);
        }
        Account account = new Account(userId, plainPassword);
        return accountRepository.save(account);
    }
}
