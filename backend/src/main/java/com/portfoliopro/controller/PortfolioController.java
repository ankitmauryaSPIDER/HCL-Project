package com.portfoliopro.controller;
import com.portfoliopro.dto.*; import com.portfoliopro.service.PortfolioService; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/portfolio") public class PortfolioController {
 private final PortfolioService service; public PortfolioController(PortfolioService service){this.service=service;}
 private Long uid(Authentication a){return service.userByEmail(a.getName()).getId();}
 @GetMapping("/dashboard") public DashboardResponse dashboard(Authentication a){return service.dashboard(uid(a));}
 @GetMapping("/holdings") public List<HoldingResponse> holdings(Authentication a){return service.holdings(uid(a));}
 @GetMapping("/transactions") public List<TransactionResponse> transactions(Authentication a){return service.transactions(uid(a));}
 @GetMapping("/watchlist") public List<StockResponse> watchlist(Authentication a){return service.watchlist(uid(a));}
 @PostMapping("/watchlist/{stockId}") public void add(@PathVariable Long stockId,Authentication a){service.addWatch(uid(a),stockId);}
 @DeleteMapping("/watchlist/{stockId}") public void remove(@PathVariable Long stockId,Authentication a){service.removeWatch(uid(a),stockId);}
}
