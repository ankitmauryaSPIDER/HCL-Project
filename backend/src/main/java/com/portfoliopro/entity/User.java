package com.portfoliopro.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="users")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String name;
    @Column(nullable=false, unique=true) private String email;
    @JsonIgnore @Column(nullable=false) private String password;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role = Role.USER;
    @Column(nullable=false, precision=15, scale=2) private BigDecimal balance = new BigDecimal("100000.00");
    public User() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPassword(){return password;} public void setPassword(String v){password=v;}
    public Role getRole(){return role;} public void setRole(Role v){role=v;}
    public BigDecimal getBalance(){return balance;} public void setBalance(BigDecimal v){balance=v;}
}
