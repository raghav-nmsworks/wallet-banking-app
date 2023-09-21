package com.nextuple.walletbankingapp.controller;

import com.nextuple.walletbankingapp.dto.response.UserDTO;
import com.nextuple.walletbankingapp.jwt.JwtHelper;
import com.nextuple.walletbankingapp.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProfileControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtHelper jwtHelper;

    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUser() {
        String token = "Bearer mockToken";
        String userEmail = "user@example.com";
        UserDTO mockUserDTO = new UserDTO("John", "Doe", userEmail, null);
        String to=token.substring(7);
        when(jwtHelper.getUsernameFromToken(to)).thenReturn(userEmail);
        when(userService.getUserDTOByEmail(userEmail)).thenReturn(mockUserDTO);

        ResponseEntity<UserDTO> responseEntity = profileController.getUser(token);

        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        UserDTO responseUserDTO = responseEntity.getBody();
        assertNotNull(responseUserDTO);
        assertEquals(mockUserDTO.getFirstName(), responseUserDTO.getFirstName());
        assertEquals(mockUserDTO.getLastName(), responseUserDTO.getLastName());
        assertEquals(mockUserDTO.getEmail(), responseUserDTO.getEmail());

        verify(jwtHelper, times(1)).getUsernameFromToken(to);
        verify(userService, times(1)).getUserDTOByEmail(userEmail);
    }

    @Test
    public void testGetUserWithInvalidToken() {
        String token = "InvalidToken";

        when(jwtHelper.getUsernameFromToken(token)).thenReturn(null);

        ResponseEntity<UserDTO> responseEntity = profileController.getUser(token);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Mockito.when(jwtHelper.getUsernameFromToken(any())).thenReturn(token);
        Mockito.when(userService.getUserDTOByEmail(token)).thenReturn(null);
        Assertions.assertEquals(ResponseEntity.notFound().build(),profileController.getUser(token));
    }
}
