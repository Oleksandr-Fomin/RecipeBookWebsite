package digital.recipy.controller;

import digital.recipy.model.User;
import digital.recipy.repository.UserRepository;
import digital.recipy.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeService recipeService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{userId}/recipe-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserRecipeCount(@PathVariable Long userId) {
        long recipeCount = recipeService.countRecipesByUserId(userId);
        return ResponseEntity.ok(recipeCount);
    }

    @GetMapping("/{userId}/average-rating")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserAverageRating(@PathVariable Long userId) {
        Double averageRating = recipeService.calculateAverageRatingByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", averageRating != null ? averageRating : "N/A");

        return ResponseEntity.ok(response);
    }

}
