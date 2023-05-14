package home.serg.security;

import home.serg.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class UserAuthentication {
    public static final String UUID_CALM_NAME = "uuid";
    private final JwtUtil jwtUtil;

    public Mono<Authentication> create(String token) {
        try {
            String calm = jwtUtil.getCalm(token, UUID_CALM_NAME);
            Principal principal = new CustomPrincipal(calm);
            System.out.println(calm);
            return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList()));
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.empty();
        }
    }
}
