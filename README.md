## Bajaj Finserv Hiring Challenge — Java (Spring Boot)

This is a Spring Boot application that automates the Bajaj Finserv Hiring Challenge workflow:

- Generates a webhook and access token from the challenge API
- Computes the required SQL query based on a registration number
- Submits the final SQL to the challenge test endpoint

There are no REST endpoints exposed by this app; it runs the entire flow automatically at startup via a `CommandLineRunner`.

### Prerequisites

- **Java 17** (matches `pom.xml` `java.version`)
- **Maven 3.9+** available on PATH
  - You may also use the bundled Maven under `apache-maven-3.9.11-bin/apache-maven-3.9.11/bin/mvn.cmd` on Windows.

### Quick start

1) Clean and build the project

```bash
mvn clean package
```

2) Run the application (pick one)

```bash
# Using Spring Boot plugin (recommended during development)
mvn spring-boot:run

# Or run the packaged JAR
java -jar target/hiring-challenge-0.0.1-SNAPSHOT.jar
```

On startup, you should see logs indicating:

- Webhook generation
- The computed SQL query
- Submission result/response

### How it works

- Entry point: `com.bajajfinserv.hiringchallenge.HiringChallengeApplication`
- Core logic: `com.bajajfinserv.hiringchallenge.service.ChallengeService` implements `CommandLineRunner`
  - `generateWebhook()` calls `https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA`
  - `solveSqlProblem(regNo)` derives the SQL to submit
  - `submitSolution(accessToken, sqlQuery)` posts to `https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA`
- HTTP client: `RestTemplate` bean configured in `com.bajajfinserv.hiringchallenge.config.AppConfig`
- DTOs: `WebhookRequest`, `WebhookResponse`, `SolutionRequest` under `com.bajajfinserv.hiringchallenge.dto`

### Update your candidate details

By default, candidate `name`, `regNo`, and `email` are hard-coded. Update them before running:

File: `src/main/java/com/bajajfinserv/hiringchallenge/service/ChallengeService.java`

```java
// Inside generateWebhook()
WebhookRequest request = new WebhookRequest(
    "Your Name",
    "YourRegNo",
    "your.email@example.com"
);
```

You may also change the `regNo` passed to `solveSqlProblem("...")` inside `run(...)` if needed.

### Configuration

- Default config is minimal; if you need to add properties, use `src/main/resources/application.properties`.
- Network/proxy settings can be supplied to the JVM/Maven as needed (see Troubleshooting).

### Build artifacts

- JAR: `target/hiring-challenge-0.0.1-SNAPSHOT.jar`
- A prebuilt JAR may also be present under `release/`.

### Troubleshooting

- SSL/Handshake errors when calling challenge APIs:
  - Ensure system time is correct
  - Try `-Djavax.net.debug=ssl,handshake` for diagnostics
  - Corporate proxy: configure JVM `-Dhttps.proxyHost`/`-Dhttps.proxyPort` or Maven `settings.xml`
- HTTP 401/403 submissions:
  - Verify you used the latest `accessToken` from the webhook response
  - Ensure candidate details are correctly set
- Maven not found on Windows:
  - Use the bundled Maven: `apache-maven-3.9.11-bin/apache-maven-3.9.11/bin/mvn.cmd`

### Tech stack

- Java 17, Spring Boot 3.2
- Spring Web + WebFlux (dependencies present; core flow uses `RestTemplate`)
- Jackson for JSON

### Project structure (key parts)

```
src/main/java/com/bajajfinserv/hiringchallenge/
  ├─ HiringChallengeApplication.java            # Spring Boot entry point
  ├─ config/AppConfig.java                      # RestTemplate bean
  ├─ service/ChallengeService.java              # Orchestrates the workflow
  └─ dto/                                       # Request/response DTOs
```

### License

Internal/Challenge use only unless stated otherwise.



