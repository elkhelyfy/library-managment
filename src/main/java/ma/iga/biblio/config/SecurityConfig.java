package ma.iga.biblio.config;

import lombok.RequiredArgsConstructor;
import ma.iga.biblio.security.JwtAuthenticationFilter;
import ma.iga.biblio.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins - adjust these for your Angular app
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:4200",      // Angular dev server default
            "http://localhost:3000",      // React dev server (if needed)
            "http://127.0.0.1:4200",     // Alternative localhost
            "http://localhost:8080",      // Same origin (for testing)
            "http://localhost:*",         // Any localhost port (for development)
            "https://your-domain.com"     // Replace with your production domain
        ));
        
        // Allow all standard HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Allow all standard headers plus Authorization
        configuration.setAllowedHeaders(Arrays.asList(
            "Origin", "Content-Type", "Accept", "Authorization", 
            "Access-Control-Request-Method", "Access-Control-Request-Headers",
            "X-Requested-With", "Cache-Control"
        ));
        
        // Allow credentials (for cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        // Expose headers that the frontend can access
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Accept", "X-Total-Count"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // Public book browsing endpoints - read-only access (including ALL paginated endpoints)
                        .requestMatchers(HttpMethod.GET, "/api/books").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/paginated").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/search/paginated").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/search/advanced").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/category/{categoryId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/category/{categoryId}/paginated").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/author/{authorId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/author/{authorId}/paginated").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/isbn13/{isbn13}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/isbn10/{isbn10}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/year/{year}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/year/{year}/paginated").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/author/name/{name}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/author/name/{name}/paginated").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/category/name/{name}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/category/name/{name}/paginated").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/{id}/availability").permitAll()

                        // Public category and author endpoints - read-only access
                        .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/authors").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/authors/**").permitAll()
                        
                        // Book management endpoints - Admin and Librarian only
                        .requestMatchers(HttpMethod.POST, "/api/books").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/{id}").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/{id}").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.PATCH, "/api/books/{id}/availability").hasAnyRole("ADMIN", "LIBRARIAN")
                        
                        // User management endpoints - Admin only
                        .requestMatchers("/api/users/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}/block").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}/unblock").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}/role").hasRole("ADMIN")
                        
                        // Loan management endpoints - Admin and Librarian
                        .requestMatchers("/api/loans/admin/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.POST, "/api/loans/{id}/approve").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.POST, "/api/loans/{id}/reject").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.POST, "/api/loans/{id}/return").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/loans/overdue").hasAnyRole("ADMIN", "LIBRARIAN")
                        
                        // Reservation management endpoints - Admin and Librarian
                        .requestMatchers("/api/reservations/admin/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.POST, "/api/reservations/{id}/approve").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.POST, "/api/reservations/{id}/reject").hasAnyRole("ADMIN", "LIBRARIAN")
                        
                        // Fine management endpoints - Admin and Librarian
                        .requestMatchers("/api/fines/admin/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.POST, "/api/fines/{id}/waive").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/api/fines/configuration").hasRole("ADMIN")
                        
                        // User personal endpoints - authenticated users only
                        .requestMatchers(HttpMethod.GET, "/api/user/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/user/profile").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/user/change-password").authenticated()
                        
                        // User borrowing/reservation endpoints - authenticated users only
                        .requestMatchers(HttpMethod.POST, "/api/user/loans").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/user/loans").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/user/reservations").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/user/reservations").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/user/reservations/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/user/fines").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/user/fines/{id}/payment").authenticated()
                        
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
} 