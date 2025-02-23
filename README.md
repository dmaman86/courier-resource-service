# Courier Office Service

The **Office Service** is a microservice responsible for managing offices, branches, and contacts within the courier application. It handles business logic related to office operations,
including creating, updating, disabling, and retrieving data about offices, branches, and contacts.

---

## Features

- Manage offices and threir branches.
- Assign contacts to specific branches.
- Business logic validation for creating, updating, and disabling entities.
- Role-based authorization (`ROLE_ADMIN`).
- Kafka integration for error logging.
- JWT authentication with RSA token validation.
- MySQL database integration.

---

## Technologies

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Spring Cloud (Kafka)
- MySQL
- Docker
- MapStruct (for DTO mapping)
- Lombok

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/dmaman86/courier-office-service.git
cd courier-office-service
```

### 2. Configure Environment Variables

Create an `application.yml` file and ensure the following configurations are configured:

```yml
spring:
  application:
    name: office-service

  datasource:
    url: jdbc:mysql://localhost:3306/office_db?useSSL=false&createDatabaseIfNotExist=true&serverTimezone=UTC
    username: root
    password: root-workbench
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: office-service-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*" # Allow all packages to be deserialized
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.StringSerializer

  cloud:
    openfeign:
      client:
        config:
          default:
            connectTimeout: 5000
            readTimeout: 5000
            loggerLevel: full
      circuitbreaker:
        enabled: true

server:
  port: 8083

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true

  instance:
    hostname: office-service
```

---

### Prerequisites

Ensure you have the following installed:

- Docker (for running Kafka)
- Java 17
- Maven
- MySQL running locally

---

### **Running Kafka with Docker**

1. **Start Kafka and Zookeeper:**

```bash
docker run -d --name zookeeper -p 2181:2181 zookeeper

docker run -d --name kafka -p 9092:9092 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 --network="host" wurstmeister/kafka
```

2. **Verify Kafka is running:**

```bash
docker ps
```

---

### Running Locally

Ensure MySQL is running locally before starting the service.

1. Run the application:

```bash
mvn spring-boot:run
```

---

## API Endpoints

### Office Endpoints

| Method | Endpoint                     | Description                          | Authorization |
| ------ | ---------------------------- | ------------------------------------ | ------------- |
| GET    | `/api/courier/office`        | Get all offices (with pagination)    | Public        |
| GET    | `/api/courier/office/all`    | Get all offices (without pagination) | Public        |
| GET    | `/api/courier/office/{id}`   | Get an office by ID                  | Public        |
| POST   | `/api/courier/office`        | Create a new office                  | `ROLE_ADMIN`  |
| PUT    | `/api/courier/office/{id}`   | Update an office                     | `ROLE_ADMIN`  |
| DELETE | `/api/courier/office/{id}`   | Disable an office                    | `ROLE_ADMIN`  |
| GET    | `/api/courier/office/search` | Search offices by name               | Public        |

### Branch Endpoints

| Method | Endpoint                     | Description                        | Authorization |
| ------ | ---------------------------- | ---------------------------------- | ------------- |
| GET    | `/api/courier/branch`        | Get all branches (with pagination) | Public        |
| GET    | `/api/courier/branch/all`    | Get all branches for an office     | Public        |
| GET    | `/api/courier/branch/{id}`   | Get a branch by ID                 | Public        |
| POST   | `/api/courier/branch`        | Create a new branch                | `ROLE_ADMIN`  |
| PUT    | `/api/courier/branch/{id}`   | Update a branch                    | `ROLE_ADMIN`  |
| DELETE | `/api/courier/branch/{id}`   | Disable a branch                   | `ROLE_ADMIN`  |
| GET    | `/api/courier/branch/search` | Search branches by city or address | Public        |

### Contact Endpoints

| Method | Endpoint                      | Description                            | Authorization |
| ------ | ----------------------------- | -------------------------------------- | ------------- |
| GET    | `/api/courier/contact`        | Get all contacts (with pagination)     | Public        |
| GET    | `/api/courier/contact/{id}`   | Get a contact by ID                    | Public        |
| POST   | `/api/courier/contact`        | Create a new contact                   | `ROLE_ADMIN`  |
| PUT    | `/api/courier/contact/{id}`   | Update a contact                       | `ROLE_ADMIN`  |
| DELETE | `/api/courier/contact/{id}`   | Disable a contact                      | `ROLE_ADMIN`  |
| GET    | `/api/courier/contact/search` | Search contacts by name, phone, office | Public        |

---

## Security & Authentication

- JWT-based authentication with RSA key validation.
- Tokens are passed via cookies.
- Role-based access control using `@PreAuthorize`.

---

## Error Handling

- Global exception handling is implemented using `@RestControllerAdvice`.
- Errors are logged to the **error-service** through Kafka.
- Handles common exceptions like:
  - `EntityNotFoundException`
  - `EntityExistsException`
  - `BusinessRuleViolationException`
  - JWT and token validation errors

---

## License

This project is licensed under the [LICENSE.md](LICENSE.md).

