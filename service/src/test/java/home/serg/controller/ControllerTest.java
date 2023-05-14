package home.serg.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.serg.model.AuthRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerTest {
    public static final String LOGIN_URI = "/api/v1/login";
    public static final String PASSPORT_URI = "/api/v1/passport";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void login_success() throws Exception {
        AuthRequestDto requestDto = AuthRequestDto.builder().phoneNumber("54321").password("123").build();
        mockMvc.perform(
                post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto))).andExpect(status().isOk());

    }

    @Test
    void login_notFound() throws Exception {
        AuthRequestDto requestDto = AuthRequestDto.builder().phoneNumber("543").password("123").build();
        mockMvc.perform(
                post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto))).andExpect(status().isNotFound());


    }

    @Test
    void login_forbidden() throws Exception {
        AuthRequestDto requestDto = AuthRequestDto.builder().phoneNumber("54321").password("12").build();
        mockMvc.perform(
                post(LOGIN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto))).andExpect(status().isForbidden());

    }

    @Test
    void passport_success() throws Exception {
        mockMvc.perform(get(PASSPORT_URI).param("uuid", "6e01c2f2-08b7-4983-b1f9-4212d63f1a04"))
                .andExpect(status().isOk());

    }

    @Test
    void passport_notFound() throws Exception {
        mockMvc.perform(get(PASSPORT_URI).param("uuid", "6e01c2f2-08b7-4983-b1f9-4212d63f1a05"))
                .andExpect(status().isNotFound());

    }

    @Test
    void passport_fail() throws Exception {
        mockMvc.perform(get(PASSPORT_URI).param("uuid", "6e01c2f2-08b7-4983-df104"))
                .andExpect(status().isBadRequest());

    }
}