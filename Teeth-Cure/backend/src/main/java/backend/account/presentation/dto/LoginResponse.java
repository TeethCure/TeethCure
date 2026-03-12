package backend.account.presentation.dto;

public record LoginResponse(
        Long accountId,
        String accessToken
) {

}
