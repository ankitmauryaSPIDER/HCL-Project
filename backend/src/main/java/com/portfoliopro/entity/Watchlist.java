package com.portfoliopro.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity @Table(name="watchlist", uniqueConstraints=@UniqueConstraint(columnNames={"user_id","stock_id"}))
public class Watchlist {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="user_id", nullable=false) @JsonIgnore private User user;
    @ManyToOne(optional=false) @JoinColumn(name="stock_id", nullable=false) private Stock stock;
    public Watchlist() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public User getUser(){return user;} public void setUser(User v){user=v;}
    public Stock getStock(){return stock;} public void setStock(Stock v){stock=v;}
}
