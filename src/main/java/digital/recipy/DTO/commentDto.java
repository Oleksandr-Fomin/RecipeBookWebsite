package digital.recipy.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class commentDto {
    private String text;
    private Long userId;
    private Long recipeId;
    private int rating;

}
