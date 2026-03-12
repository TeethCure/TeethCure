package com.example.backend.account.application;

import com.example.backend.account.domain.Account;
import com.example.backend.account.domain.AccountRegister;
import com.example.backend.account.domain.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRegister accountRegister;
    private final AccountRepository accountRepository;

    public Long registerAccount(String userId, String plainPassword) {
        Account account = accountRegister.registerAccount(userId, plainPassword);
        return account.getId();
    }

    public Long login(String userId, String plainPassword) {
        Account account = accountRepository.getByUserId(userId);
        account.login(plainPassword);
        return account.getId();
    }
}
