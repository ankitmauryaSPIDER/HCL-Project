package com.portfoliopro.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="transactions")
public class Transaction {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="user_id", nullable=false) private User user;
    @ManyToOne(optional=false) @JoinColumn(name="stock_id", nullable=false) private Stock stock;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private TradeType type;
    @Column(nullable=false) private Integer quantity;
    @Column(nullable=false, precision=15, scale=2) private BigDecimal price;
    @Column(nullable=false, precision=15, scale=2) private BigDecimal totalValue;
    @Column(nullable=false) private LocalDateTime createdAt;
    public Transaction() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public User getUser(){return user;} public void setUser(User v){user=v;}
    public Stock getStock(){return stock;} public void setStock(Stock v){stock=v;}
    public TradeType getType(){return type;} public void setType(TradeType v){type=v;}
    public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
    public BigDecimal getTotalValue(){return totalValue;} public void setTotalValue(BigDecimal v){totalValue=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}
