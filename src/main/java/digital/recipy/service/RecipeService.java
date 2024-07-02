package digital.recipy.service;

import digital.recipy.model.Like;
import digital.recipy.model.Recipe;
import digital.recipy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {

    List<Recipe> getAllRecipes();

    Recipe getRecipeById(Long id);

    Recipe addRecipe(Recipe recipe);

    List<Recipe> addRecipes(List<Recipe> recipes);
    List<Recipe> findByUserId(Long userId);
    Like likeRecipe(Long recipeId, User user);
    void unlikeRecipe(Long recipeId, User user);
    boolean isRecipeLikedByUser(Long recipeId, Long userId);
    List<Recipe> findLikedRecipesByUserId(Long userId);
    public Page<Recipe> searchByName(String name, Pageable pageable);
    public Page<Recipe> searchByDescription(String description, Pageable pageable);
    public Page<Recipe> searchByIngredients(String ingredients, Pageable pageable);
    public Page<Recipe> getAllRecipes(Pageable pageable);
    public void updateRecipeRating(Recipe recipe, int newRating);

    public void deleteRecipe(Long id);
    public Recipe saveRecipe(Recipe recipe);
    public List<Recipe> getRecipesWithHighAverageRating(int page, int size);
    public List<Recipe> getRecipesWithLowAverageRating(int page, int size);
    public Long countRecipesByUserId(Long userId);
    public Double calculateAverageRatingByUserId(Long userId);
}
