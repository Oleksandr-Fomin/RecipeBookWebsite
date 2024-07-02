package service;

import digital.recipy.DTO.userUpdateDTO;
import digital.recipy.model.Role;
import digital.recipy.model.User;
import digital.recipy.repository.UserRepository;
import digital.recipy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private userUpdateDTO userUpdateDTO;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testUser", "password", Role.HOME_COOK, null);
        userUpdateDTO = new userUpdateDTO();
        userUpdateDTO.setUsername("updatedUser");
        userUpdateDTO.setPassword("updatedPassword");
    }

    @Test
    @DisplayName("testSaveUserWhenUserProvidedThenUserSaved")
    void testSaveUserWhenUserProvidedThenUserSaved() {
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        userService.saveUser(user);
        verify(userRepository).save(user);
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(Role.HOME_COOK, user.getRole());
    }

    @Test
    @DisplayName("testFindByUsernameWhenValidUsernameProvidedThenUserReturned")
    void testFindByUsernameWhenValidUsernameProvidedThenUserReturned() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        User foundUser = userService.findByUsername("testUser");
        assertNotNull(foundUser);
        assertEquals(user, foundUser);
    }

    @Test
    @DisplayName("testFindByUsernameWhenNonExistingUsernameProvidedThenNullReturned")
    void testFindByUsernameWhenNonExistingUsernameProvidedThenNullReturned() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);
        User foundUser = userService.findByUsername("nonExistingUser");
        assertNull(foundUser);
    }

    @Test
    @DisplayName("testExistsByUsernameWhenValidUsernameProvidedThenTrueReturned")
    void testExistsByUsernameWhenValidUsernameProvidedThenTrueReturned() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        assertTrue(userService.existsByUsername("testUser"));
    }

    @Test
    @DisplayName("testExistsByUsernameWhenNonExistingUsernameProvidedThenFalseReturned")
    void testExistsByUsernameWhenNonExistingUsernameProvidedThenFalseReturned() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);
        assertFalse(userService.existsByUsername("nonExistingUser"));
    }

    @Test
    @DisplayName("testUpdateUserWhenExistingUserProvidedThenUserUpdated")
    void testUpdateUserWhenExistingUserProvidedThenUserUpdated() {
        String currentUsername = "testUser";
        String newUsername = "updatedUser";
        String newPassword = "updatedPassword";

        userUpdateDTO.setUsername(newUsername);
        userUpdateDTO.setPassword(newPassword);

        when(userRepository.findByUsername(currentUsername)).thenReturn(user);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUser(currentUsername, userUpdateDTO);

        assertNotNull(updatedUser);
        assertEquals(newUsername, updatedUser.getUsername());
        assertEquals("encodedNewPassword", updatedUser.getPassword());
        verify(userRepository).findByUsername(currentUsername);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);
    }




    @Test
    @DisplayName("testUpdateUserWhenNonExistingCurrentUsernameProvidedThenNullReturned")
    void testUpdateUserWhenNonExistingCurrentUsernameProvidedThenNullReturned() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);
        User updatedUser = userService.updateUser("nonExistingUser", userUpdateDTO);
        assertNull(updatedUser);
    }
}