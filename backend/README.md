# PortfolioPro Backend

Spring Boot REST API for PortfolioPro.

Run from this directory:

```powershell
mvn spring-boot:run
```

Configuration is in `src/main/resources/application.properties`.

Set your own MySQL password and private JWT signing key in the terminal before starting the backend. Keep these values out of Git:

```powershell
$env:DB_PASSWORD = "your-local-mysql-password"
$env:JWT_SECRET = "your-private-random-secret-at-least-32-characters"
mvn spring-boot:run
```
