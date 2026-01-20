# Document_Management_System

**Authors:** Latinovic, Korcian

## Overview

The **Document Management System** is a multi-service, containerized application designed to digitize, process, and manage documents without physical storage requirements. It provides automated OCR text extraction, AI-powered document summarization, full-text search, and image extraction from PDF documents.

The system follows a **microservices architecture** consisting of four application services and five supporting infrastructure services.

---

## Purpose and Scope

The goal of this project is to provide an end-to-end digital document pipeline:

* Upload and persist documents
* Extract text and images automatically
* Generate AI-based summaries
* Enable full-text and fuzzy search
* Provide analytics and insights on stored documents

---

## Key Features

| Feature            | Description                                         | Implementation                                  |
| ------------------ | --------------------------------------------------- | ----------------------------------------------- |
| Document Upload    | Web-based upload with metadata capture              | `paperless-frontend` → `paperless-rest` → MinIO |
| OCR Processing     | Asynchronous text extraction from scanned documents | Tesseract OCR via `paperless-ocr`               |
| AI Summarization   | Automatic document summary generation               | Google Gemini API in `paperless-ocr`            |
| Image Extraction   | Extract and display images from PDF documents       | Apache PDFBox in `paperless-ocr`                |
| Full-Text Search   | Fuzzy matching across document content              | Elasticsearch indexing                          |
| Batch Processing   | File system-based document ingestion                | `paperless-batch` directory monitoring          |
| Persistent Storage | Object storage for documents and images             | MinIO (S3-compatible)                           |
| Analytics          | Search and document analytics visualization         | Kibana dashboards                               |

---

## Use Cases

* Detect and extract images from documents
* Display extracted images in the document detail view
* Allow users to download extracted images

---

## Architecture

### Application Services

* **paperless-frontend**
  Web-based user interface for document upload, search, and visualization.

* **paperless-rest**
  REST API handling uploads, metadata persistence, and orchestration.

* **paperless-ocr**
  Performs OCR, image extraction, AI summarization, and search indexing.

* **paperless-batch**
  Monitors directories and ingests documents automatically.

---

## Infrastructure Services

### PostgreSQL (db)

* Stores document metadata (filename, upload date, file type, size, AI summary)
* Database name: `metadb`

**Container Name:** `metadb`
**Image:** `postgres:latest`
**Port:** `5432`
**Volume:** `pgdata`

Defined in `compose.yaml` (lines 78–91)

---

### RabbitMQ

* Asynchronous communication between `paperless-rest` and `paperless-ocr`
* Durable queues:

    * `ocr.queue`
    * `ocr.result.queue`

**Container Name:** `rabbitmq`
**Image:** `rabbitmq:3-management`
**Ports:**

* `5672` (AMQP)
* `15672` (Management UI)

Defined in `compose.yaml` (lines 65–76)

---

### MinIO

* S3-compatible object storage
* Stores original documents and extracted images
* Uses the `documents` bucket

**Container Name:** `minio`
**Image:** `minio/minio`
**Ports:**

* `9000` (API)
* `9090` (Console UI)
  **Volume:** `minio-storage`

Defined in `compose.yaml` (lines 104–116)

---

### Elasticsearch

* Full-text search engine
* Supports fuzzy search on extracted OCR content
* Single-node setup with security disabled (development)

**Container Name:** `elasticsearch`
**Image:** `docker.elastic.co/elasticsearch/elasticsearch:8.16.0`
**Port:** `9200`
**Volume:** `elasticsearch-data`

Defined in `compose.yaml` (lines 119–132)

---

### Kibana

* Analytics and visualization platform for Elasticsearch data

**Container Name:** `kibana`
**Image:** `docker.elastic.co/kibana/kibana:8.16.0`
**Port:** `9092` (mapped from internal `5601`)

Defined in `compose.yaml` (lines 134–144)

---

## Setup

Create a `.env` file in the project root directory:

```bash
GEMINI_API_KEY=API-KEY
```

---

## Startup

Build and start all services using Docker Compose:

```bash
docker compose build
docker compose up
```

---

## Integration Tests

The project includes integration tests to verify the document upload functionality.

### Run Tests

```bash
# Windows
.\\mvnw.cmd -Dtest=DocControllerIT test

# Linux / macOS
./mvnw -Dtest=DocControllerIT test
```

---

## Branching Strategy

* **New features:** `feature/{newFeatureName}`
* **Bug fixes:** `fix/{whatIsFixed}`
* **Other tasks:** `chore/{taskPerformed}`

---


