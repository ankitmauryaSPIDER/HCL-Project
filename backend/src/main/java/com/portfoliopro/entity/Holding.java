package com.portfoliopro.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="holdings", uniqueConstraints=@UniqueConstraint(columnNames={"portfolio_id","stock_id"}))
public class Holding {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="portfolio_id", nullable=false) @JsonIgnore private Portfolio portfolio;
    @ManyToOne(optional=false) @JoinColumn(name="stock_id", nullable=false) private Stock stock;
    @Column(nullable=false) private Integer quantity;
    @Column(nullable=false, precision=15, scale=4) private BigDecimal averageBuyPrice;
    public Holding() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Portfolio getPortfolio(){return portfolio;} public void setPortfolio(Portfolio v){portfolio=v;}
    public Stock getStock(){return stock;} public void setStock(Stock v){stock=v;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
    public BigDecimal getAverageBuyPrice(){return averageBuyPrice;} public void setAverageBuyPrice(BigDecimal v){averageBuyPrice=v;}
}
