package backend.account.presentation;

import backend.account.application.AccountService;
import backend.account.presentation.dto.AccountSignupRequest;
import backend.account.presentation.dto.AccountSignupResponse;
import backend.account.presentation.dto.LoginRequest;
import backend.account.presentation.dto.LoginResponse;
import backend.auth.Token;
import backend.auth.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public ResponseEntity<AccountSignupResponse> registerAccount(
            @RequestBody @Valid AccountSignupRequest request
    ) {
        Long accountId = accountService.registerAccount(request.userId(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountSignupResponse(accountId));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request
    ) {
        Long id = accountService.login(request.userId(), request.password());
        Token token = tokenService.createToken(id);
        return ResponseEntity.ok(new LoginResponse(id, token.accessToken()));
    }
}
