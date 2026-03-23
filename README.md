# forage-midas 

A backend microservice built as part of the **JPMorganChase Software Engineering Job Simulation** on [Forage](https://www.theforage.com/), completed March 19, 2026.

**Midas Core** is a financial transaction processing service that receives transactions via Kafka, validates and persists them to a SQL database, queries an external Incentive REST API, and exposes a balance query endpoint — all within a single Spring Boot application.

---

## System Architecture

```
[Kafka Topic: trader-updates]
          │
          ▼
  [Midas Core - Spring Boot]
    ├── KafkaConsumer         → Deserializes incoming Transaction messages
    ├── Validation Layer      → Checks sender/recipient validity & balance
    ├── H2 Database (JPA)     → Persists TransactionRecords & updates User balances
    ├── Incentive API Client  → POSTs transactions to external REST API (port 8080)
    │     └── Adds incentive amount to recipient balance
    └── REST Controller       → GET /balance?userId=... (port 33400)
```

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Core language |
| Spring Boot | 3.2.5 | Application framework & dependency injection |
| Apache Kafka | 3.1.4 | Message queue for incoming transactions |
| Spring Data JPA | 3.2.5 | ORM & database persistence |
| H2 Database | 2.2.224 | Embedded in-memory SQL database |
| RestTemplate | (Spring Web) | External REST API integration |
| Maven | — | Build & dependency management |
| JUnit / Embedded Kafka | 3.1.4 | Automated testing |
| Testcontainers (Kafka) | 1.19.1 | Integration test support |

---

## Task Breakdown

### Task 1 — Environment Setup & Dependencies
Forked and cloned the scaffold repo, configured Java 17, and added all required Maven dependencies to `pom.xml`:

```xml
spring-boot-starter-data-jpa  (3.2.5)
spring-boot-starter-web       (3.2.5)
spring-kafka                  (3.1.4)
h2                            (2.2.224)
spring-boot-starter-test      (3.2.5)
spring-kafka-test             (3.1.4)
kafka (Testcontainers)        (1.19.1)
```

Also configured `application.yml` with:
```yaml
general:
  kafka-topic: trader-updates
```

Built and ran the project with `mvn clean install` and `mvn spring-boot:run`.

---

### Task 2 — Kafka Consumer Integration
Implemented a Kafka listener class that:
- Reads the topic name from `application.yml` (`trader-updates`) via `@Value`
- Uses `@KafkaListener` to consume messages from the topic
- Deserializes incoming JSON messages into the existing `Transaction` class
- Uses the embedded Kafka test framework — no host/port configuration needed for tests

The first four transaction amounts received by the consumer were:
```
122.86, 42.87, 161.79, 22.22
```

---

### Task 3 — H2 Database Integration
Integrated Spring Data JPA with an H2 in-memory database to validate and persist transactions.

**Validation rules — a transaction is only recorded if:**
1. The `senderId` maps to a valid `User` in the database
2. The `recipientId` maps to a valid `User` in the database
3. The sender's balance is greater than or equal to the transaction `amount`

**On a valid transaction:**
- A new `TransactionRecord` entity (many-to-one with `User`) is saved to the database
- The sender's balance is decremented by the transaction amount
- The recipient's balance is incremented by the transaction amount

Invalid transactions are silently discarded with no database changes.

After processing all transactions, the `waldorf` user's balance (rounded down): **`8721`**

---

### Task 4 — External REST Incentive API Integration
Integrated an external Incentive API (`services/transaction-incentive-api.jar`) running on `localhost:8080`.

**Integration details:**
- After a transaction passes validation, Midas Core POSTs the `Transaction` object to `http://localhost:8080/incentive` using `RestTemplate`
- The API responds with an `Incentive` object containing a single `amount` field (always ≥ 0)
- The incentive amount is stored in a new `incentive` field on the `TransactionRecord`
- The incentive is added to the **recipient's** balance only — the sender is not debited for it

After processing all transactions, the `wilbur` user's balance (rounded down): **`5121`**

---

### Task 5 — REST Balance Endpoint
Exposed a REST API directly within Midas Core on **port 33400**.

**Endpoint:** `GET /balance?userId={id}`

**Behavior:**
- Looks up the `User` by `userId` in the H2 database
- Returns a JSON-serialized `Balance` object with the user's current balance
- Returns a balance of `0` if the user does not exist

**Sample responses from TaskFiveTests:**
```json
{"amount":688.5}
{"amount":702.72}
{"amount":928.63995}
{"amount":261.95996}
{"amount":283.88998}
{"amount":353.75995}
{"amount":178.47002}
{"amount":195.82999}
{"amount":274.08002}
{"amount":589.14996}
{"amount":800.0}
{"amount":900.0}
{"amount":1000.0}
```

---

## Project Structure

```
forage-midas/
├── .mvn/wrapper/                        # Maven wrapper config
├── services/
│   └── transaction-incentive-api.jar    # External Incentive API (run before tests)
├── src/
│   └── main/
│       ├── java/com/jpmc/midascore/
│       │   ├── component/               # Kafka consumer & transaction processor
│       │   ├── entity/                  # User, TransactionRecord JPA entities
│       │   ├── foundation/              # AppConfiguration, RestTemplate bean
│       │   └── repository/              # Spring Data JPA repositories
│       └── resources/
│           └── application.yml          # Kafka topic, server port, H2 config
│   └── test/                            # TaskOneTests through TaskFiveTests
├── .gitignore
├── mvnw / mvnw.cmd                      # Maven wrapper scripts
└── pom.xml                              # All dependencies
```

---

## Running the Project

### Prerequisites
- Java 17
- Maven (or use the included `./mvnw` wrapper)

### Steps

```bash
# 1. Clone the repo
git clone https://github.com/N00dleN00b/forage-midas.git
cd forage-midas

# 2. Start the Incentive API (required for Tasks 4 & 5)
java -jar services/transaction-incentive-api.jar

# 3. In a new terminal — build and run Midas Core
./mvnw clean install
./mvnw spring-boot:run
```

### Run All Tests
```bash
./mvnw test
```

### Run a Specific Task's Tests
```bash
./mvnw -Dtest=TaskOneTests test
./mvnw -Dtest=TaskTwoTests test
./mvnw -Dtest=TaskThreeTests test
./mvnw -Dtest=TaskFourTests test
./mvnw -Dtest=TaskFiveTests test
```

> Make sure `transaction-incentive-api.jar` is running before executing TaskFourTests or TaskFiveTests.

---

## Certificate

Completed the **JPMorganChase Software Engineering Job Simulation** on Forage — March 19, 2026.

**Skills demonstrated:** Java 17 · Spring Boot · Apache Kafka · Spring Data JPA · H2 SQL · RestTemplate · REST API Design · Maven · Embedded Kafka Testing

[📄 View Completion Certificate](./forage.pdf)
---
