# Manual Test Checklist

1. Start MySQL.
2. Start Spring Boot and confirm `Started PortfolioProApplication`.
3. Start Angular and open `http://localhost:4200`.
4. Login with `demo@portfoliopro.com / Demo@123`.
5. Open Stocks and confirm seeded stocks appear.
6. Add RELIANCE to watchlist and confirm it appears on Watchlist.
7. BUY 5 RELIANCE shares.
8. Confirm balance decreases and holding appears.
9. SELL 2 RELIANCE shares.
10. Confirm balance increases and holding quantity decreases.
11. Open Transactions and confirm BUY and SELL records.
12. Open Portfolio and confirm invested/current/P&L values.
13. Open Analysis and confirm fundamental fields.
14. Try BUY with quantity 0; backend should reject it.
15. Try SELL more shares than held; backend should reject it.
16. Login as admin and verify Admin page.
17. Add/update/delete a demo stock as admin.
18. Login as a normal user and confirm Admin page is not accessible.
