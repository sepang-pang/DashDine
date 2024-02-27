package jpabook.dashdine.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveTokens(String loginId, String accessToken, String refreshToken) {
        String key = "authTokens :" + loginId;
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        redisTemplate.opsForHash().putAll(key, tokens);
    }

    public Map<Object, Object> getTokens(String loginId) {
        String key = "authTokens :" + loginId;
        return redisTemplate.opsForHash().entries(key);
    }

    public void deleteRefreshToken(String accessToken) {
        redisTemplate.delete(accessToken);
    }

    public String getAccessToken(String loginId) {
        Map<Object, Object> tokens = getTokens(loginId);
        if (tokens != null && tokens.containsKey("accessToken")) {
            return (String) tokens.get("accessToken");
        }
        return null;
    }
}