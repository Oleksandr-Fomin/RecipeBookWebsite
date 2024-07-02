package service;

import digital.recipy.model.Like;
import digital.recipy.model.Recipe;
import digital.recipy.model.User;
import digital.recipy.repository.LikeRepository;
import digital.recipy.repository.RecipeRepository;
import digital.recipy.service.NotificationService;
import digital.recipy.service.RecipeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class RecipeServiceImplTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RecipeServiceImpl recipeService;
    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private Page<Recipe> expectedPage;

    private Recipe recipe;
    private User user;
    private Like like;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testUser", "password", null, Set.of());
        recipe = new Recipe(1L, "Test Recipe", "Description", "Ingredients", "Instructions", user, "photoBase64", Set.of(), 0.0, 0);
        like = new Like();
        like.setUser(user);
        like.setRecipe(recipe);
    }


    @Test
    void testGetAllRecipesReturnsAllRecipes() {
        List<Recipe> expectedRecipes = List.of(recipe);
        when(recipeRepository.findAll()).thenReturn(expectedRecipes);

        List<Recipe> actualRecipes = recipeService.getAllRecipes();

        assertEquals(expectedRecipes, actualRecipes);
        verify(recipeRepository).findAll();
    }

    @Test
    void testGetRecipeByIdReturnsRecipeForValidId() {
        when(recipeRepository.findById(any(Long.class))).thenReturn(Optional.of(recipe));

        Recipe actualRecipe = recipeService.getRecipeById(1L);

        assertEquals(recipe, actualRecipe);
        verify(recipeRepository).findById(1L);
    }

    @Test
    void testGetRecipeByIdReturnsNullForInvalidId() {
        when(recipeRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Recipe actualRecipe = recipeService.getRecipeById(2L);

        assertNull(actualRecipe);
        verify(recipeRepository).findById(2L);
    }

    @Test
    void testAddRecipeSavesAndReturnsRecipe() {
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        Recipe savedRecipe = recipeService.addRecipe(recipe);

        assertEquals(recipe, savedRecipe);
        verify(recipeRepository).save(recipe);
    }

    @Test
    void testAddRecipesSavesAndReturnsListOfRecipes() {
        List<Recipe> recipes = List.of(recipe);
        when(recipeRepository.saveAll(anyIterable())).thenReturn(recipes);

        List<Recipe> savedRecipes = recipeService.addRecipes(recipes);

        assertEquals(recipes, savedRecipes);
        verify(recipeRepository).saveAll(recipes);
    }

    @Test
    void testFindByUserIdReturnsRecipesForUserId() {
        List<Recipe> expectedRecipes = List.of(recipe);
        when(recipeRepository.findByUserId(any(Long.class))).thenReturn(expectedRecipes);

        List<Recipe> actualRecipes = recipeService.findByUserId(1L);

        assertEquals(expectedRecipes, actualRecipes);
        verify(recipeRepository).findByUserId(1L);
    }


    @Test
    void testLikeRecipeDoesNotSaveIfLikeExists() {
        when(likeRepository.findByUserIdAndRecipeId(any(Long.class), any(Long.class))).thenReturn(Optional.of(like));

        Like actualLike = recipeService.likeRecipe(1L, user);

        assertEquals(like, actualLike);
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void testUnlikeRecipeDeletesLike() {
        when(likeRepository.findByUserIdAndRecipeId(any(Long.class), any(Long.class))).thenReturn(Optional.of(like));

        recipeService.unlikeRecipe(1L, user);

        verify(likeRepository).delete(like);
    }

    @Test
    void testIsRecipeLikedByUserReturnsTrueWhenLiked() {
        when(likeRepository.findByRecipeIdAndUserId(any(Long.class), any(Long.class))).thenReturn(Optional.of(like));

        boolean isLiked = recipeService.isRecipeLikedByUser(1L, 1L);

        assertTrue(isLiked);
        verify(likeRepository).findByRecipeIdAndUserId(1L, 1L);
    }

    @Test
    void testFindLikedRecipesByUserIdReturnsLikedRecipes() {
        List<Like> likes = List.of(like);
        List<Recipe> expectedRecipes = List.of(recipe);
        when(likeRepository.findByUserId(any(Long.class))).thenReturn(likes);
        when(recipeRepository.findAllById(anyIterable())).thenReturn(expectedRecipes);

        List<Recipe> actualRecipes = recipeService.findLikedRecipesByUserId(1L);

        assertEquals(expectedRecipes, actualRecipes);
        verify(likeRepository).findByUserId(1L);
        verify(recipeRepository).findAllById(List.of(1L));
    }

    @Test
    void testUpdateRecipeRatingUpdatesRatingCorrectly() {
        int newRating = 5;
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        recipeService.updateRecipeRating(recipe, newRating);

        assertEquals(1, recipe.getRatingCount());
        assertEquals(newRating, recipe.getAverageRating());
        verify(recipeRepository).save(recipe);
    }

    @Test
    void testLikeRecipeWhenLikeDoesNotExist() {
        Long recipeId = 1L;
        when(likeRepository.findByUserIdAndRecipeId(any(Long.class), any(Long.class))).thenReturn(Optional.empty());
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe)); // Corrected line
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        Like actualLike = recipeService.likeRecipe(recipeId, user);

        assertNotNull(actualLike);
        assertEquals(user, actualLike.getUser());
        assertEquals(recipe, actualLike.getRecipe());
        verify(likeRepository).findByUserIdAndRecipeId(user.getId(), recipeId);
        verify(recipeRepository).findById(recipeId);
        verify(likeRepository).save(any(Like.class));
    }
    @Test
    void deleteRecipe_DeletesRecipe_WhenCalledWithValidId() {
        Long recipeId = 1L;


        recipeService.deleteRecipe(recipeId);

        verify(recipeRepository).deleteById(recipeId);
    }

    @Test
    void saveRecipe_SavesAndReturnsRecipe_WhenCalledWithValidRecipe() {
        when(recipeRepository.save(any(Recipe.class))).then(returnsFirstArg());

        Recipe savedRecipe = recipeService.saveRecipe(recipe);

        assertNotNull(savedRecipe);
        assertEquals(recipe.getName(), savedRecipe.getName());
        verify(recipeRepository).save(recipe);
    }

    @Test
    void getRecipesWithHighAverageRating_ReturnsNonEmptyList_WhenCalledWithValidPageInfo() {
        int page = 0;
        int size = 10;
        expectedPage = new PageImpl<>(List.of(recipe));
        when(recipeRepository.findRecipesWithHighAverageRating(any(Pageable.class)))
                .thenReturn(expectedPage);

        List<Recipe> recipes = recipeService.getRecipesWithHighAverageRating(page, size);

        assertFalse(recipes.isEmpty());
        assertEquals(1, recipes.size());
        verify(recipeRepository).findRecipesWithHighAverageRating(pageableCaptor.capture());
        assertEquals(page, pageableCaptor.getValue().getPageNumber());
        assertEquals(size, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void getRecipesWithLowAverageRating_ReturnsNonEmptyList_WhenCalledWithValidPageInfo() {
        int page = 0;
        int size = 10;
        expectedPage = new PageImpl<>(List.of(recipe));
        when(recipeRepository.findRecipesWithLowAverageRating(any(Pageable.class)))
                .thenReturn(expectedPage);

        List<Recipe> recipes = recipeService.getRecipesWithLowAverageRating(page, size);

        assertFalse(recipes.isEmpty());
        assertEquals(1, recipes.size());
        verify(recipeRepository).findRecipesWithLowAverageRating(pageableCaptor.capture());
        assertEquals(page, pageableCaptor.getValue().getPageNumber());
        assertEquals(size, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void countRecipesByUserId_ReturnsCorrectCount_WhenCalledWithValidUserId() {
        Long userId = 1L;
        Long expectedCount = 5L;
        when(recipeRepository.countRecipesByUserId(userId)).thenReturn(expectedCount);

        Long actualCount = recipeService.countRecipesByUserId(userId);

        assertEquals(expectedCount, actualCount);
        verify(recipeRepository).countRecipesByUserId(userId);
    }


}

