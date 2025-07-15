package ma.iga.biblio.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.iga.biblio.dto.BookDto;
import ma.iga.biblio.dto.PagedResponse;
import ma.iga.biblio.entity.Book;
import ma.iga.biblio.repository.BooksRepository;
import ma.iga.biblio.service.BooksService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BooksServiceImpl implements BooksService {
    private final BooksRepository booksRepository;

    // Existing methods (for backward compatibility)
    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getAllBooks() {
        return booksRepository.findAll().stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto getBookById(long id) {
        return booksRepository.findById(id)
                .map(BookDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = BookDto.toEntity(bookDto);
        Book savedBook = booksRepository.save(book);
        return BookDto.fromEntity(savedBook);
    }

    @Override
    public BookDto updateBook(long id, BookDto bookDto) {
        Book existingBook = booksRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        
        Book updatedBook = BookDto.toEntity(bookDto);
        updatedBook.setId(id);
        updatedBook.setCreatedAt(existingBook.getCreatedAt());
        
        Book savedBook = booksRepository.save(updatedBook);
        return BookDto.fromEntity(savedBook);
    }

    @Override
    public void deleteBook(long id) {
        if (!booksRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found with id: " + id);
        }
        booksRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> searchBooks(String query) {
        return booksRepository.searchBooks(query)
                .stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getBooksByCategory(long categoryId) {
        return booksRepository.findByCategoryId(categoryId)
                .stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getBooksByAuthor(long authorId) {
        return booksRepository.findByAuthorId(authorId)
                .stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto getBookByIsbn13(String isbn13) {
        return booksRepository.findByIsbn13(isbn13)
                .map(BookDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ISBN13: " + isbn13));
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto getBookByIsbn10(String isbn10) {
        return booksRepository.findByIsbn10(isbn10)
                .map(BookDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ISBN10: " + isbn10));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getBooksByPublishedYear(Integer publishedYear) {
        return booksRepository.findByPublishedYear(publishedYear)
                .stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getBooksByAuthorName(String authorName) {
        return booksRepository.findByAuthorNameContaining(authorName)
                .stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getBooksByCategoryName(String categoryName) {
        return booksRepository.findByCategoryNameContaining(categoryName)
                .stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());
    }

    // New paginated methods
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookDto> getAllBooksPaginated(Pageable pageable) {
        Page<Book> bookPage = booksRepository.findAll(pageable);
        Page<BookDto> bookDtoPage = bookPage.map(BookDto::fromEntity);
        return PagedResponse.from(bookDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookDto> searchBooksPaginated(String query, Pageable pageable) {
        Page<Book> bookPage = booksRepository.searchBooks(query, pageable);
        Page<BookDto> bookDtoPage = bookPage.map(BookDto::fromEntity);
        return PagedResponse.from(bookDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookDto> getBooksByCategoryPaginated(long categoryId, Pageable pageable) {
        Page<Book> bookPage = booksRepository.findByCategoryId(categoryId, pageable);
        Page<BookDto> bookDtoPage = bookPage.map(BookDto::fromEntity);
        return PagedResponse.from(bookDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookDto> getBooksByAuthorPaginated(long authorId, Pageable pageable) {
        Page<Book> bookPage = booksRepository.findByAuthorId(authorId, pageable);
        Page<BookDto> bookDtoPage = bookPage.map(BookDto::fromEntity);
        return PagedResponse.from(bookDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookDto> getBooksByPublishedYearPaginated(Integer publishedYear, Pageable pageable) {
        Page<Book> bookPage = booksRepository.findByPublishedYear(publishedYear, pageable);
        Page<BookDto> bookDtoPage = bookPage.map(BookDto::fromEntity);
        return PagedResponse.from(bookDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookDto> getBooksByAuthorNamePaginated(String authorName, Pageable pageable) {
        Page<Book> bookPage = booksRepository.findByAuthorNameContaining(authorName, pageable);
        Page<BookDto> bookDtoPage = bookPage.map(BookDto::fromEntity);
        return PagedResponse.from(bookDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookDto> getBooksByCategoryNamePaginated(String categoryName, Pageable pageable) {
        Page<Book> bookPage = booksRepository.findByCategoryNameContaining(categoryName, pageable);
        Page<BookDto> bookDtoPage = bookPage.map(BookDto::fromEntity);
        return PagedResponse.from(bookDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookDto> searchBooksWithFilters(String title, String authorName, 
                                                        String categoryName, Integer publishedYear, 
                                                        Pageable pageable) {
        Page<Book> bookPage = booksRepository.findBooksWithFilters(title, authorName, categoryName, publishedYear, pageable);
        Page<BookDto> bookDtoPage = bookPage.map(BookDto::fromEntity);
        return PagedResponse.from(bookDtoPage);
    }
}
