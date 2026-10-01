package com.portfoliopro.dto;
import java.math.BigDecimal; import java.util.List;
public record DashboardResponse(BigDecimal balance,BigDecimal invested,BigDecimal currentValue,BigDecimal profitLoss,long stockCount,List<HoldingResponse> holdings,List<TransactionResponse> transactions,List<StockResponse> watchlist,List<String> riskWarnings) {}
