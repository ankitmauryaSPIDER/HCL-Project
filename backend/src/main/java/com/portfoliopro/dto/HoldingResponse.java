package com.portfoliopro.dto;
import java.math.BigDecimal;
public record HoldingResponse(Long id,String symbol,String companyName,String sector,Integer quantity,BigDecimal averageBuyPrice,BigDecimal currentPrice,BigDecimal investedValue,BigDecimal currentValue,BigDecimal profitLoss) {}
