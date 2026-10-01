# PortfolioPro — Stock Trading & Portfolio Management System

PortfolioPro is a simple, full-stack **paper trading** application for a B.Tech college project. It uses Angular, Spring Boot, Spring Security/JWT, JPA/Hibernate and MySQL.

> **Important:** This project uses seeded/demo market data and simulated trades. It does not connect to a broker, NSE/BSE live trading system or real-money account.

## 1. Technology

- Frontend: Angular 20, TypeScript, HTML, CSS
- Backend: Java 25 LTS, Spring Boot 3.5.6, Spring Web, Spring Data JPA, Spring Security, JWT
- Database: MySQL 8.x
- Build: Maven + npm

## 2. Folder structure

```text
PortfolioPro/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/portfoliopro/
├── frontend/
│   ├── package.json
│   └── src/
├── database/
│   ├── schema.sql
│   └── sample-data.sql
└── docs/
```

There is **no Git repository, .git folder or submodule** inside this ZIP.

## 3. Requirements on Windows

Install:

1. JDK 25 LTS or newer
2. Maven 3.9+
3. Node.js 20.19+, 22.22.3+, or 24.15.0+
4. MySQL Server 8.x
5. VS Code

Check:

```powershell
java -version
mvn -version
node -v
npm -v
mysql --version
```

## 4. MySQL setup

The application is configured for:

- Host: `localhost`
- Port: `3306`
- Database: `portfolio`
- Username: `root`
- Password: provide your local MySQL password through `DB_PASSWORD`

You can manually create the database:

```sql
CREATE DATABASE IF NOT EXISTS portfolio;
```

The JDBC URL also contains `createDatabaseIfNotExist=true`, so MySQL will create the database if the root account has permission.

### Password configuration

The backend uses:

```properties
spring.datasource.password=${DB_PASSWORD}
app.jwt.secret=${JWT_SECRET}
```

Set both values in the backend terminal before starting the API. Use your own MySQL password and a private random JWT signing key of at least 32 bytes. Do not commit either value.

```powershell
$env:DB_PASSWORD = "your-local-mysql-password"
$env:JWT_SECRET = "your-private-random-secret-at-least-32-characters"
```

Then start the backend.

## 5. Start the backend

Open VS Code terminal:

```powershell
cd PortfolioPro\backend
mvn spring-boot:run
```

Backend:

`http://localhost:8080`

On the first successful startup, Hibernate creates/updates the tables and the application seeds demo users and stock data.

## 6. Start the frontend

Open a **second** terminal:

```powershell
cd PortfolioPro\frontend
npm install
npm start
```

Open:

`http://localhost:4200`

## 7. Demo accounts

### User

- Email: `demo@portfoliopro.com`
- Password: `Demo@123`
- Starting balance: ₹100,000

### Admin

- Email: `admin@portfoliopro.com`
- Password: `Admin@123`

## 8. Main functionality

### Authentication
- Registration
- Login
- JWT authentication
- BCrypt password hashing
- USER and ADMIN roles
- Client-side logout

### Stocks
- Search stocks
- View stock details
- Seeded price and fundamental data
- Watchlist

### Paper trading
- BUY and SELL
- Positive quantity validation
- BUY balance validation
- SELL holding validation
- Automatic balance update
- Automatic holding update
- Trade and transaction records

### Portfolio
- Invested value
- Current value
- Profit/loss
- Average buy price
- Allocation bars
- Recent transactions

### Risk warnings
Simple rule-based checks:

- Single stock over 40% of portfolio
- Sector over 50% of portfolio
- Portfolio loss over 10%

### Admin
- View users
- Add/update/delete stocks
- View trades
- View transactions

## 9. Important API endpoints

```text
POST /api/auth/register
POST /api/auth/login

GET  /api/users/me
GET  /api/stocks
GET  /api/stocks/{id}

GET  /api/portfolio/dashboard
GET  /api/portfolio/holdings
GET  /api/portfolio/transactions
GET  /api/portfolio/watchlist
POST /api/portfolio/watchlist/{stockId}
DELETE /api/portfolio/watchlist/{stockId}

POST /api/trades
GET  /api/transactions

GET  /api/admin/users
GET  /api/admin/trades
GET  /api/admin/transactions
POST /api/admin/stocks
PUT  /api/admin/stocks/{id}
DELETE /api/admin/stocks/{id}
```

## 10. If something does not start

### MySQL connection error
Check that MySQL is running and verify:

```text
username = root
password = set through the DB_PASSWORD environment variable
database = portfolio
port = 3306
```

If your local MySQL password is different, use:

```powershell
$env:DB_PASSWORD="your-password"
```

### Port 8080 already in use
Change `server.port` in `backend/src/main/resources/application.properties` and update the frontend API base URL in:

`frontend/src/app/core/api.service.ts`

and

`frontend/src/app/core/auth.service.ts`

### Frontend says it cannot connect to API
Make sure Spring Boot is already running on port 8080.

### Angular dependency problem
Delete `node_modules` and `package-lock.json`, then run:

```powershell
npm install
npm start
```

## 11. Project boundary

This is intentionally a student-friendly system. It does not implement live market feeds, brokerage order routing, real-money settlement, derivatives, high-frequency trading, machine learning or microservices.
