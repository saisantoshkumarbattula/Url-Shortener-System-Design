# URL Shortener System Design

A highly scalable and performant URL Shortener system implemented with Spring Boot, MongoDB, and Redis. It provides REST APIs to shorten URLs, redirect them to the original destination, track access counts, and perform basic CRUD operations on URLs.

## 🚀 Features

- **URL Shortening**: Generates a unique short code for any given URL.
- **Redirection**: Fast read operation to get the original URL using caching.
- **Analytics Syncing**: Uses a background scheduler (`AccessCountSyncScheduler.java`) to reliably sync access counts using MongoDB and Redis. 
- **Caching**: Leverages Redis caching for faster read accesses and redirection.
- **AOP Logging**: Implements Aspect-Oriented Programming (AOP) for consistent and robust logging of operations.
- **Global Exception Handling**: Returns standardized error responses for validation and not-found scenarios.

## 🛠️ Tech Stack

- **Java 26**
- **Spring Boot** (Web, Data MongoDB, Data Redis, AOP)
- **MongoDB**: Primary persistent data store.
- **Redis**: High-performance cache and temporary data store.
- **Maven**: Dependency and build management.
- **Lombok**: Boilerplate reduction.

## ⚙️ Prerequisites

- Java 26+
- Maven 3.8+
- MongoDB instance running locally or securely (e.g., MongoDB Atlas)
- Redis server running locally or via Docker

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/Url-Shortener-System-Design.git
cd Url-Shortener-System-Design
```

### 2. Configure Environment Variables

The application relies on several environment variables for database connections. You can export these variables in your terminal or configure them in your IDE.

```bash
export MONGO_URI="mongodb://localhost:27017/urlshortener"
export REDIS_HOST="localhost"
export REDIS_PORT="6379"
export REDIS_USERNAME=""
export REDIS_PASSWORD=""
```

*Note: Adjust the variables depending on your MongoDB/Redis credentials.*

### 3. Build and Run the Application

Compile the project and run tests:

```bash
./mvnw clean package
```

Run the application:

```bash
./mvnw spring-boot:run
```

The server should start on `http://localhost:8080`.

## 📖 API Endpoints

### 1. Create a Short URL
**POST** `/shorten`

**Request Body:**
```json
{
  "url": "https://www.example.com/very/long/url/path"
}
```

**Response (201 Created):**
```json
{
  "id": "mongo-document-id",
  "url": "https://www.example.com/very/long/url/path",
  "shortUrl": "aB3dE",
  "createdAt": "2026-04-18T10:00:00Z",
  "lastModifiedAt": "2026-04-18T10:00:00Z",
  "accessCount": 0
}
```

### 2. Redirect / Get Original URL
**GET** `/shorten/{shortCode}`

Returns the original URL as a plain string.

### 3. Get URL Statistics
**GET** `/shorten/{shortCode}/stats`

**Response (200 OK):**
```json
{
  "id": "mongo-document-id",
  "url": "https://www.example.com/very/long/url/path",
  "shortUrl": "aB3dE",
  "createdAt": "2026-04-18T10:00:00Z",
  "lastModifiedAt": "2026-04-18T10:00:00Z",
  "accessCount": 42
}
```

### 4. Update an Existing URL
**PUT** `/shorten/{shortCode}`

**Request Body:**
```json
{
  "url": "https://www.example.com/new/long/url"
}
```

### 5. Delete a Short URL
**DELETE** `/shorten/{shortCode}`

**Response:** `204 No Content`

## 🧪 Testing

To execute unit and integration tests (which utilize `@DataMongoTest` and `@DataRedisTest`), run:

```bash
./mvnw clean test
```

*Note: Ensure your MongoDB and Redis instances or Testcontainers are properly configured to allow tests to run without environmental conflicts.*
