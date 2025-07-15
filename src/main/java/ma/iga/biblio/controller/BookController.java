package ma.iga.biblio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.iga.biblio.dto.BookDto;
import ma.iga.biblio.dto.PagedResponse;
import ma.iga.biblio.service.BooksService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book", description = "Book management APIs")
public class BookController {

    private final BooksService booksService;

    // Public endpoints - no authentication required

    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieves a list of all books in the library (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all books")
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(booksService.getAllBooks());
    }

    @GetMapping("/paginated")
    @Operation(summary = "Get books with pagination", description = "Retrieves books with pagination and sorting (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated books")
    public ResponseEntity<PagedResponse<BookDto>> getAllBooksPaginated(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(booksService.getAllBooksPaginated(pageable));
    }

    @GetMapping("/search/advanced")
    @Operation(summary = "Advanced search with filters", description = "Search books with multiple filters and pagination (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered books")
    public ResponseEntity<PagedResponse<BookDto>> searchBooksWithFilters(
            @Parameter(description = "Title filter") @RequestParam(required = false) String title,
            @Parameter(description = "Author name filter") @RequestParam(required = false) String authorName,
            @Parameter(description = "Category name filter") @RequestParam(required = false) String categoryName,
            @Parameter(description = "Published year filter") @RequestParam(required = false) Integer publishedYear,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(booksService.searchBooksWithFilters(title, authorName, categoryName, publishedYear, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieves a specific book by its ID (Public access)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the book"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookDto> getBookById(
            @Parameter(description = "ID of the book to retrieve") @PathVariable long id) {
        return ResponseEntity.ok(booksService.getBookById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Searches books by title, subtitle, or ISBN (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching books")
    public ResponseEntity<List<BookDto>> searchBooks(
            @Parameter(description = "Search query (title, subtitle, or ISBN)") @RequestParam String query) {
        return ResponseEntity.ok(booksService.searchBooks(query));
    }

    @GetMapping("/search/paginated")
    @Operation(summary = "Search books with pagination", description = "Searches books by title, subtitle, or ISBN with pagination (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated search results")
    public ResponseEntity<PagedResponse<BookDto>> searchBooksPaginated(
            @Parameter(description = "Search query (title, subtitle, or ISBN)") @RequestParam String query,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(booksService.searchBooksPaginated(query, pageable));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get books by category", description = "Retrieves all books in a specific category (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved books in category")
    public ResponseEntity<List<BookDto>> getBooksByCategory(
            @Parameter(description = "ID of the category") @PathVariable long categoryId) {
        return ResponseEntity.ok(booksService.getBooksByCategory(categoryId));
    }

    @GetMapping("/category/{categoryId}/paginated")
    @Operation(summary = "Get books by category with pagination", description = "Retrieves books in a specific category with pagination (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated books in category")
    public ResponseEntity<PagedResponse<BookDto>> getBooksByCategoryPaginated(
            @Parameter(description = "ID of the category") @PathVariable long categoryId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(booksService.getBooksByCategoryPaginated(categoryId, pageable));
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get books by author", description = "Retrieves all books by a specific author (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved books by author")
    public ResponseEntity<List<BookDto>> getBooksByAuthor(
            @Parameter(description = "ID of the author") @PathVariable long authorId) {
        return ResponseEntity.ok(booksService.getBooksByAuthor(authorId));
    }

    @GetMapping("/author/{authorId}/paginated")
    @Operation(summary = "Get books by author with pagination", description = "Retrieves books by a specific author with pagination (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated books by author")
    public ResponseEntity<PagedResponse<BookDto>> getBooksByAuthorPaginated(
            @Parameter(description = "ID of the author") @PathVariable long authorId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(booksService.getBooksByAuthorPaginated(authorId, pageable));
    }

    @GetMapping("/isbn13/{isbn13}")
    @Operation(summary = "Get book by ISBN-13", description = "Retrieves a book by its ISBN-13 (Public access)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the book"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookDto> getBookByIsbn13(
            @Parameter(description = "ISBN-13 of the book to retrieve") @PathVariable String isbn13) {
        return ResponseEntity.ok(booksService.getBookByIsbn13(isbn13));
    }

    @GetMapping("/isbn10/{isbn10}")
    @Operation(summary = "Get book by ISBN-10", description = "Retrieves a book by its ISBN-10 (Public access)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the book"),
        @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookDto> getBookByIsbn10(
            @Parameter(description = "ISBN-10 of the book to retrieve") @PathVariable String isbn10) {
        return ResponseEntity.ok(booksService.getBookByIsbn10(isbn10));
    }

    @GetMapping("/year/{year}")
    @Operation(summary = "Get books by published year", description = "Retrieves all books published in a specific year (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved books by year")
    public ResponseEntity<List<BookDto>> getBooksByPublishedYear(
            @Parameter(description = "Published year") @PathVariable Integer year) {
        return ResponseEntity.ok(booksService.getBooksByPublishedYear(year));
    }

    @GetMapping("/year/{year}/paginated")
    @Operation(summary = "Get books by published year with pagination", description = "Retrieves books published in a specific year with pagination (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated books by year")
    public ResponseEntity<PagedResponse<BookDto>> getBooksByPublishedYearPaginated(
            @Parameter(description = "Published year") @PathVariable Integer year,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(booksService.getBooksByPublishedYearPaginated(year, pageable));
    }

    @GetMapping("/author/name/{name}")
    @Operation(summary = "Get books by author name", description = "Retrieves all books by author name (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved books by author name")
    public ResponseEntity<List<BookDto>> getBooksByAuthorName(
            @Parameter(description = "Author name") @PathVariable String name) {
        return ResponseEntity.ok(booksService.getBooksByAuthorName(name));
    }

    @GetMapping("/author/name/{name}/paginated")
    @Operation(summary = "Get books by author name with pagination", description = "Retrieves books by author name with pagination (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated books by author name")
    public ResponseEntity<PagedResponse<BookDto>> getBooksByAuthorNamePaginated(
            @Parameter(description = "Author name") @PathVariable String name,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(booksService.getBooksByAuthorNamePaginated(name, pageable));
    }

    @GetMapping("/category/name/{name}")
    @Operation(summary = "Get books by category name", description = "Retrieves all books by category name (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved books by category name")
    public ResponseEntity<List<BookDto>> getBooksByCategoryName(
            @Parameter(description = "Category name") @PathVariable String name) {
        return ResponseEntity.ok(booksService.getBooksByCategoryName(name));
    }

    @GetMapping("/category/name/{name}/paginated")
    @Operation(summary = "Get books by category name with pagination", description = "Retrieves books by category name with pagination (Public access)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated books by category name")
    public ResponseEntity<PagedResponse<BookDto>> getBooksByCategoryNamePaginated(
            @Parameter(description = "Category name") @PathVariable String name,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(booksService.getBooksByCategoryNamePaginated(name, pageable));
    }

    // Protected endpoints - require ADMIN or LIBRARIAN roles

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @Operation(summary = "Create a new book", description = "Creates a new book in the library (Admin/Librarian only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Book successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin or Librarian role required")
    })
    public ResponseEntity<BookDto> createBook(
            @Parameter(description = "Book data to create") @Valid @RequestBody BookDto bookDto) {
        return new ResponseEntity<>(booksService.createBook(bookDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @Operation(summary = "Update a book", description = "Updates an existing book's information (Admin/Librarian only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book successfully updated"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin or Librarian role required")
    })
    public ResponseEntity<BookDto> updateBook(
            @Parameter(description = "ID of the book to update") @PathVariable long id,
            @Parameter(description = "Updated book data") @Valid @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(booksService.updateBook(id, bookDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @Operation(summary = "Delete a book", description = "Deletes a book from the library (Admin/Librarian only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Book successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin or Librarian role required")
    })
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID of the book to delete") @PathVariable long id) {
        booksService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
