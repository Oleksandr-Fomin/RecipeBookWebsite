package digital.recipy.repository;

import digital.recipy.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByUserId(Long userId);
    Optional<Like> findByUserIdAndRecipeId(Long userId, Long recipeId);
    Optional<Like> findByRecipeIdAndUserId(Long recipeId, Long userId);

}

