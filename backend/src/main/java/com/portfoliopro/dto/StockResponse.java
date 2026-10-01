package com.portfoliopro.dto;
import java.math.BigDecimal;
public record StockResponse(Long id,String symbol,String companyName,BigDecimal currentPrice,BigDecimal previousPrice,BigDecimal marketCap,BigDecimal peRatio,BigDecimal eps,BigDecimal week52High,BigDecimal week52Low,BigDecimal dayChangePercent,String sector) {}
