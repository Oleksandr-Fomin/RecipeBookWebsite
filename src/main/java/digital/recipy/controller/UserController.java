package digital.recipy.controller;

import digital.recipy.DTO.userUpdateDTO;
import digital.recipy.model.Recipe;
import digital.recipy.model.User;
import digital.recipy.repository.UserRepository;
import digital.recipy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("User is not authenticated or is null.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user == null) {
            System.out.println("Authenticated user not found in database.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("role", user.getRole().toString());

        return ResponseEntity.ok(userInfo);
    }


    @PutMapping("/update")
    public ResponseEntity<User> updateUserProfile(@RequestBody userUpdateDTO userUpdateDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        User updatedUser = userService.updateUser(currentUsername, userUpdateDTO);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
