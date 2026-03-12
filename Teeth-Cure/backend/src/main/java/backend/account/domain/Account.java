package backend.account.domain;

import backend.global.domain.SoftDeletedDomain;
import backend.global.exception.GlobalException;
import backend.global.exception.errorcode.AccountExceptionCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends SoftDeletedDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accounts_id")
    private Long id;

    @Column(name = "users_id", nullable = false, unique = true)
    private String userId;

    @Embedded
    private Password password;

    public Account(String userId, String password) {
        this.userId = userId;
        this.password = Password.hashPassword(password);
    }

    public void login(String plainTextPassword) {
        boolean same = this.password.match(plainTextPassword);
        if (!same) {
            throw new GlobalException(AccountExceptionCode.INVALID_USERNAME_PASSWORD);
        }
    }
}
