package backend.account.application;

import backend.account.domain.Account;
import backend.account.domain.AccountRegister;
import backend.account.domain.AccountRepository;
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
