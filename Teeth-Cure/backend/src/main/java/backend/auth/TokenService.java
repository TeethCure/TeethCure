package backend.auth;

import backend.global.exception.GlobalException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class TokenService {

    private static final String ACCOUNT_ID_CLAIM = "accountId";

    private final SecretKey secretKey;
    private final long accessTokenExpirationMillis;

    public TokenService(TokenProperty tokenProperty) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(tokenProperty.secretKey()));
        this.accessTokenExpirationMillis = tokenProperty.accessTokenExpirationMillis();
    }

    public Token createToken(Long accountId) {
        String accessToken = Jwts.builder()
                .claim(ACCOUNT_ID_CLAIM, accountId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMillis))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();

        return new Token(accessToken);
    }

    public Long extractAccountId(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(ACCOUNT_ID_CLAIM, Long.class);
        } catch (ExpiredJwtException e) {
            throw new GlobalException(TokenExceptionCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new GlobalException(TokenExceptionCode.INVALID_TOKEN);
        } catch (Exception e) {
            throw new GlobalException(TokenExceptionCode.UNKNOWN_TOKEN);
        }
    }
}