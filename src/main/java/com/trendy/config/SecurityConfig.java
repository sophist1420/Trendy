package com.trendy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.trendy.login.CustomUserDetails;
import com.trendy.login.CustomOAuth2UserService;
import com.trendy.login.CustomUserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.trendy.security.JwtAuthenticationFilter;
import com.trendy.security.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomUserDetailsService customUserDetailsService, JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login","/payment/**","/naverpay/**", "/kakaopay/**", 
                    "/loginFailure", "check-username", "/signup", "/oauth2/**", "/mail",
                    "/product-detail/**","/product-list/**","/product/**", "/products/**").permitAll()
                .requestMatchers("/mypage", "/reviews/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form
                .loginProcessingUrl("/perform_login")
                .successHandler((request, response, authentication) -> {
                    // 로컬 로그인 성공 시
                    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                    response.sendRedirect("http://localhost:3000/loginSuccess?email=" + userDetails.getEmail());
                })
                .failureHandler((request, response, exception) -> {
                    response.sendRedirect("http://localhost:3000/loginFailure");
                })
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("http://localhost:3000/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .successHandler((request, response, authentication) -> {
                    // 소셜 로그인 성공 시
                    OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                    String email = extractEmail(oauth2User, request.getRequestURI());
                    response.sendRedirect("http://localhost:3000/loginSuccess?email=" + email);
                })
                .failureHandler((request, response, exception) -> {
                    System.err.println("OAuth2 Login failed: " + exception.getMessage());
                    response.sendRedirect("http://localhost:3000/loginFailure");
                })
            )
            .logout(logout -> logout
                .logoutUrl("/perform_logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().flush();
                })
                .permitAll()
            );

        return http.build();
    }

    private String extractEmail(OAuth2User oauth2User, String requestUri) {
        if (oauth2User.getAttribute("email") != null) {
            return oauth2User.getAttribute("email");
        } else if (oauth2User.getAttribute("response") != null) {
            Map<String, Object> response = (Map<String, Object>) oauth2User.getAttribute("response");
            return (String) response.get("email");
        } else if (oauth2User.getAttribute("kakao_account") != null) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttribute("kakao_account");
            return (String) kakaoAccount.get("email");
        }
        return null;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}