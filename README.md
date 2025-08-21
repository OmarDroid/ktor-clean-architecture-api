# Ktor Clean Architecture API

[![Kotlin](https://img.shields.io/badge/kotlin-2.1.10-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Ktor](https://img.shields.io/badge/ktor-3.2.3-orange.svg)](https://ktor.io)
[![PostgreSQL](https://img.shields.io/badge/postgresql-15-blue.svg?logo=postgresql)](https://postgresql.org)
[![Docker](https://img.shields.io/badge/docker-enabled-blue.svg?logo=docker)](https://docker.com)
[![Documentation](https://img.shields.io/badge/Documentation-dokka-brightgreen.svg)](https://omardroid.github.io/ktor-clean-architecture-api/)
[![Architecture](https://img.shields.io/badge/architecture-clean-green.svg)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

A production-ready RESTful API built with **Ktor** framework, following **Clean Architecture**
principles and modern development practices.

## Documentation

**[Complete API Documentation](https://omardroid.github.io/ktor-clean-architecture-api/)**

Explore the comprehensive API documentation with detailed explanations of the clean architecture
implementation, including all classes, methods, and architectural decisions.

## Clean Architecture

This project implements **Clean Architecture** with clear separation of concerns across three
distinct layers:

### **Domain Layer** (Business Logic)

- **Entities**: Core business objects (`User`, `UserId`, `Email`)
- **Use Cases**: Business operations (Create, Read, Update, Delete users)
- **Repository Interfaces**: Data access contracts
- **Zero external dependencies** - pure business logic

### **Data Layer** (External Interfaces)

- **Repository Implementations**: Concrete data access using Exposed ORM
- **Database Configuration**: PostgreSQL with connection pooling
- **Health Services**: System monitoring and diagnostics
- **External service integrations**

### **Presentation Layer** (API Interface)

- **Controllers**: HTTP request/response handling
- **DTOs**: Data transfer objects for API contracts
- **Routes**: RESTful endpoint definitions
- **Exception Handling**: Global error management

### **Dependency Injection**

- **Koin**: Lightweight DI framework
- **Module-based**: Organized by architectural layers
- **Interface-driven**: Enabling easy testing and mocking

## Features

### **Core Functionality**

- **User Management**: Complete CRUD operations with validation
- **Health Monitoring**: System status and database connectivity checks
- **Pagination**: Efficient data retrieval with customizable page sizes
- **Input Validation**: Comprehensive request data validation
- **Error Handling**: Structured error responses with appropriate HTTP status codes

### **Production Ready**

- **Docker Support**: Multi-stage builds with optimized container images
- **Database Migration**: Automatic schema management
- **Environment Configuration**: Flexible config management (HOCON + .env)
- **Logging**: Structured logging with Logback
- **Health Checks**: Kubernetes/Docker-ready health endpoints

### **Development Experience**

- **Comprehensive Testing**: Unit, integration, and repository tests
- **API Documentation**: Auto-generated with Dokka
- **Hot Reload**: Development server with automatic restarts
- **Code Quality**: Kotlin coding conventions and best practices

## Tech Stack

| Component            | Technology                                                                                                 | Version   |
|----------------------|------------------------------------------------------------------------------------------------------------|-----------|
| **Framework**        | [Ktor](https://ktor.io/)                                                                                   | 3.2.3     |
| **Language**         | [Kotlin](https://kotlinlang.org/)                                                                          | 2.1.10    |
| **Database**         | [PostgreSQL](https://www.postgresql.org/)                                                                  | 15 Alpine |
| **ORM**              | [Exposed](https://github.com/JetBrains/Exposed)                                                            | Latest    |
| **DI**               | [Koin](https://insert-koin.io/)                                                                            | Latest    |
| **Serialization**    | [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)                                   | Latest    |
| **Configuration**    | [HOCON](https://github.com/lightbend/config) + [dotenv-kotlin](https://github.com/cdimascio/dotenv-kotlin) | Latest    |
| **Containerization** | [Docker](https://www.docker.com/) + Docker Compose                                                         | Latest    |
| **Testing**          | [JUnit 5](https://junit.org/junit5/) + [MockK](https://mockk.io/)                                          | Latest    |

## Quick Start

### Prerequisites

- **Java 17+**
- **Docker & Docker Compose**
- **Git**

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/omardroid/ktor-clean-architecture-api.git
cd ktor-clean-architecture-api

# Start the application with PostgreSQL
docker-compose up --build -d

# Verify it's running
curl http://localhost:8080/health
```

### Option 2: Local Development

```bash
# Clone and navigate
git clone https://github.com/omardroid/ktor-clean-architecture-api.git
cd ktor-clean-architecture-api

# Start PostgreSQL (Docker)
docker-compose up postgres -d

# Run the application
./gradlew run

# Or run with specific profile
ENVIRONMENT=development ./gradlew run
```

### Testing & Development

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport

# Build fat JAR
./gradlew buildFatJar

# Generate API documentation
./gradlew dokkaHtml
```

## Project Structure

```
src/main/kotlin/
├── com/omaroid/
│   ├── Application.kt              # Main application entry point
│   ├── config/                     # Configuration management
│   ├── domain/                     # Business logic layer
│   │   ├── entities/              # Core business entities
│   │   ├── usecases/              # Business operations
│   │   ├── repositories/          # Data access interfaces
│   │   └── errors/                # Domain exceptions
│   ├── data/                      # External interfaces layer
│   │   ├── database/              # Database configuration
│   │   ├── repository/            # Repository implementations
│   │   └── health/                # Health monitoring
│   ├── presentation/              # API layer
│   │   ├── controllers/           # HTTP controllers
│   │   ├── routes/                # Route definitions
│   │   ├── dto/                   # Data transfer objects
│   │   ├── mappers/               # Entity-DTO converters
│   │   └── plugins/               # Ktor plugins
│   └── di/                        # Dependency injection modules
```

## License

This project is **open source** and available under
the [MIT License](https://opensource.org/licenses/MIT). Feel free to use it in your projects,
contribute improvements, or adapt it to your needs. We believe in sharing knowledge and helping the
developer community grow!

## Get Started & Connect

### **Ready to dive in?**

Star ⭐ this repository if you find it helpful, fork it to make it your own.

### **Follow my journey**

If you enjoyed this project and want to see more clean architecture implementations, Kotlin tips,
and modern development practices:

**[Follow me on GitHub](https://github.com/OmarDroid)** for more awesome projects and updates!

---

*Happy coding!*
