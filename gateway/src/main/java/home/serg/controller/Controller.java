package home.serg.controller;

import home.serg.dto.AuthResponseDto;
import home.serg.dto.TokenDto;
import home.serg.exception.AuthException;
import home.serg.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
public class Controller {
    private final WebClient.Builder webClientBuilder;

    @PostMapping("/login")
    public Mono<TokenDto> login(@RequestBody String requestDto) {
        return webClientBuilder.build().post().uri("http://service/api/v1/login").contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .exchangeToMono(this::handle);
    }

    private Mono<TokenDto> handle(ClientResponse clientResponse) {
        if (clientResponse.statusCode().equals(HttpStatusCode.valueOf(200))) {
            return clientResponse.bodyToMono(AuthResponseDto.class)
                    .flatMap(authResponseDto -> Mono.just(new TokenDto(authResponseDto.getUuid())));
        } else if (clientResponse.statusCode().equals(HttpStatusCode.valueOf(404))) {
            return Mono.error(new NotFoundException());
        } else if (clientResponse.statusCode().equals(HttpStatusCode.valueOf(403))) {
            return Mono.error(new AuthException());
        }
        return Mono.error(new RuntimeException());
    }
}
