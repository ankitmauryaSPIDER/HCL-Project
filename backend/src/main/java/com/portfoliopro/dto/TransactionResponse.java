package com.portfoliopro.dto;
import java.math.BigDecimal; import java.time.LocalDateTime;
public record TransactionResponse(Long id,String symbol,String companyName,String type,Integer quantity,BigDecimal price,BigDecimal totalValue,LocalDateTime createdAt) {}
