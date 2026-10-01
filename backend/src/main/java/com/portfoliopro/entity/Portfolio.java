package com.portfoliopro.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity @Table(name="portfolios")
public class Portfolio {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(optional=false) @JoinColumn(name="user_id", nullable=false, unique=true) @JsonIgnore private User user;
    public Portfolio() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public User getUser(){return user;} public void setUser(User v){user=v;}
}
