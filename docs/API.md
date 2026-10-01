# PortfolioPro API Quick Reference

Authentication is JWT-based. Login returns a token. The Angular frontend stores the token locally and sends it as `Authorization: Bearer <token>`.

## Auth

`POST /api/auth/register`

```json
{"name":"Ankit","email":"ankit@example.com","password":"secret123"}
```

`POST /api/auth/login`

```json
{"email":"demo@portfoliopro.com","password":"Demo@123"}
```

## Trade

`POST /api/trades`

```json
{"symbol":"RELIANCE","type":"BUY","quantity":5}
```

For SELL:

```json
{"symbol":"RELIANCE","type":"SELL","quantity":2}
```

The server determines the current seeded price and updates balance, holdings, trade and transaction records inside one database transaction.
