# 🔗 LinkForge

**LinkForge** is a **high‑throughput, distributed URL shortener** designed to handle **millions to billions of redirects per day** with **low latency, strong consistency for writes, and eventual consistency for analytics**.

This project is built to demonstrate **real-world backend engineering concepts** used in large-scale systems such as Bitly, TinyURL, and internal redirect platforms at big tech companies.

---

## 🚀 Key Features

* ⚡ **Cache-first redirects** using Redis (sub-millisecond latency)
* 🔐 **Rate limiting** to protect system from abuse
* ♻️ **Idempotent URL creation** (safe retries, no duplicates)
* 🌸 **Bloom filter** to prevent cache & DB penetration attacks
* 📊 **Kafka-based async analytics** (no DB writes on redirect path)
* 🧩 **Stateless Spring Boot services** (horizontally scalable)
* 🗃️ **Transactional DB writes** only where required

---

## 🏗️ High-Level Architecture

```
Client
  |
  v
Load Balancer
  |
  v
LinkForge API (Spring Boot, Stateless)
  |
  +--> Redis (Cache, Rate Limit, Idempotency)
  |
  +--> Bloom Filter (Existence Check)
  |
  +--> Kafka (Redirect Events)
  |
  +--> Database (URL Storage, Analytics - Batched)
```

---

## 🔁 Request Flows

### 1️⃣ URL Creation (Write Path)

```
POST /api/v1/shorten
  |
  v
Rate Limit Check (Redis)
  |
  v
Idempotency Check (Redis)
  |
  v
Generate Short Code
  |
  v
DB Transaction (INSERT)
  |
  v
Redis SET + Bloom Filter ADD
  |
  v
Return Short URL
```

✔ Strong consistency
✔ Exactly-once semantics
✔ Safe retries

---

### 2️⃣ Redirect (Read Path – Hot Path)

```
GET /{shortCode}
  |
  v
Rate Limit Check
  |
  v
Bloom Filter Check
  |
  v
Redis Cache
  |
  +--> HIT  → Redirect (302) → Kafka Event
  |
  +--> MISS → DB Lookup → Redis SET → Redirect → Kafka Event
```

✔ No DB writes
✔ Ultra-low latency
✔ Handles massive QPS

---

## 🧠 Why This Design Works at Scale

| Problem            | Solution               |
| ------------------ | ---------------------- |
| DB overload        | Cache-first reads      |
| Cache penetration  | Bloom filter           |
| Duplicate writes   | Idempotency keys       |
| Abuse & bots       | Redis rate limiting    |
| Analytics overhead | Kafka async processing |
| Horizontal scaling | Stateless services     |

---

## 📦 Tech Stack

| Layer     | Technology         |
| --------- | ------------------ |
| Language  | Java 17            |
| Framework | Spring Boot        |
| Cache     | Redis              |
| Messaging | Kafka              |
| Database  | PostgreSQL / MySQL |
| ORM       | Spring Data JPA    |
| Build     | Maven              |

---

## 📁 Project Structure

```
com.example.linkforge
│
├── api              # REST controllers
├── service          # Core business logic
├── filter           # Rate limit, idempotency, bloom filters
├── cache            # Redis configuration
├── kafka            # Kafka producers / consumers
├── bloom            # Bloom filter config
├── repository       # JPA repositories
├── entity           # Database entities
├── dto              # Request / response models
├── config           # Application configuration
└── LinkForgeApplication.java
```

---

## 🔐 Rate Limiting Strategy

* Redis atomic counters
* Sliding window (per minute)
* Different limits per endpoint

```
rate:{ip}:{window} → request_count
```

---

## ♻️ Idempotency Strategy

Clients must send:

```
Idempotency-Key: <unique-key>
```

Redis stores:

```
idem:{key} → response
```

✔ Prevents duplicate URL creation
✔ Safe client retries

---

## 🌸 Bloom Filter Usage

* Prevents invalid short code lookups
* Eliminates DB hits for non-existent URLs
* Updated on every successful URL creation

False positives allowed ✔
False negatives not allowed ❌

---

## 📊 Kafka Analytics

* Every redirect publishes an event
* No synchronous DB updates
* Consumers batch-insert analytics

Example event:

```json
{
  "shortCode": "aZ3kP9",
  "timestamp": "2026-02-07T10:15:30Z",
  "ip": "192.168.1.1"
}
```

---

## 🧪 How to Run Locally

### Prerequisites

* Java 17+
* Redis
* Kafka
* PostgreSQL / MySQL

### Steps

```bash
mvn clean install
mvn spring-boot:run
```

---

## 📈 Future Enhancements

* 🔁 DB sharding & consistent hashing
* 🌍 Multi-region Redis replication
* 📦 Kafka exactly-once consumers
* 🧪 Load testing with k6 / Gatling
* 🐳 Docker Compose & Kubernetes

---

## 🎯 Learning Outcomes

By building **LinkForge**, you learn:

* Designing high-throughput systems
* Cache vs DB trade-offs
* Event-driven architecture
* Transaction boundaries
* Failure handling at scale
* Interview-level system design

---

## 📜 License

MIT License

---

**LinkForge** is not a demo — it’s a **production-grade system design project**.

If you’re preparing for **backend interviews, system design rounds, or senior engineering roles**, this project directly maps to real-world expectations.
