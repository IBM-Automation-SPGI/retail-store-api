# retail-store-api

Backend service for a small retail store. It exposes a REST API for browsing products, managing customer carts, placing orders, issuing coupons and downloading order receipts.

Built with Spring Boot 2.7, JDBC and an in-memory H2 database that is seeded at startup (about 20 products, 3 customers, one coupon `WELCOME10`).

## Requirements

- JDK 17 or newer
- Maven 3.6+

## Run

```
mvn spring-boot:run
```

The service listens on port 8080. Run the tests with `mvn verify`.

## Configuration

`src/main/resources/application.properties`:

| Property | Description |
|---|---|
| `store.receipts.dir` | Directory where receipts are written (default `./receipts`) |
| `payment.gateway.url` | Payment gateway endpoint |
| `payment.gateway.api-key` | Payment gateway API key |

## Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/products` | List products |
| GET | `/api/products/{id}` | Get a product |
| GET | `/api/products/search?q=` | Search products by name |
| POST | `/api/cart/{customerId}/items` | Add an item (`{"productId":1,"quantity":2}`) |
| GET | `/api/cart/{customerId}` | View the cart |
| POST | `/api/checkout/{customerId}` | Check out; optional body `{"couponCode":"...","note":"..."}` |
| GET | `/api/orders/{id}` | Get an order |
| GET | `/api/customers/{id}/orders` | List a customer's orders |
| GET | `/api/receipts/download?file=` | Download a receipt, e.g. `receipt-1.txt` |
| POST | `/api/coupons` | Generate a coupon (`{"percent":15}`) |

## Example

```
curl -X POST localhost:8080/api/cart/1/items -H 'Content-Type: application/json' -d '{"productId":1,"quantity":2}'
curl -X POST localhost:8080/api/checkout/1 -H 'Content-Type: application/json' -d '{"couponCode":"WELCOME10"}'
curl 'localhost:8080/api/receipts/download?file=receipt-1.txt'
```
