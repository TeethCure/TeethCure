package backend.account.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountSignupRequest(

        @NotBlank(message = "아이디는 필수 입력값입니다.")
        @Size(min = 4, max = 15, message = "아이디는 4자에서 15자 사이여야 합니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9]+$",
                message = "아이디는 영문자 또는 숫자만 사용할 수 있습니다."
        )
        String userId,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])\\S+$",
                message = "비밀번호는 영문자(대소문자 무관), 숫자, 특수문자를 모두 포함해야 합니다."
        )
        String password

) {

}