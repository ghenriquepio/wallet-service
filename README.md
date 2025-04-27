
# Wallet Service API

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring WebFlux](https://img.shields.io/badge/Spring-WebFlux-6DB33F.svg)](https://spring.io/projects/spring-webflux)
[![Keycloak](https://img.shields.io/badge/Keycloak-Authentication-blueviolet)](https://www.keycloak.org/)
[![OpenTelemetry](https://img.shields.io/badge/Observability-OpenTelemetry-purple)](https://opentelemetry.io/)
[![Docker](https://img.shields.io/badge/Docker-Containerization-2496ED)](https://www.docker.com/)
[![Swagger-UI](https://img.shields.io/badge/Swagger-UI-orange)](https://swagger.io/tools/swagger-ui/)
[![Testcontainers](https://img.shields.io/badge/Testcontainers-Integration%20Tests-green)](https://www.testcontainers.org/)

## Overview
This project is a **reactive microservice** developed using **Java 21** and **Spring WebFlux**, designed to handle the lifecycle of wallets: onboarding, balance management, deposits, withdrawals, and transfers.

The project includes:
- Full API with secure authentication using **Keycloak**.
- Observability configured via **OpenTelemetry**, exported to **Jaeger** and **Prometheus**.
- Database managed with **PostgreSQL** and **R2DBC**.
- API documentation via **Swagger-UI**.
- Logging through **Aspect-Oriented Programming (AOP)**.
- Extensive **unit** and **integration testing** using **JUnit** and **Testcontainers**.

All tests and API interactions can be easily executed using the **Postman Collection** available at the root of the project.

---

## Tech Stack
- Java 21
- Spring WebFlux
- Spring Security (Resource Server - JWT)
- R2DBC + PostgreSQL
- Flyway (Database migrations)
- Keycloak (Authentication and Authorization)
- OpenTelemetry (Tracing and Metrics)
- Jaeger (Tracing UI)
- Prometheus (Metrics UI)
- Docker + Docker Compose
- Swagger OpenAPI 3
- AspectJ (AOP Logging)
- JUnit 5 + Mockito + Testcontainers

---

## Installation and Setup

### Prerequisites
- Docker
- Docker Compose

### How to Run Locally

1. **Clone the repository**
```bash
git clone https://github.com/your-username/wallet-service.git
```

2. **Navigate into the project**
```bash
cd wallet-service
```

3. **Start the environment**
```bash
docker-compose up --build
```

4. **Access services**
- Wallet API: http://localhost:8081
- Swagger UI: http://localhost:8081/swagger-ui.html
- Keycloak UI: http://localhost:8080 (admin/admin)
- Jaeger UI: http://localhost:16686
- Prometheus UI: http://localhost:9090

> Note: The application will automatically create the database schema and import the Keycloak realm on startup.

---

## Testing the API

- Import the **`Wallet Service Postman Collection.json`** file located in the root of the project into Postman.
- The collection is already configured to:
  - Retrieve an access token from Keycloak.
  - Set the token dynamically in subsequent requests.
  - Execute Wallet API operations such as onboarding, balance checking, deposit, withdrawal, and transfer.

---

## Design Choices

- **Reactive Stack**: Using WebFlux and R2DBC ensures the service is non-blocking, highly scalable, and prepared for handling high concurrency.
- **Clean Architecture**: Ports and Adapters (Hexagonal) pattern applied, enabling easy maintainability, testing, and scalability.
- **Security**: Protects all endpoints using OAuth2 JWT tokens via Keycloak integration.
- **Observability**: Configured OpenTelemetry agent in Docker to export traces and metrics without modifying the application code.
- **API Documentation**: Available through integrated Swagger-UI, describing all endpoints and payloads.
- **AOP Logging**: Key operations automatically logged using Aspect-Oriented Programming (AOP) to track method execution time, input, and output parameters without code duplication.
- **Testing Strategy**:
  - **Unit Tests** cover use cases and service layer logic.
  - **Integration Tests** use **Testcontainers** to spin up real PostgreSQL and Keycloak containers during the tests.
- **CI/CD Ready**: The system is containerized and ready to be plugged into any pipeline.

---

## Non-Functional Requirements

| Requirement              | Solution                               |
|---------------------------|----------------------------------------|
| Scalability               | Reactive programming with WebFlux      |
| Observability             | Traces via Jaeger, Metrics via Prometheus |
| Security                  | OAuth2 Resource Server (Keycloak)     |
| Database Migrations       | Flyway applied at container start     |
| API Documentation         | Swagger-UI available at /swagger-ui.html |
| Logging                   | Aspect-Oriented Programming (AOP) based logging |
| Testing                   | Unit and Integration tests with Testcontainers |

---

## Trade-offs and Limitations

- **Embedded Keycloak**: For simplicity and quick setup, Keycloak runs as a Docker container. In production, an external managed Keycloak would be preferable.
- **Simplified OpenTelemetry Setup**: The Java Agent was configured directly via Docker, prioritizing speed over fine-grained SDK instrumentation.
- **Jaeger OTLP Direct Export**: Chose OTLP instead of native Jaeger exporters to align with OpenTelemetry's recommended practices and future-proof the architecture.
- **Basic Retry Policies**: Error handling and retry policies are minimal for the sake of focusing on core functionality during the challenge timeframe.

---

## Folder Structure

```plaintext
wallet-service/
├── src/main/java
│   ├── com.br.recargapay.walletservice
│   │   ├── adapter
│   │   ├── application
│   │   ├── config
│   │   ├── domain
│   │   └── infra
│   └── resources
│       ├── application.yml
│       ├── db/migration
├── docker-compose.yml
├── otel-collector-config.yaml
├── prometheus/prometheus.yml
├── keycloak/wallet-realm.json
├── wallet-service.postman_collection.json
├── Dockerfile
├── README.md
```

---

## Contact

If you have any questions, feel free to reach out!

> Developed with passion and attention to clean architecture, observability, software craftsmanship, and professional testing strategies.
