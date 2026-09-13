# Order & Inventory Management System

Backend REST API do zarządzania zamówieniami i magazynem, zbudowany jako projekt portfolio.

Projekt pokazuje pełny cykl budowy realnego backendu: modelowanie domeny, logikę biznesową
(w tym ochronę przed race condition przy rezerwacji stanów magazynowych), bezpieczeństwo
(JWT, role), testy (jednostkowe + integracyjne z Testcontainers), konteneryzację i CI/CD,
a docelowo wdrożenie na AWS.

## Stack technologiczny

- **Język / runtime:** Java 21
- **Framework:** Spring Boot 3, Spring Data JPA (Hibernate), Spring Security
- **Baza danych:** PostgreSQL, migracje Flyway
- **Autoryzacja:** JWT
- **Testy:** JUnit 5, Mockito, Testcontainers
- **Dokumentacja API:** OpenAPI / Swagger
- **Konteneryzacja:** Docker, Docker Compose
- **CI/CD:** GitHub Actions
- **Chmura:** AWS ECS (aplikacja), RDS (PostgreSQL), S3 (pliki)

## Status projektu

🚧 W trakcie budowy - projekt rozwijany etapami, historia commitów odzwierciedla postęp
prac (od szkieletu, przez logikę biznesową, po deployment).

## Główne funkcje (docelowo)

- Rejestracja i logowanie użytkowników, role `USER` / `ADMIN`
- Zarządzanie produktami i stanami magazynowymi
- Składanie zamówień wieloproduktowych z rezerwacją stanu
- Anulowanie zamówień ze zwrotem produktów do magazynu
- Statusy zamówień i historia ich zmian
- Ochrona przed nadsprzedażą przy równoczesnych zamówieniach (concurrency control)
- Walidacja, globalna obsługa błędów, paginacja i filtrowanie
- Dokumentacja API (Swagger/OpenAPI)

## Uruchomienie lokalne

_(sekcja zostanie uzupełniona po dodaniu Docker Compose)_

## Architektura

_(diagram ERD i krótki opis decyzji architektonicznych - dodane w trakcie Etapu 2)_

## Licencja

Projekt na licencji MIT - patrz [LICENSE](./LICENSE).