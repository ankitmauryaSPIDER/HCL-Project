package com.portfoliopro.dto;
import jakarta.validation.constraints.*;
public record TradeRequest(@NotBlank String symbol,@NotBlank String type,@Min(1) int quantity) {}
