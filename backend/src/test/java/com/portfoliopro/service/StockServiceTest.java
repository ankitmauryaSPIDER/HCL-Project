package com.portfoliopro.service;

import com.portfoliopro.dto.StockRequest;
import com.portfoliopro.entity.Stock;
import com.portfoliopro.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {
    @Mock
    private StockRepository stocks;

    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockService(stocks);
    }

    @Test
    void allReturnsEveryStockWhenQueryIsBlank() {
        List<Stock> expected = List.of(new Stock());
        when(stocks.findAll()).thenReturn(expected);

        assertSame(expected, stockService.all("  "));
        verify(stocks).findAll();
    }

    @Test
    void allSearchesSymbolAndCompanyNameWhenQueryIsPresent() {
        List<Stock> expected = List.of(new Stock());
        when(stocks.findBySymbolContainingIgnoreCaseOrCompanyNameContainingIgnoreCase("Acme", "Acme"))
                .thenReturn(expected);

        assertSame(expected, stockService.all("Acme"));
    }

    @Test
    void getReturnsMatchingStock() {
        Stock expected = new Stock();
        when(stocks.findById(3L)).thenReturn(Optional.of(expected));

        assertSame(expected, stockService.get(3L));
    }

    @Test
    void bySymbolNormalizesCase() {
        Stock expected = new Stock();
        when(stocks.findBySymbol("ACME")).thenReturn(Optional.of(expected));

        assertSame(expected, stockService.bySymbol("acme"));
    }

    @Test
    void saveNormalizesSymbolAndTextFields() {
        when(stocks.save(any(Stock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Stock result = stockService.save(stockRequest(" acme ", " Acme Inc ", " Technology "));

        assertEquals("ACME", result.getSymbol());
        assertEquals("Acme Inc", result.getCompanyName());
        assertEquals("Technology", result.getSector());
        assertEquals(0, new BigDecimal("12.50").compareTo(result.getCurrentPrice()));
        verify(stocks).save(any(Stock.class));
    }

    @Test
    void updateMutatesAndSavesExistingStock() {
        Stock existing = new Stock();
        existing.setId(3L);
        when(stocks.findById(3L)).thenReturn(Optional.of(existing));
        when(stocks.save(existing)).thenReturn(existing);

        Stock result = stockService.update(3L, stockRequest(" beta ", " Beta Co ", " Finance "));

        assertSame(existing, result);
        assertEquals("BETA", existing.getSymbol());
        assertEquals("Beta Co", existing.getCompanyName());
        assertEquals("Finance", existing.getSector());
        verify(stocks).save(existing);
    }

    @Test
    void missingStockThrowsForGetAndBySymbol() {
        when(stocks.findById(99L)).thenReturn(Optional.empty());
        when(stocks.findBySymbol("UNKNOWN")).thenReturn(Optional.empty());

        assertEquals("Stock not found", assertThrows(IllegalArgumentException.class,
                () -> stockService.get(99L)).getMessage());
        assertEquals("Stock not found", assertThrows(IllegalArgumentException.class,
                () -> stockService.bySymbol("unknown")).getMessage());
    }

    @Test
    void deleteDelegatesToRepository() {
        stockService.delete(3L);

        verify(stocks).deleteById(3L);
    }

    private static StockRequest stockRequest(String symbol, String companyName, String sector) {
        return new StockRequest(symbol, companyName, new BigDecimal("12.50"), new BigDecimal("12.00"),
                new BigDecimal("1000000"), new BigDecimal("15.2"), new BigDecimal("2.1"),
                new BigDecimal("20"), new BigDecimal("8"), new BigDecimal("1.5"), sector);
    }
}
