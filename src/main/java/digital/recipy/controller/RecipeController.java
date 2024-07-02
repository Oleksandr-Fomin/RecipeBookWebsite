package digital.recipy.controller;

import digital.recipy.model.Like;
import digital.recipy.model.Recipe;
import digital.recipy.model.User;
import digital.recipy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import digital.recipy.service.RecipeService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }


    @GetMapping("/{userId}/recipes")
    public ResponseEntity<List<Recipe>> getUserRecipes(@PathVariable Long userId) {
        List<Recipe> recipes = recipeService.findByUserId(userId);
        return ResponseEntity.ok(recipes);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/addRecipe")
    public ResponseEntity<Recipe> addRecipe(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("ingredients") String ingredients,
            @RequestParam("instructions") String instructions,
            @RequestParam("photoBase64") String photoBase64,
            Authentication authentication) {

        logger.debug("addRecipe endpoint hit");
        logger.debug("Received name: {}", name);
        logger.debug("Received photoBase64 length: {}", photoBase64.length());

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            logger.error("User not found for username: {}", authentication.getName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setDescription(description);
        recipe.setIngredients(ingredients);
        recipe.setInstructions(instructions);
        recipe.setUser(user);
        recipe.setPhotoBase64(photoBase64);

        Recipe savedRecipe = recipeService.addRecipe(recipe);
        logger.debug("Recipe saved with ID: {}", savedRecipe.getId());
        return ResponseEntity.ok(savedRecipe);
    }



    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeRecipe(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Like like = recipeService.likeRecipe(id, user);
        if (like != null) {

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlikeRecipe(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        recipeService.unlikeRecipe(id, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/check-like")
    public ResponseEntity<Boolean> checkIfUserLikedRecipe(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        boolean isLiked = recipeService.isRecipeLikedByUser(id, user.getId());
        return ResponseEntity.ok(isLiked);
    }

    @GetMapping("/liked")
    public ResponseEntity<List<Recipe>> getLikedRecipes(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Recipe> likedRecipes = recipeService.findLikedRecipesByUserId(user.getId());
        return ResponseEntity.ok(likedRecipes);
    }
    @GetMapping("/search")
    public ResponseEntity<Page<Recipe>> searchRecipesByName(
            @RequestParam String name,
            Pageable pageable) {

        Page<Recipe> recipes = recipeService.searchByName(name, pageable);
        return ResponseEntity.ok(recipes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        Recipe recipe = recipeService.getRecipeById(id);

        if (recipe != null && recipe.getUser().equals(currentUser)) {
            recipeService.deleteRecipe(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecipe(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("ingredients") String ingredients,
            @RequestParam("instructions") String instructions,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            Authentication authentication) {

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            logger.error("User not found for username: {}", authentication.getName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!recipe.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        recipe.setName(name);
        recipe.setDescription(description);
        recipe.setIngredients(ingredients);
        recipe.setInstructions(instructions);

        if (photo != null && !photo.isEmpty()) {
            try {
                String photoBase64 = Base64.getEncoder().encodeToString(photo.getBytes());
                recipe.setPhotoBase64(photoBase64);
            } catch (IOException e) {
                logger.error("Error saving recipe photo", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        Recipe updatedRecipe = recipeService.saveRecipe(recipe);
        return ResponseEntity.ok(updatedRecipe);
    }

    @GetMapping("/rating/high")
    public ResponseEntity<List<Recipe>> getHighRatedRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Recipe> highRatedRecipes = recipeService.getRecipesWithHighAverageRating(page, size);
        return ResponseEntity.ok(highRatedRecipes);
    }
    
    @GetMapping("/rating/low")
    public ResponseEntity<List<Recipe>> getLowRatedRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Recipe> lowRatedRecipes = recipeService.getRecipesWithLowAverageRating(page, size);
        return ResponseEntity.ok(lowRatedRecipes);
    }



}