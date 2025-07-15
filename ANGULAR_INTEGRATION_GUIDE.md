# Angular Integration Guide

## ✅ CORS Configuration Status

Your Spring Boot backend is now properly configured to work with Angular! The CORS configuration allows:

- **Origins**: `http://localhost:4200` (Angular default dev server)
- **Methods**: GET, POST, PUT, DELETE, PATCH, OPTIONS
- **Headers**: Authorization, Content-Type, Accept, etc.
- **Credentials**: Enabled (for JWT tokens)

## 🚀 Angular Setup

### 1. **Environment Configuration**

Create/update your Angular environment files:

**`src/environments/environment.ts`** (Development):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  defaultPageSize: 20
};
```

**`src/environments/environment.prod.ts`** (Production):
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-backend-domain.com/api', // Replace with your production URL
  defaultPageSize: 20
};
```

### 2. **HTTP Service for Books**

Create an Angular service to handle book API calls:

**`src/app/services/book.service.ts`**:
```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Book {
  id: number;
  title: string;
  subtitle?: string;
  isbn13?: string;
  isbn10?: string;
  authors: Author[];
  categories: Category[];
  thumbnail?: string;
  description?: string;
  publishedYear?: number;
  averageRating?: number;
  numPages?: number;
  ratingsCount?: number;
  createdAt: string;
  updatedAt: string;
}

export interface Author {
  id: number;
  name: string;
}

export interface Category {
  id: number;
  name: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface BookFilters {
  title?: string;
  authorName?: string;
  categoryName?: string;
  publishedYear?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private apiUrl = environment.apiUrl + '/books';

  constructor(private http: HttpClient) {}

  // ===== PAGINATED METHODS (RECOMMENDED) =====

  /**
   * Get books with pagination - RECOMMENDED for large datasets
   */
  getBooksPaginated(filters: BookFilters = {}): Observable<PagedResponse<Book>> {
    let params = new HttpParams()
      .set('page', (filters.page || 0).toString())
      .set('size', (filters.size || environment.defaultPageSize).toString())
      .set('sortBy', filters.sortBy || 'title')
      .set('sortDir', filters.sortDir || 'asc');

    return this.http.get<PagedResponse<Book>>(`${this.apiUrl}/paginated`, { params });
  }

  /**
   * Advanced search with multiple filters - BEST for complex filtering
   */
  searchBooksWithFilters(filters: BookFilters): Observable<PagedResponse<Book>> {
    let params = new HttpParams()
      .set('page', (filters.page || 0).toString())
      .set('size', (filters.size || environment.defaultPageSize).toString())
      .set('sortBy', filters.sortBy || 'title')
      .set('sortDir', filters.sortDir || 'asc');

    if (filters.title) params = params.set('title', filters.title);
    if (filters.authorName) params = params.set('authorName', filters.authorName);
    if (filters.categoryName) params = params.set('categoryName', filters.categoryName);
    if (filters.publishedYear) params = params.set('publishedYear', filters.publishedYear.toString());

    return this.http.get<PagedResponse<Book>>(`${this.apiUrl}/search/advanced`, { params });
  }

  /**
   * Search books by query with pagination
   */
  searchBooksPaginated(query: string, filters: BookFilters = {}): Observable<PagedResponse<Book>> {
    let params = new HttpParams()
      .set('query', query)
      .set('page', (filters.page || 0).toString())
      .set('size', (filters.size || environment.defaultPageSize).toString())
      .set('sortBy', filters.sortBy || 'title')
      .set('sortDir', filters.sortDir || 'asc');

    return this.http.get<PagedResponse<Book>>(`${this.apiUrl}/search/paginated`, { params });
  }

  /**
   * Get books by category with pagination
   */
  getBooksByCategoryPaginated(categoryId: number, filters: BookFilters = {}): Observable<PagedResponse<Book>> {
    let params = new HttpParams()
      .set('page', (filters.page || 0).toString())
      .set('size', (filters.size || environment.defaultPageSize).toString())
      .set('sortBy', filters.sortBy || 'title')
      .set('sortDir', filters.sortDir || 'asc');

    return this.http.get<PagedResponse<Book>>(`${this.apiUrl}/category/${categoryId}/paginated`, { params });
  }

  /**
   * Get books by author with pagination
   */
  getBooksByAuthorPaginated(authorId: number, filters: BookFilters = {}): Observable<PagedResponse<Book>> {
    let params = new HttpParams()
      .set('page', (filters.page || 0).toString())
      .set('size', (filters.size || environment.defaultPageSize).toString())
      .set('sortBy', filters.sortBy || 'title')
      .set('sortDir', filters.sortDir || 'asc');

    return this.http.get<PagedResponse<Book>>(`${this.apiUrl}/author/${authorId}/paginated`, { params });
  }

  // ===== SINGLE BOOK METHODS =====

  /**
   * Get single book by ID
   */
  getBookById(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.apiUrl}/${id}`);
  }

  // ===== LEGACY METHODS (USE ONLY IF NECESSARY) =====

  /**
   * ⚠️ WARNING: Don't use this for 1000+ books! Use getBooksPaginated() instead
   */
  getAllBooks(): Observable<Book[]> {
    console.warn('WARNING: getAllBooks() loads all books at once. Use getBooksPaginated() for better performance.');
    return this.http.get<Book[]>(this.apiUrl);
  }
}
```

### 3. **Component Example**

**`src/app/components/book-list/book-list.component.ts`**:
```typescript
import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { BookService, Book, PagedResponse, BookFilters } from '../../services/book.service';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit {
  books: Book[] = [];
  loading = false;
  error: string | null = null;

  // Pagination
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;
  pageSizeOptions = [10, 20, 50];

  // Search and filters
  searchControl = new FormControl();
  titleFilter = new FormControl();
  authorFilter = new FormControl();
  categoryFilter = new FormControl();
  yearFilter = new FormControl();

  // Sorting
  sortBy = 'title';
  sortDirection: 'asc' | 'desc' = 'asc';

  constructor(private bookService: BookService) {}

  ngOnInit() {
    this.loadBooks();
    this.setupSearch();
  }

  private setupSearch() {
    // Debounced search
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(() => {
      this.currentPage = 0;
      this.loadBooks();
    });

    // Filter changes
    [this.titleFilter, this.authorFilter, this.categoryFilter, this.yearFilter].forEach(control => {
      control.valueChanges.pipe(
        debounceTime(300),
        distinctUntilChanged()
      ).subscribe(() => {
        this.currentPage = 0;
        this.loadBooks();
      });
    });
  }

  loadBooks() {
    this.loading = true;
    this.error = null;

    const filters: BookFilters = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDir: this.sortDirection,
      title: this.titleFilter.value || undefined,
      authorName: this.authorFilter.value || undefined,
      categoryName: this.categoryFilter.value || undefined,
      publishedYear: this.yearFilter.value || undefined
    };

    // Use advanced search if any filters are applied
    const hasFilters = filters.title || filters.authorName || filters.categoryName || filters.publishedYear;
    const searchQuery = this.searchControl.value;

    let request;
    if (searchQuery) {
      request = this.bookService.searchBooksPaginated(searchQuery, filters);
    } else if (hasFilters) {
      request = this.bookService.searchBooksWithFilters(filters);
    } else {
      request = this.bookService.getBooksPaginated(filters);
    }

    request.subscribe({
      next: (response: PagedResponse<Book>) => {
        this.books = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading books:', error);
        this.error = 'Failed to load books. Please try again.';
        this.loading = false;
      }
    });
  }

  onPageChange(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadBooks();
  }

  onSort(field: string) {
    if (this.sortBy === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = field;
      this.sortDirection = 'asc';
    }
    this.currentPage = 0;
    this.loadBooks();
  }

  clearFilters() {
    this.searchControl.setValue('');
    this.titleFilter.setValue('');
    this.authorFilter.setValue('');
    this.categoryFilter.setValue('');
    this.yearFilter.setValue('');
    this.currentPage = 0;
    this.loadBooks();
  }
}
```

### 4. **Template Example**

**`src/app/components/book-list/book-list.component.html`**:
```html
<div class="book-list-container">
  <!-- Search and Filters -->
  <mat-card class="filters-card">
    <mat-card-header>
      <mat-card-title>Find Books</mat-card-title>
    </mat-card-header>
    <mat-card-content>
      <div class="filters-grid">
        <!-- General Search -->
        <mat-form-field appearance="outline">
          <mat-label>Search Books</mat-label>
          <input matInput [formControl]="searchControl" placeholder="Title, ISBN, or keyword...">
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>

        <!-- Specific Filters -->
        <mat-form-field appearance="outline">
          <mat-label>Title</mat-label>
          <input matInput [formControl]="titleFilter" placeholder="Filter by title...">
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Author</mat-label>
          <input matInput [formControl]="authorFilter" placeholder="Filter by author...">
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Category</mat-label>
          <input matInput [formControl]="categoryFilter" placeholder="Filter by category...">
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Year</mat-label>
          <input matInput [formControl]="yearFilter" type="number" placeholder="Published year...">
        </mat-form-field>

        <button mat-raised-button color="warn" (click)="clearFilters()">
          Clear Filters
        </button>
      </div>
    </mat-card-content>
  </mat-card>

  <!-- Loading State -->
  <div *ngIf="loading" class="loading-container">
    <mat-spinner></mat-spinner>
    <p>Loading books...</p>
  </div>

  <!-- Error State -->
  <mat-card *ngIf="error && !loading" class="error-card">
    <mat-card-content>
      <mat-icon color="warn">error</mat-icon>
      {{ error }}
    </mat-card-content>
  </mat-card>

  <!-- Books Grid -->
  <div *ngIf="!loading && !error" class="books-container">
    <div class="books-header">
      <h2>Books ({{ totalElements }} total)</h2>
      <div class="sort-controls">
        <mat-button-toggle-group>
          <mat-button-toggle value="title" (click)="onSort('title')" 
            [checked]="sortBy === 'title'">
            Title {{ sortBy === 'title' ? (sortDirection === 'asc' ? '↑' : '↓') : '' }}
          </mat-button-toggle>
          <mat-button-toggle value="publishedYear" (click)="onSort('publishedYear')"
            [checked]="sortBy === 'publishedYear'">
            Year {{ sortBy === 'publishedYear' ? (sortDirection === 'asc' ? '↑' : '↓') : '' }}
          </mat-button-toggle>
          <mat-button-toggle value="averageRating" (click)="onSort('averageRating')"
            [checked]="sortBy === 'averageRating'">
            Rating {{ sortBy === 'averageRating' ? (sortDirection === 'asc' ? '↑' : '↓') : '' }}
          </mat-button-toggle>
        </mat-button-toggle-group>
      </div>
    </div>

    <div class="books-grid">
      <mat-card *ngFor="let book of books" class="book-card">
        <img mat-card-image [src]="book.thumbnail" [alt]="book.title" 
             onerror="this.src='assets/images/book-placeholder.png'">
        <mat-card-header>
          <mat-card-title>{{ book.title }}</mat-card-title>
          <mat-card-subtitle *ngIf="book.subtitle">{{ book.subtitle }}</mat-card-subtitle>
        </mat-card-header>
        <mat-card-content>
          <p><strong>Authors:</strong> 
            <span *ngFor="let author of book.authors; let last = last">
              {{ author.name }}<span *ngIf="!last">, </span>
            </span>
          </p>
          <p><strong>Categories:</strong>
            <span *ngFor="let category of book.categories; let last = last">
              {{ category.name }}<span *ngIf="!last">, </span>
            </span>
          </p>
          <p *ngIf="book.publishedYear"><strong>Year:</strong> {{ book.publishedYear }}</p>
          <p *ngIf="book.averageRating"><strong>Rating:</strong> {{ book.averageRating }}/5</p>
        </mat-card-content>
        <mat-card-actions>
          <button mat-button color="primary" [routerLink]="['/books', book.id]">
            View Details
          </button>
        </mat-card-actions>
      </mat-card>
    </div>

    <!-- Pagination -->
    <mat-paginator 
      [length]="totalElements"
      [pageSize]="pageSize"
      [pageIndex]="currentPage"
      [pageSizeOptions]="pageSizeOptions"
      (page)="onPageChange($event)"
      showFirstLastButtons>
    </mat-paginator>
  </div>
