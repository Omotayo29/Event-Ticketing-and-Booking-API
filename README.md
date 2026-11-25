# Event Ticketing and Booking API

A Spring Boot REST API for creating events, publishing ticket tiers, and managing time-limited ticket reservations. It uses PostgreSQL for persistence and JWT bearer tokens to authenticate API requests.

## Features

- Create, browse, and cancel events with ticket tiers.
- Register attendees and log in to receive a signed JWT.
- Reserve tickets, view reservations, confirm pending reservations, or cancel them.
- Automatically expire pending reservations and return their ticket inventory.
- Validate request data and return structured API errors.
- Restrict reservation access and actions to the authenticated reservation owner.

## Technology

- Java 26
- Spring Boot, Spring Web MVC, Spring Data JPA, Spring Security, and Bean Validation
- PostgreSQL
- JSON Web Tokens (JJWT)
- Maven wrapper

## API overview

Registration and login are public. All other endpoints require `Authorization: Bearer <token>`.

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/register` | Register an attendee |
| `POST` | `/login` | Authenticate and receive a JWT |
| `POST` | `/api/v1/events` | Create an event with ticket tiers |
| `GET` | `/api/v1/events` | Browse events (supports Spring pageable query parameters) |
| `GET` | `/api/v1/events/{eventId}` | Get event details |
| `DELETE` | `/api/v1/events/{eventId}` | Cancel an event |
| `POST` | `/api/v1/events/{eventId}/reservations` | Reserve tickets for the authenticated attendee |
| `GET` | `/api/v1/reservations/{reservationId}` | Get an owned reservation |
| `PATCH` | `/api/v1/reservations/{reservationId}/confirm` | Confirm an owned pending reservation |
| `DELETE` | `/api/v1/reservations/{reservationId}` | Cancel an owned pending reservation |

