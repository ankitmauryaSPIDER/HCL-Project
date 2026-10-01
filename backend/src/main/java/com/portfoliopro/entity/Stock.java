package com.portfoliopro.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="stocks")
public class Stock {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=20) private String symbol;
    @Column(nullable=false) private String companyName;
    @Column(nullable=false, precision=15, scale=2) private BigDecimal currentPrice;
    private BigDecimal previousPrice;
    private BigDecimal marketCap;
    private BigDecimal peRatio;
    private BigDecimal eps;
    private BigDecimal week52High;
    private BigDecimal week52Low;
    private BigDecimal dayChangePercent;
    private String sector;
    public Stock() {}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getSymbol(){return symbol;} public void setSymbol(String v){symbol=v;}
    public String getCompanyName(){return companyName;} public void setCompanyName(String v){companyName=v;}
    public BigDecimal getCurrentPrice(){return currentPrice;} public void setCurrentPrice(BigDecimal v){currentPrice=v;}
    public BigDecimal getPreviousPrice(){return previousPrice;} public void setPreviousPrice(BigDecimal v){previousPrice=v;}
    public BigDecimal getMarketCap(){return marketCap;} public void setMarketCap(BigDecimal v){marketCap=v;}
    public BigDecimal getPeRatio(){return peRatio;} public void setPeRatio(BigDecimal v){peRatio=v;}
    public BigDecimal getEps(){return eps;} public void setEps(BigDecimal v){eps=v;}
    public BigDecimal getWeek52High(){return week52High;} public void setWeek52High(BigDecimal v){week52High=v;}
    public BigDecimal getWeek52Low(){return week52Low;} public void setWeek52Low(BigDecimal v){week52Low=v;}
    public BigDecimal getDayChangePercent(){return dayChangePercent;} public void setDayChangePercent(BigDecimal v){dayChangePercent=v;}
    public String getSector(){return sector;} public void setSector(String v){sector=v;}
}