</div>
```

### 5. **Module Setup**

Ensure these imports in your `app.module.ts`:

```typescript
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonToggleModule } from '@angular/material/button-toggle';

@NgModule({
  imports: [
    // ... other imports
    HttpClientModule,
    ReactiveFormsModule,
    MatPaginatorModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatButtonToggleModule
  ],
  // ... rest of module
})
export class AppModule { }
```

## 🔐 Authentication Integration

If you need authentication in your Angular app:

```typescript
// auth.service.ts
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl + '/auth';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { username, password });
  }

  // Store JWT token
  saveToken(token: string) {
    localStorage.setItem('authToken', token);
  }

  // Get JWT token for requests
  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  // HTTP Interceptor for adding token to requests
  // (implement this in a separate interceptor)
}
```

## ✅ Testing Your Setup

1. **Start your Spring Boot backend**: `mvn spring-boot:run`
2. **Start your Angular dev server**: `ng serve`
3. **Test the connection**:
   ```typescript
   // In your component or service
   this.bookService.getBooksPaginated({ page: 0, size: 5 }).subscribe(
     response => console.log('✅ Connection works!', response),
     error => console.error('❌ Connection failed:', error)
   );
   ```

## 🚀 Production Deployment

For production, update the CORS configuration in `SecurityConfig.java`:

```java
configuration.setAllowedOriginPatterns(Arrays.asList(
    "https://your-angular-app-domain.com",
    "https://www.your-angular-app-domain.com"
));
```

## 📊 Performance Tips

1. **Always use paginated endpoints** for lists
2. **Implement proper loading states** in your UI
3. **Use Angular CDK Virtual Scrolling** for very long lists
4. **Cache frequently accessed data** using Angular services
5. **Implement proper error handling** with user-friendly messages

Your Angular app is now ready to efficiently handle your 1000+ books with optimal performance! 🎉 