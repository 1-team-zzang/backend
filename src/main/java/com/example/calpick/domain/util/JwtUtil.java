package com.example.calpick.domain.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    // JWT 생성 및 검증
    private final Key key;
    private final long accessTokenExpireMs;
    private final long refreshTokenExpireMs;

    long now = (new Date()).getTime();

    public JwtUtil(
            @Value("${jwt.secret}") final String secretKey,
            @Value("${jwt.accessToken_expire_time}") final long accessTokenExpireMs,
            @Value("${jwt.refreshToken_expire_time}") final long refreshTokenExpireMs
    ){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpireMs = accessTokenExpireMs;
        this.refreshTokenExpireMs = refreshTokenExpireMs;
    }

    public String getName(String token){
        return parseClaims(token).get("name", String.class);
    }

    public String getEmail(String token){
        return parseClaims(token).get("email", String.class);
    }

    public Boolean isExpired(String token){
        return parseClaims(token).getExpiration().before(new Date());
    }

    public Claims parseClaims(String token){
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e){ return e.getClaims(); }
    }

    public String createAccessToken(String email, String name){
        long nowMs = System.currentTimeMillis();
        return Jwts.builder()
                .claim("email", email)
                .claim("name", name)
                .setIssuedAt(new Date(nowMs))
                .setExpiration(new Date(nowMs+accessTokenExpireMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

    }

    public String createRefreshToken(String role, String email){
        long nowMs = System.currentTimeMillis();
        return Jwts.builder()
                .claim("role", role)
                .claim("email",email)
                .setIssuedAt(new Date(nowMs))
                .setExpiration(new Date(nowMs+refreshTokenExpireMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

}
