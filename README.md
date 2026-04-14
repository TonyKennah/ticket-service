# Ticket Service

A Java-based service designed to handle ticket seat reservations and payments for a ticketing system. This service enforces specific business rules and integrates with external systems to finalise purchases.

## Business Rules

The service enforces the following constraints:

- **Ticket Types**: 
  - Adult: £25
  - Child: £15
  - Infant: £0 (Infants sit on an Adult's lap and do not require a seat).
- **Purchase Limits**: A maximum of 25 tickets can be purchased in a single transaction.
- **Adult Requirement**: Child and Infant tickets cannot be purchased without at least one Adult ticket.
- **Infant Capacity**: Each Infant must be accompanied by an Adult (the number of Infants cannot exceed the number of Adults).
- **Validation**: Account IDs must be greater than zero, and every request must be for at least one ticket.

## Technical Stack

- **Java 21**
- **Maven** (Build Tool)
- **JUnit 5** (Testing Framework)
- **Mockito** (Mocking Framework)

## Project Structure

```text
src/main/java/uk/org/kennah/
├── dto/               # Data Transfer Objects (TicketTypeRequest)
├── exceptions/        # Custom exceptions (InvalidPurchaseException)
├── external/          # Interfaces for third-party services (Payment/Reservation)
└── services/          # Business logic implementation (TicketServiceImpl)
```

## Getting Started

### Prerequisites

- Java 21 JDK
- Apache Maven

### Build the Project

To compile the source code and package the application, run:

```bash
mvn clean compile
```

### Running Tests

The project includes a suite of unit tests verifying both happy paths and edge cases (boundary limits, missing adults, etc.). Run them using:

```bash
mvn test
```
