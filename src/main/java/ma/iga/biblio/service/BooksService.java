package ma.iga.biblio.service;

import ma.iga.biblio.dto.BookDto;
import ma.iga.biblio.dto.PagedResponse;
import ma.iga.biblio.entity.Book;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BooksService {
    // Existing methods (for backward compatibility)
    List<BookDto> getAllBooks();
    BookDto getBookById(long id);
    BookDto createBook(BookDto bookDto);
    BookDto updateBook(long id, BookDto bookDto);
    void deleteBook(long id);
    List<BookDto> searchBooks(String query);
    List<BookDto> getBooksByCategory(long categoryId);
    List<BookDto> getBooksByAuthor(long authorId);
    BookDto getBookByIsbn13(String isbn13);
    BookDto getBookByIsbn10(String isbn10);
    List<BookDto> getBooksByPublishedYear(Integer publishedYear);
    List<BookDto> getBooksByAuthorName(String authorName);
    List<BookDto> getBooksByCategoryName(String categoryName);
    
    // New paginated methods
    PagedResponse<BookDto> getAllBooksPaginated(Pageable pageable);
    PagedResponse<BookDto> searchBooksPaginated(String query, Pageable pageable);
    PagedResponse<BookDto> getBooksByCategoryPaginated(long categoryId, Pageable pageable);
    PagedResponse<BookDto> getBooksByAuthorPaginated(long authorId, Pageable pageable);
    PagedResponse<BookDto> getBooksByPublishedYearPaginated(Integer publishedYear, Pageable pageable);
    PagedResponse<BookDto> getBooksByAuthorNamePaginated(String authorName, Pageable pageable);
    PagedResponse<BookDto> getBooksByCategoryNamePaginated(String categoryName, Pageable pageable);
    
    // Advanced filtered search
    PagedResponse<BookDto> searchBooksWithFilters(String title, String authorName, 
                                                 String categoryName, Integer publishedYear, 
                                                 Pageable pageable);
}
