package digital.recipy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 1000)
    private String ingredients;

    @Column(length = 1000)
    private String instructions;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String photoBase64;

    @OneToMany(mappedBy = "recipe")
    @JsonManagedReference
    private Set<Comment> comments;

    @Column(nullable = true)
    private Double averageRating = 0.0;

    @Column(nullable = true)
    private Integer ratingCount = 0;


}
