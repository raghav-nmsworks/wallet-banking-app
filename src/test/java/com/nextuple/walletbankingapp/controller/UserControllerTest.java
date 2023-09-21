package com.nextuple.walletbankingapp.controller;

import com.nextuple.walletbankingapp.dto.request.LoginRequestDTO;
import com.nextuple.walletbankingapp.dto.response.LoginResponseDTO;
import com.nextuple.walletbankingapp.dto.response.RegistrationResponseDTO;
import com.nextuple.walletbankingapp.entity.User;
import com.nextuple.walletbankingapp.jwt.JwtHelper;
import com.nextuple.walletbankingapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtHelper jwtHelper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateUser() {
        User mockUser = new User();
        RegistrationResponseDTO mockResponseDTO = new RegistrationResponseDTO("mockEmail", "mockToken");

        when(userService.createUser(mockUser)).thenReturn(mockResponseDTO);

        ResponseEntity<?> responseEntity = userController.createUser(mockUser);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Object responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody instanceof RegistrationResponseDTO);
        RegistrationResponseDTO responseDTO = (RegistrationResponseDTO) responseBody;
        assertEquals("mockEmail", responseDTO.getEmail());
        assertEquals("mockToken", responseDTO.getToken());

        verify(userService, times(1)).createUser(mockUser);
    }

    @Test
    public void testLoginUser() {
        LoginRequestDTO mockLoginRequest = new LoginRequestDTO();
        LoginResponseDTO mockResponseDTO = new LoginResponseDTO("mockEmail", "mockToken");

        when(userService.login(mockLoginRequest)).thenReturn(mockResponseDTO);

        ResponseEntity<?> responseEntity = userController.loginUser(mockLoginRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Object responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody instanceof LoginResponseDTO);
        LoginResponseDTO responseDTO = (LoginResponseDTO) responseBody;
        assertEquals("mockEmail", responseDTO.getEmail());
        assertEquals("mockToken", responseDTO.getToken());

        verify(userService, times(1)).login(mockLoginRequest);
    }

}
