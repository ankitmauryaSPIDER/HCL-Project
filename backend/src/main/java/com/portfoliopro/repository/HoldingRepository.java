package com.portfoliopro.repository;
import com.portfoliopro.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface HoldingRepository extends JpaRepository<Holding,Long>{ List<Holding> findByPortfolioId(Long portfolioId); Optional<Holding> findByPortfolioIdAndStockId(Long portfolioId,Long stockId); }
