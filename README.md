# Order & Inventory Management System

Backend REST API for handling orders and inventory. A portfolio project for a Junior Java Backend Developer position, built in stages.

## Stack

- Java 21, Spring Boot 4
- Spring Data JPA (Hibernate), Spring Security, JWT
- PostgreSQL, Flyway
- JUnit 5, Mockito, Testcontainers
- Swagger / OpenAPI
- Docker, Docker Compose
- GitHub Actions
- AWS: ECS, RDS, S3

## Status

Work in progress.

## Planned features

- registration and login, `USER` and `ADMIN` roles
- product and stock management
- orders with multiple products and stock reservation
- order cancellation with stock returned to the warehouse
- order statuses and status change history
- protection against selling the last item to several customers at once
- validation, global error handling, pagination and filtering
- API documentation in Swagger

## License

MIT, see the [LICENSE](./LICENSE) file.
