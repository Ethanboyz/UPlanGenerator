## UPlan Generator
### General
UPlan Generator is a UMD undergraduate schedule generator designed to help students find an optimal path towards graduation from
day of enrollment.

### Development and Setup
The project runs Spring Boot's Maven and has a primarily Java codebase.
We use an in-memory H2 relational database for development and testing and a standalone MySQL server for prod. To ensure proper production setup:
- Copy `src/main/resources/application.properties` to `src/main/resources/application-prod.properties`.
- Replace the placeholders in `application-prod.properties` with actual database credentials and other environment-specific configurations.

Be sure to run the corresponding spring boot profile (simply: dev for development, prod for production). If you're using Spring Boot's Maven, you can simply use `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` to run the dev profile, for example.