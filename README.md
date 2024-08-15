## UPlan Generator
### General
UPlan Generator is a UMD undergraduate courses database application designed to help students find an optimal path towards graduation from
day of enrollment.

An undergraduate plan generator in development, UPlan Generator primarily works as a persistent UMD course database, where queries for courses can be run where UMD's proprietary services fall short.

For example, finding certain Gen-Ed courses without any restrictions where anyone can enroll, regardless of obtained years and credits, is a use case available here exclusively (that is if you wish to make such a search easily in one go). The results are organized and easily downloadable in a table.

![image](https://github.com/user-attachments/assets/9b62c3ce-e564-4d97-a356-72c20dc1dd96)

The service can also predict the semesters each course is likely to be taught in the next years given previous years of data. No more anxious waiting to see if ENGL142 is going to be available next spring!

### Development and Setup
The project is built on top of Java Spring Boot, with Maven as the intended build tool.
You may use an in-memory H2 relational database or localhost mysql database server for development and testing. A standalone MySQL server for prod ensures proper production setup:
- Copy `src/main/resources/application.properties` to `src/main/resources/application-prod.properties`.
- Replace the placeholders in `application-prod.properties` with actual database credentials and other environment-specific configurations.
- Feel free to do the same for development environments, but copy `src/main/resources/application.properties` to `src/main/resources/application-dev.properties` instead.

Be sure to run the corresponding spring boot profile (simply: dev for development, prod for production). If you're using Spring Boot's Maven, you can simply use `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` to run the dev profile, for example.

### Future
The project as it stands today is only one-half of a major undertaking to build an undergraduate schedule generator for UMD students. When complete, students will be able to:
- Access the application running as a standalone web app hosted on a web server, possibly in the cloud.
- Use the database to generate 4-year (more or less) undergraduate plans to see which courses can be taken in which semesters. Gone are the Excel plan sheets!
- Algorithmically determine the best courses to maximize the chances of optimal GPA and credit earnings using the data already available in the database.
- Perform the above in a user-friendly web interface.
