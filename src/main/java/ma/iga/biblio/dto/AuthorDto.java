package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ma.iga.biblio.entity.Author;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@Schema(description = "Author data transfer object")
public class AuthorDto {
    @Schema(description = "Unique identifier of the author")
    private Long id;

    @Schema(description = "Name of the author", required = true, example = "F. Scott Fitzgerald")
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Schema(description = "Biography of the author", example = "Francis Scott Key Fitzgerald was an American novelist...")
    @Size(max = 1000, message = "Biography cannot exceed 1000 characters")
    private String biography;

    @Schema(description = "URL of the author's image", example = "https://example.com/author.jpg")
    private String imageUrl;

    @Schema(description = "Birth date of the author", example = "1896-09-24")
    private LocalDate birthDate;

    @Schema(description = "Death date of the author", example = "1940-12-21")
    private LocalDate deathDate;

    @Schema(description = "Nationality of the author", example = "American")
    private String nationality;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public static AuthorDto fromEntity(Author author) {
        if (author == null) return null;
        return AuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .biography(author.getBiography())
                .imageUrl(author.getImageUrl())
                .birthDate(author.getBirthDate())
                .deathDate(author.getDeathDate())
                .nationality(author.getNationality())
                .createdAt(author.getCreatedAt())
                .updatedAt(author.getUpdatedAt())
                .build();
    }

    public static Author toEntity(AuthorDto dto) {
        if (dto == null) return null;
        Author author = new Author();
        author.setId(dto.getId());
        author.setName(dto.getName());
        author.setBiography(dto.getBiography());
        author.setImageUrl(dto.getImageUrl());
        author.setBirthDate(dto.getBirthDate());
        author.setDeathDate(dto.getDeathDate());
        author.setNationality(dto.getNationality());
        return author;
    }
} 