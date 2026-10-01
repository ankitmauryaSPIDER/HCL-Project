package com.portfoliopro.service;
import com.portfoliopro.dto.*; import com.portfoliopro.entity.*; import com.portfoliopro.repository.*; import com.portfoliopro.security.JwtService;
import org.springframework.security.authentication.*; import org.springframework.security.core.userdetails.UserDetails; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthService {
 private final UserRepository users; private final PortfolioRepository portfolios; private final PasswordEncoder encoder; private final AuthenticationManager auth; private final JwtService jwt;
 public AuthService(UserRepository users,PortfolioRepository portfolios,PasswordEncoder encoder,AuthenticationManager auth,JwtService jwt){this.users=users;this.portfolios=portfolios;this.encoder=encoder;this.auth=auth;this.jwt=jwt;}
 @Transactional public AuthResponse register(RegisterRequest r){
   String email=r.email().trim().toLowerCase(); if(users.findByEmail(email).isPresent()) throw new IllegalArgumentException("Email is already registered");
   User u=new User();u.setName(r.name().trim());u.setEmail(email);u.setPassword(encoder.encode(r.password()));u.setRole(Role.USER);u=users.save(u);
   Portfolio p=new Portfolio();p.setUser(u);portfolios.save(p);
   UserDetails d=org.springframework.security.core.userdetails.User.withUsername(u.getEmail()).password(u.getPassword()).roles(u.getRole().name()).build();
   return new AuthResponse(jwt.generate(d),u.getId(),u.getName(),u.getEmail(),u.getRole().name());
 }
 public AuthResponse login(LoginRequest r){
   String email=r.email().trim().toLowerCase(); auth.authenticate(new UsernamePasswordAuthenticationToken(email,r.password()));
   User u=users.findByEmail(email).orElseThrow(); UserDetails d=org.springframework.security.core.userdetails.User.withUsername(u.getEmail()).password(u.getPassword()).roles(u.getRole().name()).build();
   return new AuthResponse(jwt.generate(d),u.getId(),u.getName(),u.getEmail(),u.getRole().name());
 }
}
