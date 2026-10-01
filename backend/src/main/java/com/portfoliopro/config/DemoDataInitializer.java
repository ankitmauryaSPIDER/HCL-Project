package com.portfoliopro.config;

import com.portfoliopro.entity.*;
import com.portfoliopro.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;

@Configuration
public class DemoDataInitializer {
    @Bean CommandLineRunner seed(UserRepository users, PortfolioRepository portfolios, StockRepository stocks, PasswordEncoder encoder) {
        return args -> {
            User demo = users.findByEmail("demo@portfoliopro.com").orElseGet(() -> {
                User u=new User();u.setName("Demo Investor");u.setEmail("demo@portfoliopro.com");u.setPassword(encoder.encode("Demo@123"));u.setRole(Role.USER);u.setBalance(new BigDecimal("100000.00"));return users.save(u);
            });
            if(portfolios.findByUserId(demo.getId()).isEmpty()){Portfolio p=new Portfolio();p.setUser(demo);portfolios.save(p);}
            User admin = users.findByEmail("admin@portfoliopro.com").orElseGet(() -> {User u=new User();u.setName("PortfolioPro Admin");u.setEmail("admin@portfoliopro.com");u.setPassword(encoder.encode("Admin@123"));u.setRole(Role.ADMIN);u.setBalance(new BigDecimal("100000.00"));return users.save(u);});
            if(portfolios.findByUserId(admin.getId()).isEmpty()){Portfolio p=new Portfolio();p.setUser(admin);portfolios.save(p);}
            seedStock(stocks,"RELIANCE","Reliance Industries","2945.50","Energy","19.20","154.00","3180.00","2200.00","0.62");
            seedStock(stocks,"TCS","Tata Consultancy Services","4250.75","IT","29.10","146.20","4600.00","3200.00","-0.52");
            seedStock(stocks,"INFY","Infosys","1588.20","IT","24.50","64.80","1730.00","1250.00","0.80");
            seedStock(stocks,"HDFCBANK","HDFC Bank","1765.30","Banking","20.40","86.50","1880.00","1360.00","0.40");
            seedStock(stocks,"ICICIBANK","ICICI Bank","1325.10","Banking","18.80","70.40","1420.00","920.00","-0.41");
            seedStock(stocks,"ITC","ITC","505.25","FMCG","23.10","21.90","560.00","390.00","0.62");
            seedStock(stocks,"SBIN","State Bank of India","845.70","Banking","11.90","71.10","910.00","600.00","1.11");
            seedStock(stocks,"BHARTIARTL","Bharti Airtel","1920.40","Telecom","29.70","64.60","1980.00","1120.00","1.30");
        };
    }
    private void seedStock(StockRepository r,String symbol,String company,String price,String sector,String pe,String eps,String high,String low,String change){
        if(r.findBySymbol(symbol).isEmpty()){Stock s=new Stock();s.setSymbol(symbol);s.setCompanyName(company);s.setCurrentPrice(new BigDecimal(price));s.setPreviousPrice(new BigDecimal(price));s.setMarketCap(new BigDecimal("1000000"));s.setPeRatio(new BigDecimal(pe));s.setEps(new BigDecimal(eps));s.setWeek52High(new BigDecimal(high));s.setWeek52Low(new BigDecimal(low));s.setDayChangePercent(new BigDecimal(change));s.setSector(sector);r.save(s);}
    }
}
