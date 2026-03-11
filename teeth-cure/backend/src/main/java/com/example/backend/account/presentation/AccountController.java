package com.example.backend.account.presentation;

import com.example.backend.account.application.AccountService;
import com.example.backend.account.presentation.dto.AccountSignupRequest;
import com.example.backend.account.presentation.dto.AccountSignupResponse;
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

    @PostMapping("/signup")
    public ResponseEntity<AccountSignupResponse> registerAccount(
            @RequestBody @Valid AccountSignupRequest request
    ) {
        Long accountId = accountService.registerAccount(request.userId(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountSignupResponse(accountId));
    }
}
