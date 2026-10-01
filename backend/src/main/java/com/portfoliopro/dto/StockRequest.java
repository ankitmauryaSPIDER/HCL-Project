package com.portfoliopro.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record StockRequest(@NotBlank String symbol,@NotBlank String companyName,@Positive BigDecimal currentPrice,BigDecimal previousPrice,BigDecimal marketCap,BigDecimal peRatio,BigDecimal eps,BigDecimal week52High,BigDecimal week52Low,BigDecimal dayChangePercent,@NotBlank String sector) {}
