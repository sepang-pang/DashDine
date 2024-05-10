package jpabook.dashdine.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String loginId, String refreshToken) {
        redisTemplate.opsForValue().set(loginId, refreshToken);
    }

    public void saveLoginIdAndAuthCode(String loginId, String authCode, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set("Auth Code : " + loginId, authCode, timeout, unit);
    }

    public String getRefreshToken(String loginId) {
        return redisTemplate.opsForValue().get(loginId);
    }

    public String getStoredAuthCode(String loginId) {
        return redisTemplate.opsForValue().get("Auth Code : " + loginId);
    }

    public void deleteRefreshToken(String loginId) {
        redisTemplate.delete(loginId);
    }
}