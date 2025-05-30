package project.global.verification.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;


@RedisHash(value = "Email", timeToLive = 600) //Redis 저장 엔티티, 제한시간 10분
@AllArgsConstructor
@Getter
@Setter
public class EmailVerification implements Serializable {
    @Id
    private String email;
    private String verificationCode;
}

