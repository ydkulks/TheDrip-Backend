package dev.ydkulks.TheDrip.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import dev.ydkulks.TheDrip.services.MyUserDetailsService;

// NOTE: Spring security
// Spring security will create a chain of filters or functionalities by default, like:
// Session creation, CSRF token, User authentication to access APIs and so on...
// Disable the defaults and customize the filters using @Bean
@Configuration
@EnableWebSecurity
public class SecurityConfigurer {

  @Autowired
  private MyUserDetailsService userDetailsService;

  @Autowired
  private JwtFilter jwtFilter;

  // TODO: Role based endpoint access
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(customizer -> customizer.disable()) // Disable all security
        .authorizeHttpRequests(request -> request
            .requestMatchers("/api/signup","/api/login").permitAll() // No basic auth for matched APIs
            .anyRequest().authenticated()) // Enable basic auth for APIs
        .formLogin(Customizer.withDefaults()) // Enable login form in browser
        .httpBasic(Customizer.withDefaults()) // Handle basic auth
        .sessionManagement(
            session -> session.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS)) // Remove session
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}
