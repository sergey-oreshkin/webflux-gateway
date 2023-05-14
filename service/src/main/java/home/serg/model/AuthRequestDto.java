package home.serg.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequestDto {
    private String phoneNumber;
    private String password;
}
