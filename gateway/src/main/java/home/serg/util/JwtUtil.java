package home.serg.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    public static final String SECRET = "secret";

    public String getToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 5000000))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public String getCalm(String token, String claimName) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .get(claimName).toString();
    }
}
