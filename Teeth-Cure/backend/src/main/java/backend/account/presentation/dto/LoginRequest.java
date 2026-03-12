package backend.account.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "아이디는 필수 입력값입니다.")
        String userId,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password
) {

}
