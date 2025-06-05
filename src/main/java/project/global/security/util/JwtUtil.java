package project.global.security.util;

import project.domain.member.enums.Role;
import project.global.security.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtUtil {

    private SecretKey secretKey;

    @Value("${jwt.access-token-validity-in-seconds}")
    private Long ACCESS_TOKEN_VALIDITY_IN_SECONDS;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long REFRESH_TOKEN_VALIDITY_IN_SECONDS;


    private static final String HASH_ALGORITHM = Jwts.SIG.HS256.key().build().getAlgorithm();
    private static final String PAYLOAD_MEMBER_ID_KEY = "memberId";
    private static final String PAYLOAD_EMAIL_KEY = "email";
    private static final String PAYLOAD_ROLE_TYPE = "roleType";


    public JwtUtil(@Value("${jwt.secret}") String secret, CustomUserDetailsService customUserDetailsService) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HASH_ALGORITHM);
    }

    public String createJwt(Long memberId, String email, Boolean isAccess, Role roleType) {
        final LocalDateTime now = LocalDateTime.now();
        final Date issuedDate = localDateTimeToDate(now);
        final Date expiredDate;
        if (isAccess) {
            expiredDate = localDateTimeToDate(now.plusSeconds(ACCESS_TOKEN_VALIDITY_IN_SECONDS));
        }else{
            expiredDate = localDateTimeToDate(now.plusSeconds(REFRESH_TOKEN_VALIDITY_IN_SECONDS));
        }

        return Jwts.builder()
                .claim(PAYLOAD_MEMBER_ID_KEY, memberId.toString())
                .claim(PAYLOAD_EMAIL_KEY, email)
                .claim(PAYLOAD_ROLE_TYPE, roleType.toString())
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(secretKey)
                .compact();
    }

    public Claims getPayload(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Token", e);
        }
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .get(PAYLOAD_EMAIL_KEY, String.class);
    }

    public String getRoleType(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .get(PAYLOAD_ROLE_TYPE, String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration().before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Date localDateTimeToDate(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    // 요청값에서 header또는 cookie값을 확인해서 token 값 추출
    public String getJwtFromRequest(HttpServletRequest request) {
        // 헤더에서 JWT 추출
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken;
        }

        // 쿠키에서 JWT 추출
        String cookieToken = getCookieValue(request, "Authorization");
        if (StringUtils.hasText(cookieToken)) {
            return cookieToken;
        }

        return null;
    }

    // 쿠키에서 값 추출하는 메소드
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return "Bearer "+cookie.getValue();
                }
            }
        }
        return null;
    }

    public Long getMemberId(String token) {
        String memberId = Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token)
            .getPayload()
            .get(PAYLOAD_MEMBER_ID_KEY, String.class);

        return Long.valueOf(memberId);
    }
}
