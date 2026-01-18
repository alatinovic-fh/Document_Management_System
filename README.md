# Document_Management_System (Latinovic, Korcian)

## Setup
Create a .env file in the root directory \
**.env**
```bash
GEMINI_API_KEY=API-KEY
```

## Startup
```bash
docker compose build
docker compose up
```

## Integration Tests

The project includes integration tests to verify the document upload functionality.

### Running the tests
```bash
# Windows
.\mvnw.cmd -Dtest=DocControllerIT test

# Linux / macOS
./mvnw -Dtest=DocControllerIT test
```

## Branching
* new feature: feature/{newFeatureName}
* fixing errors/bugs: fix/{whatIsFixed}
* everythingelse: chore/{taskPerformed}

## Use Case

* Bilder in Dokumenten erkennen und extrahieren
* werden in der Detailansicht angezeigt und können heruntergeladen werden

