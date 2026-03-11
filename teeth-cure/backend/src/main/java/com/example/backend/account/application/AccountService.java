package com.example.backend.account.application;

import com.example.backend.account.domain.Account;
import com.example.backend.account.domain.AccountRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRegister accountRegister;

    public Long registerAccount(String userId, String plainPassword) {
        Account account = accountRegister.registerAccount(userId, plainPassword);
        return account.getId();
    }
}
