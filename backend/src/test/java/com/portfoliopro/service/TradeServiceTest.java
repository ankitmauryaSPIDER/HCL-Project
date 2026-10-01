package com.portfoliopro.service;

import com.portfoliopro.dto.TradeRequest;
import com.portfoliopro.entity.Holding;
import com.portfoliopro.entity.Portfolio;
import com.portfoliopro.entity.Stock;
import com.portfoliopro.entity.Trade;
import com.portfoliopro.entity.TradeType;
import com.portfoliopro.entity.Transaction;
import com.portfoliopro.entity.User;
import com.portfoliopro.repository.HoldingRepository;
import com.portfoliopro.repository.PortfolioRepository;
import com.portfoliopro.repository.StockRepository;
import com.portfoliopro.repository.TradeRepository;
import com.portfoliopro.repository.TransactionRepository;
import com.portfoliopro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {
    private static final String EMAIL = "trader@example.com";

    @Mock
    private UserRepository users;
    @Mock
    private StockRepository stocks;
    @Mock
    private PortfolioRepository portfolios;
    @Mock
    private HoldingRepository holdings;
    @Mock
    private TradeRepository trades;
    @Mock
    private TransactionRepository transactions;
    @InjectMocks
    private TradeService tradeService;

    private User user;
    private Stock stock;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);
        user.setBalance(new BigDecimal("1000.00"));

        stock = new Stock();
        stock.setId(2L);
        stock.setSymbol("ACME");
        stock.setCurrentPrice(new BigDecimal("20.00"));

        portfolio = new Portfolio();
        portfolio.setId(3L);

        lenient().when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        lenient().when(stocks.findBySymbol("ACME")).thenReturn(Optional.of(stock));
        lenient().when(portfolios.findByUserId(1L)).thenReturn(Optional.of(portfolio));
        lenient().when(holdings.findByPortfolioIdAndStockId(3L, 2L)).thenReturn(Optional.empty());
        lenient().when(trades.save(any(Trade.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(transactions.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void buyCreatesHoldingAndPersistsTradeAndTransaction() {
        Trade result = tradeService.execute(EMAIL, new TradeRequest(" acme ", " buy ", 3));

        assertEquals(TradeType.BUY, result.getType());
        assertEquals(3, result.getQuantity());
        assertEquals(0, new BigDecimal("940.00").compareTo(user.getBalance()));
        assertEquals(0, new BigDecimal("60.00").compareTo(result.getTotalValue()));

        ArgumentCaptor<Holding> holdingCaptor = ArgumentCaptor.forClass(Holding.class);
        verify(holdings).save(holdingCaptor.capture());
        assertEquals(3, holdingCaptor.getValue().getQuantity());
        assertEquals(0, new BigDecimal("20.00").compareTo(holdingCaptor.getValue().getAverageBuyPrice()));
        verify(users).save(user);
        verify(trades).save(any(Trade.class));
        verify(transactions).save(any(Transaction.class));
    }

    @Test
    void buyUpdatesExistingHoldingUsingWeightedAveragePrice() {
        Holding holding = new Holding();
        holding.setPortfolio(portfolio);
        holding.setStock(stock);
        holding.setQuantity(4);
        holding.setAverageBuyPrice(new BigDecimal("10.00"));
        when(holdings.findByPortfolioIdAndStockId(3L, 2L)).thenReturn(Optional.of(holding));

        tradeService.execute(EMAIL, new TradeRequest("ACME", "BUY", 2));

        assertEquals(6, holding.getQuantity());
        assertEquals(0, new BigDecimal("13.3333").compareTo(holding.getAverageBuyPrice()));
        assertEquals(0, new BigDecimal("960.00").compareTo(user.getBalance()));
        verify(holdings).save(holding);
    }

    @Test
    void sellReducesHoldingAndCreditsBalance() {
        Holding holding = new Holding();
        holding.setPortfolio(portfolio);
        holding.setStock(stock);
        holding.setQuantity(5);
        holding.setAverageBuyPrice(new BigDecimal("15.00"));
        when(holdings.findByPortfolioIdAndStockId(3L, 2L)).thenReturn(Optional.of(holding));

        Trade result = tradeService.execute(EMAIL, new TradeRequest("ACME", "SELL", 2));

        assertEquals(TradeType.SELL, result.getType());
        assertEquals(3, holding.getQuantity());
        assertEquals(0, new BigDecimal("1040.00").compareTo(user.getBalance()));
        verify(holdings).save(holding);
    }

    @Test
    void sellingEntireHoldingDeletesIt() {
        Holding holding = new Holding();
        holding.setPortfolio(portfolio);
        holding.setStock(stock);
        holding.setQuantity(2);
        holding.setAverageBuyPrice(new BigDecimal("15.00"));
        when(holdings.findByPortfolioIdAndStockId(3L, 2L)).thenReturn(Optional.of(holding));

        tradeService.execute(EMAIL, new TradeRequest("ACME", "SELL", 2));

        assertEquals(0, user.getBalance().compareTo(new BigDecimal("1040.00")));
        verify(holdings).delete(holding);
    }

    @Test
    void rejectsMissingUserBeforeAccessingOtherRepositories() {
        when(users.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertEquals("User not found", assertThrows(IllegalArgumentException.class,
                () -> tradeService.execute(EMAIL, new TradeRequest("ACME", "BUY", 1))).getMessage());
        verifyNoInteractions(stocks, portfolios, holdings, trades, transactions);
    }

    @Test
    void rejectsUnknownStock() {
        when(stocks.findBySymbol("UNKNOWN")).thenReturn(Optional.empty());

        assertEquals("Stock not found", assertThrows(IllegalArgumentException.class,
                () -> tradeService.execute(EMAIL, new TradeRequest("unknown", "BUY", 1))).getMessage());
    }

    @Test
    void rejectsInvalidTypeAndNonPositiveQuantity() {
        IllegalArgumentException invalidType = assertThrows(IllegalArgumentException.class,
                () -> tradeService.execute(EMAIL, new TradeRequest("ACME", "HOLD", 1)));
        IllegalArgumentException invalidQuantity = assertThrows(IllegalArgumentException.class,
                () -> tradeService.execute(EMAIL, new TradeRequest("ACME", "BUY", 0)));

        assertEquals("Trade type must be BUY or SELL", invalidType.getMessage());
        assertEquals("Quantity must be greater than zero", invalidQuantity.getMessage());
    }

    @Test
    void rejectsBuyWhenBalanceIsInsufficient() {
        user.setBalance(new BigDecimal("10.00"));

        assertEquals("Insufficient balance for BUY", assertThrows(IllegalArgumentException.class,
                () -> tradeService.execute(EMAIL, new TradeRequest("ACME", "BUY", 1))).getMessage());
    }

    @Test
    void rejectsSellWithoutSufficientHoldings() {
        assertEquals("Insufficient holdings for SELL", assertThrows(IllegalArgumentException.class,
                () -> tradeService.execute(EMAIL, new TradeRequest("ACME", "SELL", 1))).getMessage());
    }

    @Test
    void rejectsTradeWhenPortfolioDoesNotExist() {
        when(portfolios.findByUserId(1L)).thenReturn(Optional.empty());

        assertEquals("Portfolio not found", assertThrows(IllegalArgumentException.class,
                () -> tradeService.execute(EMAIL, new TradeRequest("ACME", "BUY", 1))).getMessage());
        verifyNoInteractions(trades, transactions);
    }
}
