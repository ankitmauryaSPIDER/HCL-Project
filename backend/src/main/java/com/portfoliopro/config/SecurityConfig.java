package com.portfoliopro.config;
import com.portfoliopro.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;
@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwt; public SecurityConfig(JwtAuthenticationFilter jwt){this.jwt=jwt;}
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c)throws Exception{return c.getAuthenticationManager();}
    @Bean SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http.csrf(csrf->csrf.disable()).cors(c->c.configurationSource(corsConfigurationSource()))
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a.requestMatchers("/api/auth/**","/error").permitAll().requestMatchers("/api/users/me").authenticated().requestMatchers("/api/users/**","/api/admin/**").hasRole("ADMIN").anyRequest().authenticated())
            .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration c=new CorsConfiguration(); c.setAllowedOrigins(List.of("http://localhost:4200")); c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS")); c.setAllowedHeaders(List.of("*")); c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource(); s.registerCorsConfiguration("/**",c); return s;
    }
}
