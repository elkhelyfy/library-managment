package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ma.iga.biblio.entity.Book;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@Schema(description = "Book data transfer object")
public class BookDto {

    @Schema(description = "Unique identifier of the book")
    private Long id;

    @Schema(description = "ISBN-13 of the book", example = "9780743273565")
    private String isbn13;

    @Schema(description = "ISBN-10 of the book", example = "0743273567")
    private String isbn10;

    @Schema(description = "Title of the book", required = true, example = "The Great Gatsby")
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 500, message = "Title must be between 1 and 500 characters")
    private String title;

    @Schema(description = "Subtitle of the book", example = "A Novel")
    @Size(max = 500, message = "Subtitle cannot exceed 500 characters")
    private String subtitle;

    @Schema(description = "Authors of the book")
    private Set<AuthorDto> authors;

    @Schema(description = "Categories of the book")
    private Set<CategoryDto> categories;

    @Schema(description = "Thumbnail URL of the book", example = "https://example.com/thumbnail.jpg")
    private String thumbnail;

    @Schema(description = "Description of the book", example = "A story of the fabulously wealthy Jay Gatsby...")
    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    @Schema(description = "Published year of the book", example = "1925")
    private Integer publishedYear;

    @Schema(description = "Average rating of the book", example = "4.5")
    private Double averageRating;

    @Schema(description = "Number of pages", example = "180")
    private Integer numPages;

    @Schema(description = "Number of ratings", example = "12345")
    private Integer ratingsCount;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public static BookDto fromEntity(Book book) {
        if(book == null) return null;
        return BookDto.builder()
                .id(book.getId())
                .isbn13(book.getIsbn13())
                .isbn10(book.getIsbn10())
                .title(book.getTitle())
                .subtitle(book.getSubtitle())
                .authors(book.getAuthors().stream()
                        .map(AuthorDto::fromEntity)
                        .collect(Collectors.toSet()))
                .categories(book.getCategories().stream()
                        .map(CategoryDto::fromEntity)
                        .collect(Collectors.toSet()))
                .thumbnail(book.getThumbnail())
                .description(book.getDescription())
                .publishedYear(book.getPublishedYear())
                .averageRating(book.getAverageRating())
                .numPages(book.getNumPages())
                .ratingsCount(book.getRatingsCount())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    public static Book toEntity(BookDto dto) {
        if(dto == null) return null;
        Book book = new Book();
        book.setId(dto.getId());
        book.setIsbn13(dto.getIsbn13());
        book.setIsbn10(dto.getIsbn10());
        book.setTitle(dto.getTitle());
        book.setSubtitle(dto.getSubtitle());
        book.setAuthors(dto.getAuthors().stream()
                .map(AuthorDto::toEntity)
                .collect(Collectors.toSet()));
        book.setCategories(dto.getCategories().stream()
                .map(CategoryDto::toEntity)
                .collect(Collectors.toSet()));
        book.setThumbnail(dto.getThumbnail());
        book.setDescription(dto.getDescription());
        book.setPublishedYear(dto.getPublishedYear());
        book.setAverageRating(dto.getAverageRating());
        book.setNumPages(dto.getNumPages());
        book.setRatingsCount(dto.getRatingsCount());
        return book;
    }
}
