# inventory-service

Microservicio responsable de gestionar el stock disponible de productos dentro de la plataforma.

## Contexto de negocio

El `inventory-service` administra:

- Disponibilidad de productos
- Reservas temporales de stock
- Confirmación de stock luego de un pago exitoso
- Liberación de unidades cuando una orden es cancelada

Su objetivo principal es garantizar consistencia sobre las cantidades disponibles y evitar la sobreventa de productos.

## Diagrama de estados de una reserva

```
                        reserveStock(orderId, units)
                               │
                               ▼
                           RESERVED ──────────────────────────────────────┐
                          /        \                                       │
    confirmReservation()  /          \ releaseReservation()                │ expira (expiresAt)
                         ▼            ▼                                   ▼
                     CONFIRMED     RELEASED                            EXPIRED
```

| Estado      | Descripción                                                   |
|-------------|---------------------------------------------------------------|
| `RESERVED`  | Unidades reservadas temporalmente para una orden              |
| `CONFIRMED` | Stock descontado definitivamente tras pago exitoso            |
| `RELEASED`  | Unidades liberadas de vuelta al stock (orden cancelada)       |
| `EXPIRED`   | Reserva vencida por superar el tiempo máximo de expiración    |

## Endpoints

| Método | Path                                  | Descripción                               | Respuesta exitosa |
|--------|---------------------------------------|-------------------------------------------|-------------------|
| POST   | `/api/inventory/reserve`              | Reserva temporal de unidades              | 201 Created       |
| POST   | `/api/inventory/confirm`              | Confirma reserva tras pago exitoso        | 200 OK            |
| POST   | `/api/inventory/release`              | Libera reserva (orden cancelada)          | 200 OK            |
| GET    | `/api/inventory/availability/{id}`    | Disponibilidad actual de un producto      | 200 OK            |
| GET    | `/api/inventory/reports/metrics`      | Métricas operativas para reporting        | 200 OK            |

### POST /api/inventory/reserve
```json
// Request
{ "productId": "prod-1", "units": 3, "orderId": "order-99" }

// 201 Created
{ "reservationId": "...", "productId": "prod-1", "units": 3, "status": "RESERVED", "expiresAt": "..." }
```
Errores: `400` (body inválido) · `404` (producto inexistente) · `409` (stock insuficiente)

### POST /api/inventory/confirm
```json
{ "reservationId": "res-abc" }
// 200 OK → status: "CONFIRMED"
```
Errores: `404` (reserva inexistente) · `409` (estado inválido)

### POST /api/inventory/release
```json
{ "reservationId": "res-abc" }
// 200 OK → status: "RELEASED"
```
Errores: `404` (reserva inexistente) · `409` (reserva CONFIRMED no puede liberarse)

### GET /api/inventory/availability/{productId}
```json
{ "productId": "prod-1", "available": 12, "reserved": 3 }
```

### GET /api/inventory/reports/metrics
```json
{ "outOfStockProducts": 1, "activeReservations": 3, "totalAvailableUnits": 89 }
```

## Cómo correr

```bash
# Compilar y ejecutar
mvn spring-boot:run

# Ejecutar tests
mvn test

# Consola H2 (base de datos en memoria)
# http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:inventorydb
# User: sa  /  Password: (vacío)
```

## Stack

- Java 21
- Spring Boot 3.2.5
- Spring Data JPA + H2 (in-memory)
- Maven
- JUnit 5 + Mockito + AssertJ + MockMvc
