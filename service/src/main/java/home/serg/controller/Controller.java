package home.serg.controller;

import home.serg.exception.AuthException;
import home.serg.exception.NotFoundException;
import home.serg.model.AuthRequestDto;
import home.serg.model.AuthResponseDto;
import home.serg.model.PassportDto;
import home.serg.model.User;
import home.serg.repo.UserRepository;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class Controller {

    private final UserRepository userRepository;

    public static final String UUID_PATTERN = "[\\da-fA-F]{8}-[\\da-fA-F]{4}-[\\da-fA-F]{4}-[\\da-fA-F]{4}-[\\da-fA-F]{12}";

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody AuthRequestDto requestDto) {
        User user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber())
                .orElseThrow(NotFoundException::new);
        if (!user.getPassword().equals(requestDto.getPassword())) {
            throw new AuthException();
        }
        return new AuthResponseDto(user.getUuid());
    }

    @GetMapping("/passport")
    public PassportDto passport(
            @RequestParam(name = "uuid") @Pattern(regexp = UUID_PATTERN) String uuid) {
        User user = userRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(NotFoundException::new);
        return PassportDto.builder().passport(user.getPassportNumber()).build();
    }
}
