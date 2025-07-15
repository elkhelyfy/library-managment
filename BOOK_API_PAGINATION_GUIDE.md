# Book API Pagination & Filtering Guide

## ⚠️ Performance Recommendation

**DO NOT** use `/api/books` (without pagination) for displaying 1000 books in your Angular application. Instead, use the new paginated endpoints.

## 🚀 New Paginated Endpoints

### 1. **Basic Pagination**
```
GET /api/books/paginated?page=0&size=20&sortBy=title&sortDir=asc
```

**Response Structure:**
```json
{
  "content": [...], // Array of BookDto objects
  "page": 0,        // Current page (0-based)
  "size": 20,       // Items per page
  "totalElements": 1000, // Total number of books
  "totalPages": 50,      // Total pages
  "first": true,         // Is first page
  "last": false,         // Is last page
  "hasNext": true,       // Has next page
  "hasPrevious": false   // Has previous page
}
```

### 2. **Advanced Search with Multiple Filters**
```
GET /api/books/search/advanced?title=gatsby&authorName=fitzgerald&categoryName=fiction&publishedYear=1925&page=0&size=20
```

### 3. **Category-based Pagination**
```
GET /api/books/category/{categoryId}/paginated?page=0&size=20
GET /api/books/category/name/{categoryName}/paginated?page=0&size=20
```

### 4. **Author-based Pagination**
```
GET /api/books/author/{authorId}/paginated?page=0&size=20
GET /api/books/author/name/{authorName}/paginated?page=0&size=20
```

### 5. **Search with Pagination**
```
GET /api/books/search/paginated?query=searchTerm&page=0&size=20
```

## 🎯 Angular Frontend Recommendations

### 1. **Use Appropriate Page Sizes**
- **Recommended**: 20-50 items per page
- **For mobile**: 10-20 items per page
- **For desktop**: 20-50 items per page

### 2. **Implement Virtual Scrolling (Optional)**
For better UX with large datasets, consider Angular CDK Virtual Scrolling:
```typescript
// In your component
loadMoreBooks() {
  if (this.hasNext && !this.loading) {
    this.currentPage++;
    this.loadBooks(this.currentPage);
  }
}
```

### 3. **Client-Side Filter Strategy**
```typescript
// DON'T: Load all books then filter
loadAllBooks() {
  this.http.get('/api/books').subscribe(books => {
    this.filteredBooks = books.filter(/* filter logic */);
  });
}

// DO: Use server-side filtering
searchBooks(filters: BookFilters) {
  const params = new HttpParams()
    .set('title', filters.title || '')
    .set('authorName', filters.authorName || '')
    .set('categoryName', filters.categoryName || '')
    .set('page', '0')
    .set('size', '20');
    
  this.http.get('/api/books/search/advanced', { params })
    .subscribe(response => {
      this.books = response.content;
      this.totalElements = response.totalElements;
    });
}
```

### 4. **Implement Debounced Search**
```typescript
searchControl = new FormControl();

ngOnInit() {
  this.searchControl.valueChanges.pipe(
    debounceTime(300),
    distinctUntilChanged()
  ).subscribe(searchTerm => {
    this.searchBooks(searchTerm);
  });
}
```

### 5. **Use Angular Material Paginator**
```html
<mat-paginator 
  [length]="totalElements"
  [pageSize]="pageSize"
  [pageSizeOptions]="[10, 20, 50]"
  (page)="onPageChange($event)">
</mat-paginator>
```

```typescript
onPageChange(event: PageEvent) {
  this.currentPage = event.pageIndex;
  this.pageSize = event.pageSize;
  this.loadBooks();
}
```

## 📊 Performance Benefits

### Before (Loading All 1000 Books):
- **Payload Size**: ~5-10 MB
- **Load Time**: 3-8 seconds
- **Memory Usage**: High (all books in memory)
- **Initial Render**: Slow (1000 DOM elements)

### After (Pagination with 20 items/page):
- **Payload Size**: ~100-200 KB
- **Load Time**: 200-500ms
- **Memory Usage**: Low (only current page)
- **Initial Render**: Fast (20 DOM elements)

## 🔍 Sorting Options

Available sort fields:
- `title` (default)
- `publishedYear`
- `averageRating`
- `createdAt`
- `updatedAt`

Sort directions:
- `asc` (ascending, default)
- `desc` (descending)

## 💡 Best Practices

1. **Default Page Size**: Start with 20 items per page
2. **Show Loading States**: Display spinners during API calls
3. **Handle Empty States**: Show appropriate messages when no results
4. **Cache Strategy**: Consider caching frequently accessed pages
5. **URL State**: Sync pagination state with URL for bookmarkable results
6. **Error Handling**: Implement proper error handling for failed requests

## 🔄 Migration Strategy

1. **Phase 1**: Replace `/api/books` with `/api/books/paginated` in main listing
2. **Phase 2**: Implement category and author filtering with pagination
3. **Phase 3**: Add advanced search with multiple filters
4. **Phase 4**: Optimize with virtual scrolling if needed

## 📱 Mobile Considerations

- Use smaller page sizes (10-15 items)
- Implement infinite scroll for better mobile UX
- Consider lazy loading images
- Optimize for touch interactions

This pagination system will provide a much better user experience and significantly improve your application's performance! 