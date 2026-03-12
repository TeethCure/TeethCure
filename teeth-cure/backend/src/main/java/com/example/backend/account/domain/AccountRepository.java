package com.example.backend.account.domain;

import com.example.backend.global.exception.GlobalException;
import com.example.backend.global.exception.errorcode.AccountExceptionCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUserId(String userId);

    default Account getByUserId(String userId) {
        return findByUserId(userId).orElseThrow(
                () -> new GlobalException(AccountExceptionCode.ACCOUNT_NOT_FOUND));
    }

    Optional<Account> findByUserId(String userId);
}
