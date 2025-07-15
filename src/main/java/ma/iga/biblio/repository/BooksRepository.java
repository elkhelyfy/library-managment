package ma.iga.biblio.repository;

import ma.iga.biblio.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Book, Long>, PagingAndSortingRepository<Book, Long> {
    
    // Paginated version of findAll
    Page<Book> findAll(Pageable pageable);
    
    // Search books by title, subtitle, or ISBN
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.subtitle) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "b.isbn13 LIKE CONCAT('%', :query, '%') OR " +
           "b.isbn10 LIKE CONCAT('%', :query, '%')")
    List<Book> searchBooks(@Param("query") String query);
    
    // Paginated search
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.subtitle) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "b.isbn13 LIKE CONCAT('%', :query, '%') OR " +
           "b.isbn10 LIKE CONCAT('%', :query, '%')")
    Page<Book> searchBooks(@Param("query") String query, Pageable pageable);
    
    // Find books by category
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.id = :categoryId")
    List<Book> findByCategoryId(@Param("categoryId") Long categoryId);
    
    // Paginated find by category
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.id = :categoryId")
    Page<Book> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    // Find books by author
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorId")
    List<Book> findByAuthorId(@Param("authorId") Long authorId);
    
    // Paginated find by author
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorId")
    Page<Book> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);
    
    // Find book by ISBN13
    Optional<Book> findByIsbn13(String isbn13);
    
    // Find book by ISBN10
    Optional<Book> findByIsbn10(String isbn10);
    
    // Find books by published year
    List<Book> findByPublishedYear(Integer publishedYear);
    
    // Paginated find by published year
    Page<Book> findByPublishedYear(Integer publishedYear, Pageable pageable);
    
    // Find books by title containing (case insensitive)
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    // Find books by author name
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthorNameContaining(@Param("authorName") String authorName);
    
    // Paginated find by author name
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    Page<Book> findByAuthorNameContaining(@Param("authorName") String authorName, Pageable pageable);
    
    // Find books by category name
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))")
    List<Book> findByCategoryNameContaining(@Param("categoryName") String categoryName);
    
    // Paginated find by category name
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))")
    Page<Book> findByCategoryNameContaining(@Param("categoryName") String categoryName, Pageable pageable);
    
    // Advanced search with multiple filters
    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN b.authors a " +
           "LEFT JOIN b.categories c " +
           "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:authorName IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))) " +
           "AND (:categoryName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))) " +
           "AND (:publishedYear IS NULL OR b.publishedYear = :publishedYear)")
    Page<Book> findBooksWithFilters(@Param("title") String title,
                                   @Param("authorName") String authorName,
                                   @Param("categoryName") String categoryName,
                                   @Param("publishedYear") Integer publishedYear,
                                   Pageable pageable);
}
