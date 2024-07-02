package service;

import digital.recipy.model.Recipe;
import digital.recipy.model.User;
import digital.recipy.repository.RecipeRepository;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class RecipeServiceImplTests {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private Page<Recipe> expectedPage;
    private Recipe recipe;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recipe = new Recipe(1L, "Test Recipe", "Description", "Ingredients", "Instructions", user, "photoBase64", Set.of(), 0.0, 0);
        List<Recipe> recipeList = Collections.singletonList(recipe);
        expectedPage = new PageImpl<>(recipeList);
    }


    @Test
    void searchByNameShouldReturnPageOfRecipes() {
        String name = "Test";
        when(recipeRepository.findByNameContainingIgnoreCase(eq(name), any(Pageable.class))).thenReturn(expectedPage);

        Page<Recipe> result = recipeService.searchByName(name, PageRequest.of(0, 10));

        assertEquals(expectedPage, result);
    }

    @Test
    void searchByDescriptionShouldReturnPageOfRecipes() {
        String description = "Test";
        when(recipeRepository.findByDescriptionContainingIgnoreCase(eq(description), any(Pageable.class))).thenReturn(expectedPage);

        Page<Recipe> result = recipeService.searchByDescription(description, PageRequest.of(0, 10));

        assertEquals(expectedPage, result);
    }

    @Test
    void searchByIngredientsShouldReturnPageOfRecipes() {
        String ingredients = "Test";
        when(recipeRepository.findByIngredientsContainingIgnoreCase(eq(ingredients), any(Pageable.class))).thenReturn(expectedPage);

        Page<Recipe> result = recipeService.searchByIngredients(ingredients, PageRequest.of(0, 10));

        assertEquals(expectedPage, result);
    }

    @Test
    void getAllRecipesShouldReturnPageOfRecipes() {
        when(recipeRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

        Page<Recipe> result = recipeService.getAllRecipes(PageRequest.of(0, 10));

        assertEquals(expectedPage, result);
    }
}