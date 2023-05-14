package home.serg.security;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

@RequiredArgsConstructor
public class CustomPrincipal implements Principal {

    private final String name;

    @Override
    public String getName() {
        return name;
    }
}
