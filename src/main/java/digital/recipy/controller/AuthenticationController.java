package digital.recipy.controller;

import digital.recipy.model.LoginRequest;
import digital.recipy.model.User;
import digital.recipy.service.AuthenticationService;
import digital.recipy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String jwtToken = authenticationService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if (jwtToken != null) {
            return ResponseEntity.ok().body(jwtToken);
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully!");
    }
}
