package com.pixapp.pixapp.config;

import com.pixapp.pixapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/register", "/login", "/h2-console/**",
                                "/avatars/**", "/users/{username}", "/create-palette", "/palettes/create",
                                "/palettes/user/{username}", "/palettes/{id}/likes", "/palettes/user/{username}/likes",
                                "palettes/public", "/palettes/user/{username}/public", "/palettes/user/{username}/likes/public",
                                "arts/create", "/arts/save", "/arts/user/{username}", "/arts/user/{username}/public",
                                "palettes/{id}/likes/users", "arts/public").permitAll()
                        .requestMatchers("/users/edit/{username}", "/users/update-user-details",
                                "/palettes/{id}/like").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            String username = authentication.getName();
                            response.getWriter().write("{\"message\": \"Login successful\", \"username\": \"" + username + "\"}");
                            response.setContentType("application/json");
                            response.setStatus(200);
                        })
                        .failureHandler(customAuthenticationFailureHandler())
                        .permitAll()
                )
                .headers(headers -> headers.frameOptions().sameOrigin())
                .userDetailsService(userService);

        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid username or password\"}");
        };
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("None");
        serializer.setUseHttpOnlyCookie(true);
        serializer.setCookiePath("/");
        return serializer;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService();
    }
}
