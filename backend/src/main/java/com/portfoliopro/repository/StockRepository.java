package com.portfoliopro.repository;
import com.portfoliopro.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
public interface StockRepository extends JpaRepository<Stock,Long>{ Optional<Stock> findBySymbol(String symbol); List<Stock> findBySymbolContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(String symbol,String companyName); }
