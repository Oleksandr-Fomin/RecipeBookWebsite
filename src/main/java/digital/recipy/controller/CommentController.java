package digital.recipy.controller;

import digital.recipy.DTO.commentDto;
import digital.recipy.configuration.JwtUtils;
import digital.recipy.model.Comment;
import digital.recipy.model.Recipe;
import digital.recipy.model.User;
import digital.recipy.repository.CommentRepository;
import digital.recipy.repository.RecipeRepository;
import digital.recipy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    public CommentController(UserRepository userRepository, RecipeRepository recipeRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.commentRepository = commentRepository;
    }


    @GetMapping("/{recipeId}")
    public ResponseEntity<List<Comment>> getCommentsByRecipeId(@PathVariable Long recipeId) {
        List<Comment> comments = commentRepository.findByRecipeId(recipeId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody commentDto commentDto,
                                              @RequestHeader(name="Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtUtils.getUserIdFromJwtToken(jwt);

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Recipe recipe = recipeRepository.findById(commentDto.getRecipeId()).orElseThrow(() -> new RuntimeException("Recipe not found"));

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setRating(commentDto.getRating());
        comment.setUser(user);
        comment.setRecipe(recipe);

        updateRecipeRating(recipe, commentDto.getRating());

        Comment savedComment = commentRepository.save(comment);
        Long recipeOwnerId = recipe.getUser().getId();

        String notificationJson = String.format("{\"content\": \"You have a new comment on your recipe!\"}");

        messagingTemplate.convertAndSend("/topic/notifications/" + recipeOwnerId, notificationJson);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }
    private void updateRecipeRating(Recipe recipe, int rating) {
        double totalRating = recipe.getAverageRating() * recipe.getRatingCount() + rating;
        recipe.setRatingCount(recipe.getRatingCount() + 1);
        recipe.setAverageRating(totalRating / recipe.getRatingCount());
        recipeRepository.save(recipe);
    }


}
