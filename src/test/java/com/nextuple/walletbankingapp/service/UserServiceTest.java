package com.nextuple.walletbankingapp.service;

import com.nextuple.walletbankingapp.dto.request.LoginRequestDTO;
import com.nextuple.walletbankingapp.dto.response.LoginResponseDTO;
import com.nextuple.walletbankingapp.dto.response.RegistrationResponseDTO;
import com.nextuple.walletbankingapp.dto.response.UserDTO;
import com.nextuple.walletbankingapp.entity.User;
import com.nextuple.walletbankingapp.entity.Wallet;
import com.nextuple.walletbankingapp.excepiton.UserAlreadyExistsException;
import com.nextuple.walletbankingapp.excepiton.UserNotFoundException;
import com.nextuple.walletbankingapp.jwt.JwtHelper;
import com.nextuple.walletbankingapp.repository.UserRepository;
import com.nextuple.walletbankingapp.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUserByEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User resultUser = userService.getUserByEmail(email);

        assertNotNull(resultUser);
        assertEquals(email, resultUser.getEmail());

        verify(userRepository, times(2)).findByEmail(email);
    }

    @Test
    public void testGetUserByEmailWhenUserNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByEmail(email));

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testGetUserDTOByEmail() {
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail(email);
        UserDTO expectedUserDTO = new UserDTO(mockUser.getFirstName(), mockUser.getLastName(), mockUser.getEmail(), mockUser.getWallet());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserDTO resultUserDTO = userService.getUserDTOByEmail(email);

        assertNotNull(resultUserDTO);
        assertEquals(expectedUserDTO, resultUserDTO);

        verify(userRepository, times(2)).findByEmail(email);
    }

    @Test
    public void testGetUserDTOByEmailUserNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserDTOByEmail(email));
        verify(userRepository, times(1)).findByEmail(email);
    }


    @Test
    public void testCreateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(walletRepository.save(any())).thenReturn(new Wallet());
        when(userRepository.save(user)).thenReturn(user);

        RegistrationResponseDTO responseDTO = userService.createUser(user);

        assertNotNull(responseDTO);
        assertEquals(user.getEmail(), responseDTO.getEmail());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        //verify(passwordEncoder, times(1)).encode(user.getPassword());
        verify(walletRepository, times(1)).save(any());
        verify(userRepository, times(1)).save(user);
        verify(emailSenderService, times(1)).sendSimpleEmail(eq(user.getEmail()), any(), any());
    }

    @Test
    public void testCreateUserWhenEmailAlreadyExists() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(user));
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void testLogin() {
        String email = "test@example.com";
        String password = "password";
        LoginRequestDTO loginDTO = new LoginRequestDTO(email, password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtHelper.generateToken(user)).thenReturn("generatedToken");

        LoginResponseDTO responseDTO = userService.login(loginDTO);

        assertNotNull(responseDTO);
        assertEquals(email, responseDTO.getEmail());

        verify(userRepository, times(2)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, user.getPassword());
        verify(jwtHelper, times(1)).generateToken(user);
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        String email = "test@example.com";
        String password = "password";
        LoginRequestDTO loginDTO = new LoginRequestDTO(email, password);

        User user = new User();
        user.setEmail(email);
        user.setPassword("incorrectPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        assertThrows(UsernameNotFoundException.class, () -> userService.login(loginDTO));

        verify(userRepository, times(2)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, user.getPassword());
    }
}
