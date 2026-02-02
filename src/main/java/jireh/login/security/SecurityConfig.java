package jireh.login.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jireh.login.security.filters.JwtAuthenticationFilter;
import jireh.login.security.filters.JwtAuthorizationFilter;
import jireh.login.security.jwt.JwtUtils;
import jireh.login.service.UserDetailsServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import jireh.login.repositories.UserRepository;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService; 

    @Autowired
    JwtAuthorizationFilter authorizationFilter;

    @Autowired
    private UserRepository userRepository;

    @Bean
    SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception{

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils, userRepository);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");

        return httpSecurity
                .cors(withDefaults()) 
                .csrf(config -> config.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/users/user/register").permitAll();
                    auth.requestMatchers("/login").permitAll();
                    auth.requestMatchers("/users/user/check-email").permitAll();
                    auth.requestMatchers("/auth/forgot-password").permitAll();   
                    auth.requestMatchers("/auth/reset-password").permitAll();  
                    auth.anyRequest().authenticated();
                    // TEMPORAL: Solo para probar si es un problema de permisos de ruta
                    auth.requestMatchers("/users/user/update/**").permitAll();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(Arrays.asList(
            "https://eddc0f65-a17f-4af9-9774-b722606f3449-00-2zbw4m3gak2ba.worf.replit.dev",
            "https://jireh-frontend-chi.vercel.app/"
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager (HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception{
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder)
            .and().build();
    }
}
