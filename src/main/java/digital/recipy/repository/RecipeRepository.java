package digital.recipy.repository;

import digital.recipy.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByUserId(Long userId);
    Page<Recipe> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Recipe> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    Page<Recipe> findByIngredientsContainingIgnoreCase(String ingredients, Pageable pageable);
    @Query("SELECT r FROM Recipe r WHERE r.averageRating > 5")
    Page<Recipe> findRecipesWithHighAverageRating(Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.averageRating < 5")
    Page<Recipe> findRecipesWithLowAverageRating(Pageable pageable);

    @Query("SELECT COUNT(r) FROM Recipe r WHERE r.user.id = :userId")
    long countRecipesByUserId(@Param("userId") Long userId);
    @Query("SELECT AVG(r.averageRating) FROM Recipe r WHERE r.user.id = :userId")
    Double averageRatingByUserId(@Param("userId") Long userId);



}
