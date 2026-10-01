package com.portfoliopro.service;
import com.portfoliopro.dto.StockRequest; import com.portfoliopro.entity.Stock; import com.portfoliopro.repository.StockRepository; import org.springframework.stereotype.Service; import java.util.*;
@Service public class StockService {
 private final StockRepository stocks; public StockService(StockRepository stocks){this.stocks=stocks;}
 public List<Stock> all(String q){return q==null||q.isBlank()?stocks.findAll():stocks.findBySymbolContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(q,q);}
 public Stock get(Long id){return stocks.findById(id).orElseThrow(()->new IllegalArgumentException("Stock not found"));}
 public Stock bySymbol(String symbol){return stocks.findBySymbol(symbol.toUpperCase()).orElseThrow(()->new IllegalArgumentException("Stock not found"));}
 public Stock save(StockRequest r){Stock s=new Stock(); apply(s,r); return stocks.save(s);}
 public Stock update(Long id,StockRequest r){Stock s=get(id); apply(s,r); return stocks.save(s);}
 public void delete(Long id){stocks.deleteById(id);}
 private void apply(Stock s,StockRequest r){s.setSymbol(r.symbol().trim().toUpperCase());s.setCompanyName(r.companyName().trim());s.setCurrentPrice(r.currentPrice());s.setPreviousPrice(r.previousPrice());s.setMarketCap(r.marketCap());s.setPeRatio(r.peRatio());s.setEps(r.eps());s.setWeek52High(r.week52High());s.setWeek52Low(r.week52Low());s.setDayChangePercent(r.dayChangePercent());s.setSector(r.sector().trim());}
}
