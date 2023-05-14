package home.serg.controller;

import home.serg.dto.AuthResponseDto;
import home.serg.dto.TokenDto;
import home.serg.exception.AuthException;
import home.serg.exception.BadRequestException;
import home.serg.exception.NotFoundException;
import home.serg.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class Controller {
    private final WebClient.Builder webClientBuilder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Mono<TokenDto> login(@RequestBody String requestDto) {
        return webClientBuilder.build()
                .post()
                .uri("http://service/api/v1/login").contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .exchangeToMono(this::handle);
    }

    @GetMapping("passport")
    public Mono<String> passport(Authentication authentication) {
        Principal principal = (Principal) authentication.getPrincipal();
        return webClientBuilder.baseUrl("http://service/").build()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/passport")
                        .queryParam("uuid", principal.getName()).build())
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class));
    }

    private Mono<TokenDto> handle(ClientResponse clientResponse) {
        if (clientResponse.statusCode().equals(HttpStatusCode.valueOf(200))) {
            return clientResponse.bodyToMono(AuthResponseDto.class)
                    .flatMap(authResponseDto -> {
                        Map<String, Object> claims = new HashMap<>() {{
                            put("uuid", authResponseDto.getUuid());
                        }};
                        return Mono.just(new TokenDto(jwtUtil.getToken(claims, authResponseDto.getUuid())));
                    });
        } else if (clientResponse.statusCode().equals(HttpStatusCode.valueOf(404))) {
            return Mono.error(NotFoundException::new);
        } else if (clientResponse.statusCode().equals(HttpStatusCode.valueOf(403))) {
            return Mono.error(AuthException::new);
        } else if (clientResponse.statusCode().equals(HttpStatusCode.valueOf(400))) {
            return Mono.error(BadRequestException::new);
        }
        return Mono.error(new RuntimeException());
    }
}
