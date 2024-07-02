package service;

import digital.recipy.configuration.CustomUserDetails;
import digital.recipy.configuration.JwtUtils;
import digital.recipy.repository.UserRepository;
import digital.recipy.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void authenticateShouldReturnJwtWhenCredentialsAreValid() {
        String username = "user";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String expectedToken = "token";

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn(encodedPassword);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn(expectedToken);

        String resultToken = authenticationService.authenticate(username, password);


        assertEquals(expectedToken, resultToken);
    }

    @Test
    void authenticateShouldReturnNullWhenUserNotFound() {

        String username = "nonExistentUser";
        when(userDetailsService.loadUserByUsername(username)).thenReturn(null);


        String resultToken = authenticationService.authenticate(username, "password");


        assertNull(resultToken);
    }

    @Test
    void authenticateShouldReturnNullWhenPasswordDoesNotMatch() {

        String username = "user";
        String password = "incorrectPassword";
        String encodedPassword = "encodedPassword";

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn(encodedPassword);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);


        String resultToken = authenticationService.authenticate(username, password);

        assertNull(resultToken);
    }
}
