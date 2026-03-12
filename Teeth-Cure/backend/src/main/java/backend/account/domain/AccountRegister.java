package backend.account.domain;

import backend.global.exception.GlobalException;
import backend.global.exception.errorcode.AccountExceptionCode;
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
