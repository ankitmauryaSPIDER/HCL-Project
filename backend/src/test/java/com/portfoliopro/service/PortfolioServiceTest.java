package com.portfoliopro.service;

import com.portfoliopro.dto.DashboardResponse;
import com.portfoliopro.dto.HoldingResponse;
import com.portfoliopro.entity.Holding;
import com.portfoliopro.entity.Portfolio;
import com.portfoliopro.entity.Stock;
import com.portfoliopro.entity.User;
import com.portfoliopro.entity.Watchlist;
import com.portfoliopro.repository.HoldingRepository;
import com.portfoliopro.repository.PortfolioRepository;
import com.portfoliopro.repository.StockRepository;
import com.portfoliopro.repository.TransactionRepository;
import com.portfoliopro.repository.UserRepository;
import com.portfoliopro.repository.WatchlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {
    @Mock
    private UserRepository users;
    @Mock
    private PortfolioRepository portfolios;
    @Mock
    private HoldingRepository holdings;
    @Mock
    private TransactionRepository transactions;
    @Mock
    private WatchlistRepository watchlist;
    @Mock
    private StockRepository stocks;

    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        portfolioService = new PortfolioService(users, portfolios, holdings, transactions, watchlist, stocks);
    }

    @Test
    void holdingsOmitsZeroQuantityAndCalculatesValues() {
        Portfolio portfolio = portfolio(3L);
        Stock stock = stock(2L, "ACME", "Technology", "15.00");
        Holding held = holding(portfolio, stock, 4, "10.00");
        Holding empty = holding(portfolio, stock, 0, "10.00");
        when(portfolios.findByUserId(1L)).thenReturn(Optional.of(portfolio));
        when(holdings.findByPortfolioId(3L)).thenReturn(List.of(held, empty));

        List<HoldingResponse> result = portfolioService.holdings(1L);

        assertEquals(1, result.size());
        assertEquals("ACME", result.get(0).symbol());
        assertEquals(0, new BigDecimal("40.00").compareTo(result.get(0).investedValue()));
        assertEquals(0, new BigDecimal("60.00").compareTo(result.get(0).currentValue()));
        assertEquals(0, new BigDecimal("20.00").compareTo(result.get(0).profitLoss()));
    }

    @Test
    void dashboardAggregatesValuesAndReportsConcentrationAndLossWarnings() {
        User user = new User();
        user.setBalance(new BigDecimal("500.00"));
        Portfolio portfolio = portfolio(3L);
        Holding dominantHolding = holding(portfolio, stock(2L, "ACME", "Technology", "10.00"), 9, "12.00");
        Holding secondaryHolding = holding(portfolio, stock(4L, "BETA", "Technology", "10.00"), 1, "12.00");

        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(portfolios.findByUserId(1L)).thenReturn(Optional.of(portfolio));
        when(holdings.findByPortfolioId(3L)).thenReturn(List.of(dominantHolding, secondaryHolding));
        when(transactions.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());
        when(watchlist.findByUserId(1L)).thenReturn(List.of());

        DashboardResponse dashboard = portfolioService.dashboard(1L);

        assertEquals(0, new BigDecimal("120.00").compareTo(dashboard.invested()));
        assertEquals(0, new BigDecimal("100.00").compareTo(dashboard.currentValue()));
        assertEquals(0, new BigDecimal("-20.00").compareTo(dashboard.profitLoss()));
        assertEquals(2, dashboard.stockCount());
        assertEquals(3, dashboard.riskWarnings().size());
        assertEquals("High concentration in ACME (over 40% of portfolio).", dashboard.riskWarnings().get(0));
        assertEquals("High sector concentration in Technology (over 50%).", dashboard.riskWarnings().get(1));
        assertEquals("Portfolio has a loss greater than 10% of current value.", dashboard.riskWarnings().get(2));
    }

    @Test
    void dashboardHasNoRiskWarningsForProfitableDiversifiedPositions() {
        User user = new User();
        Portfolio portfolio = portfolio(3L);
        Holding first = holding(portfolio, stock(2L, "ACME", "Technology", "12.00"), 1, "10.00");
        Holding second = holding(portfolio, stock(4L, "BETA", "Finance", "12.00"), 1, "10.00");
        Holding third = holding(portfolio, stock(5L, "GAMMA", "Healthcare", "12.00"), 1, "10.00");
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(portfolios.findByUserId(1L)).thenReturn(Optional.of(portfolio));
        when(holdings.findByPortfolioId(3L)).thenReturn(List.of(first, second, third));
        when(transactions.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());
        when(watchlist.findByUserId(1L)).thenReturn(List.of());

        assertEquals(List.of(), portfolioService.dashboard(1L).riskWarnings());
    }

    @Test
    void addWatchSavesOnlyWhenPairIsNotAlreadyPresent() {
        User user = new User();
        user.setId(1L);
        Stock stock = stock(2L, "ACME", "Technology", "15.00");
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(stocks.findById(2L)).thenReturn(Optional.of(stock));
        when(watchlist.findByUserIdAndStockId(1L, 2L)).thenReturn(Optional.empty());

        portfolioService.addWatch(1L, 2L);

        verify(watchlist).save(any(Watchlist.class));
    }

    @Test
    void addWatchDoesNotCreateDuplicate() {
        User user = new User();
        user.setId(1L);
        Stock stock = stock(2L, "ACME", "Technology", "15.00");
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(stocks.findById(2L)).thenReturn(Optional.of(stock));
        when(watchlist.findByUserIdAndStockId(1L, 2L)).thenReturn(Optional.of(new Watchlist()));

        portfolioService.addWatch(1L, 2L);

        verify(watchlist, never()).save(any(Watchlist.class));
    }

    @Test
    void missingPortfolioIsRejected() {
        when(portfolios.findByUserId(1L)).thenReturn(Optional.empty());

        assertEquals("Portfolio not found", assertThrows(IllegalArgumentException.class,
                () -> portfolioService.holdings(1L)).getMessage());
    }

    private static Portfolio portfolio(Long id) {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(id);
        return portfolio;
    }

    private static Stock stock(Long id, String symbol, String sector, String currentPrice) {
        Stock stock = new Stock();
        stock.setId(id);
        stock.setSymbol(symbol);
        stock.setCompanyName(symbol + " Corporation");
        stock.setSector(sector);
        stock.setCurrentPrice(new BigDecimal(currentPrice));
        stock.setPreviousPrice(new BigDecimal(currentPrice));
        stock.setMarketCap(BigDecimal.ZERO);
        stock.setPeRatio(BigDecimal.ZERO);
        stock.setEps(BigDecimal.ZERO);
        stock.setWeek52High(BigDecimal.ZERO);
        stock.setWeek52Low(BigDecimal.ZERO);
        stock.setDayChangePercent(BigDecimal.ZERO);
        return stock;
    }

    private static Holding holding(Portfolio portfolio, Stock stock, int quantity, String averagePrice) {
        Holding holding = new Holding();
        holding.setPortfolio(portfolio);
        holding.setStock(stock);
        holding.setQuantity(quantity);
        holding.setAverageBuyPrice(new BigDecimal(averagePrice));
        return holding;
    }
}
