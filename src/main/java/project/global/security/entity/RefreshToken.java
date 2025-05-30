package project.global.security.entity;


import java.io.Serializable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;


@RedisHash("refresh_token") // Redis 엔티티 이름 설정
public class RefreshToken implements Serializable {

    @Id
    @Indexed
    private String refreshToken;

    @Indexed
    private Long memberId;

    @TimeToLive
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long expiration;

    public RefreshToken(final Long memberId, final String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }

    public RefreshToken update(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
