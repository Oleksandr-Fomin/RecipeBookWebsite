package digital.recipy.service;

import digital.recipy.DTO.userUpdateDTO;
import digital.recipy.model.Role;
import digital.recipy.model.User;
import digital.recipy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.HOME_COOK);
        userRepository.save(user);
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public boolean existsByUsername(String username) {
        User foundUser = userRepository.findByUsername(username);
        return foundUser != null;
    }

    public User updateUser(String currentUsername, userUpdateDTO userUpdateDTO) {
        User user = userRepository.findByUsername(currentUsername);

        if (user != null) {
            if (userUpdateDTO.getUsername() != null) {
                user.setUsername(userUpdateDTO.getUsername());
            }
            if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
            }

            return userRepository.save(user);
        }
        return null;
    }
}
