package digital.recipy.service;

import digital.recipy.configuration.CustomUserDetails;
import digital.recipy.configuration.JwtUtils;
import digital.recipy.model.User;
import digital.recipy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;

    public String authenticate(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
            Long userId = ((CustomUserDetails) userDetails).getUserId();
            return jwtUtils.generateJwtToken((CustomUserDetails) userDetails);
        }
        return null;
    }

}
