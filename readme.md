<html>

<body align="justify" > 
<em>Warning: this is a work in progress!</em>

# Simplebank 
This project is a backend application that provides an API to store basic bank accounts and users that can access them via authentication and authorization. 

## Technologies used:
+ Java 11
+ Maven
+ Spring Boot
+ Junit and Mockito
+ Lombok

### Instructions for local testing and development:
Install Java 11( JDK+JRE) and maven in your machine, run the following commands to run tests and then start the Application using the test profile, which uses an in-memory database (H2) 

```bash
git clone https://github.com/carol-ao/simplebank.git
cd simplebank
mvn clean package
java -jar target/simplebank-0.0.1-SNAPSHOT.JAR
```

If you'd like to use a database like Postgres, you can edit the application.properties file to set ``` spring.profiles.active=production``` .
You'll have to run the script src/main/resources/data.sql  in your Postgres database to have valid roles and one user with admin role to access endpoints.
Finally, set the environment variables bellow when you run the App or the tests:

+ POSTGRES_PASSWORD=<YOUR_POSTGRES_PASSWORD> 
+ POSTGRES_USERNAME=<YOUR_POSTGRES_USERNAME>
+ POSTGRES_URL=<YOUR_DATABASE_URL>
+ SCHEMA=<YOUR_POSTGRES_SCHEMA>

If you'd like to change the environment variables for the token generation (jwt.secret and jwt.duration), alter them in the application.properties file.


Things that still have to be done:
- finish unit tests
- add swagger to generate documentation and add link here
- change bank balance to BigDecimal 
- implement bank operations history consultation 

For questions and suggestions, please contact me at carol.amoroli@gmail.com :)
</body>
</html>



