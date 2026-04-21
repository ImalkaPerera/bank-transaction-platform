# 🏦 Bank Transaction Platform

A microservices-based Bank Transaction System with centralized logging using the ELK Stack (Elasticsearch, Logstash, Kibana, Filebeat).

---

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Services](#services)
- [ELK Stack](#elk-stack)
- [How Logging Works](#how-logging-works)
- [API Endpoints](#api-endpoints)
- [Verification](#verification)
- [Roadmap](#roadmap)

---

## 📖 Overview

This project is a Bank Transaction Platform built with a microservices architecture. It demonstrates:

- Building multiple Spring Boot microservices
- Centralized log management using the ELK Stack
- Containerized infrastructure using Docker and Docker Compose
- Real-time log monitoring and visualization in Kibana
- Distributed tracing across services using MDC (Mapped Diagnostic Context)

---

## 🏗️ Architecture

```
User Request
     ↓
Gateway Service (port 8080)
     ↓              ↓
Account Service   Transaction Service
     ↓                    ↓
  (logs)               (logs)
     ↓                    ↓
        /var/log/app/*.log
               ↓
           Filebeat
         (collects logs)
               ↓
           Logstash
         (processes logs)
               ↓
        Elasticsearch
         (stores logs)
               ↓
            Kibana
      (visualize & search)
```

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| API Gateway | Spring Cloud Gateway |
| Logging | ELK Stack (Elasticsearch, Logstash, Kibana, Filebeat) |
| Containerization | Docker, Docker Compose |
| Build Tool | Maven (Multi-module) |
| Tracing | MDC (Mapped Diagnostic Context) |

---

## 📁 Project Structure

```
bank-transaction-platform/
│
├── services/
│   ├── account-service/          # Manages bank accounts
│   ├── transaction-service/      # Handles transactions
│   └── gateway-service/          # API Gateway
│
├── logging-common/               # Shared logging library
│   └── src/main/java/
│       ├── config/
│       │   └── LoggingFilter.java
│       ├── exception/
│       │   └── GlobalExceptionHandler.java
│       └── util/
│           └── MDCUtil.java
│
├── elk/
│   ├── docker-compose.yml        # ELK Stack containers
│   ├── .env                      # Environment variables
│   ├── elasticsearch/
│   │   └── elasticsearch.yml     # Elasticsearch config
│   ├── logstash/
│   │   └── pipeline/
│   │       └── logstash.conf     # Log processing pipeline
│   ├── filebeat/
│   │   └── filebeat.yml          # Log collection config
│   └── kibana/
│       └── kibana.yml            # Kibana config
│
├── scripts/                      # Utility scripts
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed:

| Tool | Version | Check Command |
|------|---------|---------------|
| Java | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| Docker | 24+ | `docker -version` |
| Git | 2+ | `git --version` |

> ⚠️ **Windows Users:** Docker Desktop requires at least **4GB of memory** allocated. Set this in Docker Desktop → Settings → Resources.

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/bank-transaction-platform.git
cd bank-transaction-platform
```

### 2. Start the ELK Stack

```bash
cd elk
docker-compose up -d
```

### 3. Verify Containers are Running

```bash
docker ps
```

Expected output:
```
CONTAINER       STATUS
elasticsearch   Up (healthy)
kibana          Up (healthy)
logstash        Up
filebeat        Up
```

### 4. Set Kibana System Password

```bash
curl -X POST -u elastic:BankAdmin123 \
  http://localhost:9200/_security/user/kibana_system/_password \
  -H "Content-Type: application/json" \
  -d '{"password": "KibanaAdmin123"}'
```

### 5. Open Kibana

```
URL:      http://localhost:5601
Username: elastic
Password: BankAdmin123
```

---

## 🔧 Services

### Account Service (port 8081)
Manages bank accounts — create accounts and check balances.

```
POST /accounts              → Create new account
GET  /accounts/{id}         → Get account details
GET  /accounts/{id}/balance → Check balance
```

### Transaction Service (port 8082)
Handles money transfers and transaction history.

```
POST /transactions           → Send money
GET  /transactions/{id}      → Get transaction details
GET  /transactions/history   → Get all transactions
```

### Gateway Service (port 8080)
Single entry point that routes all requests to the correct service.

```
All requests go through → localhost:8080
```

---

## 📊 ELK Stack

### What Each Tool Does

| Tool | Role | Port |
|------|------|------|
| Elasticsearch | Stores and indexes all logs | 9200 |
| Logstash | Processes and transforms logs | 5044 |
| Kibana | Visualizes logs in dashboard | 5601 |
| Filebeat | Collects logs from log files | - |

### Log Pipeline

```
Spring Boot Service
       ↓
writes to /var/log/app/*.log
       ↓
Filebeat watches and ships logs
       ↓
Logstash receives on port 5044
       ↓
Logstash parses, transforms, enriches
       ↓
Elasticsearch stores in daily index
bank-logs-YYYY.MM.dd
       ↓
Kibana → search and visualize
```

### Elasticsearch Credentials

```
Username: elastic
Password: BankAdmin123
URL:      http://localhost:9200
```

---

## 📝 How Logging Works

Every log entry automatically includes these fields:

```json
{
  "@timestamp"     : "2026-04-21T10:30:00Z",
  "service"        : "account-service",
  "level"          : "INFO",
  "message"        : "Account created successfully",
  "traceId"        : "abc-123-xyz",
  "method"         : "POST",
  "path"           : "/accounts",
  "status"         : 200,
  "durationMs"     : 45,
  "app"            : "bank-transaction-platform"
}
```

### Log Indexes in Elasticsearch

| Index Pattern | Contains |
|--------------|----------|
| `bank-logs-*` | All INFO and WARN logs |
| `bank-error-logs-*` | ERROR logs only |

### Searching Logs in Kibana

Examples of useful searches:

```
# Find all errors from account-service
service: "account-service" AND level: "ERROR"

# Find slow requests (over 1 second)
durationMs > 1000

# Find logs by trace ID
traceId: "abc-123-xyz"

# Find failed requests
status: 500
```

---

## ✅ Verification

### Check Elasticsearch Health

```bash
curl -u elastic:BankAdmin123 http://localhost:9200/_cluster/health
```

Expected: `"status": "green"`

### Send a Test Log

```bash
docker exec -it filebeat bash
mkdir -p /var/log/app
echo '{"@timestamp":"2026-04-21T10:30:00Z","level":"INFO","service":"account-service","message":"Test log","status":200,"durationMs":45}' > /var/log/app/test.log
exit
```

### Verify Log in Elasticsearch

```bash
curl -u elastic:BankAdmin123 http://localhost:9200/bank-logs-*/_search?pretty
```

---

## 🗺️ Roadmap

### Stage 1 — Learning (Current)
- [x] ELK Stack setup with Docker
- [ ] account-service (Spring Boot)
- [ ] transaction-service (Spring Boot)
- [ ] gateway-service (Spring Cloud Gateway)
- [ ] logging-common shared library
- [ ] Connect all services to ELK
- [ ] Kibana dashboards

### Stage 2 — Portfolio Ready (Coming Soon)
- [ ] PostgreSQL database
- [ ] JWT Authentication
- [ ] Apache Kafka integration
- [ ] React frontend
- [ ] GitHub Actions CI/CD
- [ ] Deploy to AWS

---

## ⚠️ Common Issues

| Problem | Fix |
|---------|-----|
| Elasticsearch won't start | Increase Docker memory to 4GB in Docker Desktop settings |
| Kibana can't connect | Wait 2 minutes — Elasticsearch takes time to fully start |
| Port already in use | Run `netstat -ano \| findstr :9200` to find the conflicting process |
| Filebeat permission error | Run Docker Desktop as Administrator |
| `curl` not found | Use Git Bash instead of Windows Command Prompt |

---

## 📚 What I Learned

- How ELK Stack works and why centralized logging matters
- Docker and Docker Compose for containerized infrastructure
- How Filebeat collects, Logstash processes, and Elasticsearch stores logs
- Microservices architecture with Spring Boot
- MDC for distributed tracing across services
- Kibana for log visualization and search

---

## 👨💻 Author

Built as a learning project to understand microservices observability and centralized logging.
