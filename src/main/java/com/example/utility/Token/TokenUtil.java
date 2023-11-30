package com.example.utility.Token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.object.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtil {
    //私钥
    private static final String TOKEN_SECRET = "privateKey";
    //设置过期时间
    private static final long EXPIRE_TIME = 60 * 60 * 1000;

    //生成token
    public String generateToken(User user) {
        // 私钥和加密算法
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        // 设置头部信息
        Map<String, Object> header = new HashMap<>(2);
        header.put("Type", "Jwt");
        header.put("alg", "HS256");

        Date end = new Date(System.currentTimeMillis() + EXPIRE_TIME);

        return JWT.create()
                .withHeader(header)
                .withClaim("userId", user.getId())
                .withClaim("userName", user.getName())
                .withExpiresAt(end)
                .sign(algorithm);
    }

    public static boolean verifyToken(String token) {
        try {
            //设置签名的加密算法：HMAC256
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /**
     * @param request
     * @return 获取token
     */
    public String getToken(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            for (Cookie c : cookies) {
                if (c.getName().equals("token")) {
                    return c.getValue();
                }
            }
        }catch (Exception ex){
            return null;
        }
        return null;
    }
}

