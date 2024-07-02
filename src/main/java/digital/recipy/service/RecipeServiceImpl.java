package digital.recipy.service;


import digital.recipy.controller.NotificationController;
import digital.recipy.model.Like;
import digital.recipy.model.LikeNotification;
import digital.recipy.model.Recipe;
import digital.recipy.model.User;
import digital.recipy.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import digital.recipy.repository.RecipeRepository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private LikeRepository likeRepository;



    @Autowired
    private NotificationService notificationService;


    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public Recipe getRecipeById(Long id) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(id);
        return recipeOptional.orElse(null);
    }

    @Override
    public Recipe addRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @Override
    public List<Recipe> addRecipes(List<Recipe> recipes) {
        recipeRepository.saveAll(recipes);
        return recipes;
    }
    @Override
    public List<Recipe> findByUserId(Long userId) {
        return recipeRepository.findByUserId(userId);
    }

    @Override
    public Like likeRecipe(Long recipeId, User user) {
        Optional<Like> existingLike = likeRepository.findByUserIdAndRecipeId(user.getId(), recipeId);
        if (existingLike.isPresent()) {
            return existingLike.get();
        }

        Like like = new Like();
        like.setUser(user);
        Recipe recipe = getRecipeById(recipeId);
        like.setRecipe(recipe);

        Like savedLike = likeRepository.save(like);


        return savedLike;
    }

    @Override
    public void unlikeRecipe(Long recipeId, User user) {
        Optional<Like> like = likeRepository.findByUserIdAndRecipeId(user.getId(), recipeId);

        like.ifPresent(likeRepository::delete);
    }

    @Override
    public boolean isRecipeLikedByUser(Long recipeId, Long userId) {
        return likeRepository.findByRecipeIdAndUserId(recipeId, userId).isPresent();
    }

    @Override
    public List<Recipe> findLikedRecipesByUserId(Long userId) {
        List<Like> likes = likeRepository.findByUserId(userId);

        List<Long> recipeIds = likes.stream()
                .map(Like::getRecipe)
                .map(Recipe::getId)
                .collect(Collectors.toList());

        return recipeRepository.findAllById(recipeIds);
    }



    public Page<Recipe> searchByName(String name, Pageable pageable) {
        return recipeRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<Recipe> searchByDescription(String description, Pageable pageable) {
        return recipeRepository.findByDescriptionContainingIgnoreCase(description, pageable);
    }

    public Page<Recipe> searchByIngredients(String ingredients, Pageable pageable) {
        return recipeRepository.findByIngredientsContainingIgnoreCase(ingredients, pageable);
    }

    public Page<Recipe> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    public void updateRecipeRating(Recipe recipe, int newRating) {
        double oldAverage = recipe.getAverageRating() != null ? recipe.getAverageRating() : 0.0;
        int oldCount = recipe.getRatingCount();

        double newAverage = (oldAverage * oldCount + newRating) / (oldCount + 1);

        recipe.setAverageRating(newAverage);
        recipe.setRatingCount(oldCount + 1);

        recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    public Recipe saveRecipe(Recipe recipe) {
        // You can include any business logic here before saving the recipe
        return recipeRepository.save(recipe);
    }

    public List<Recipe> getRecipesWithHighAverageRating(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recipeRepository.findRecipesWithHighAverageRating(pageable).getContent();
    }

    public List<Recipe> getRecipesWithLowAverageRating(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recipeRepository.findRecipesWithLowAverageRating(pageable).getContent();
    }
    public Long countRecipesByUserId(Long userId) {
        return recipeRepository.countRecipesByUserId(userId);
    }

    public Double calculateAverageRatingByUserId(Long userId) {
        return recipeRepository.averageRatingByUserId(userId);
    }
}
