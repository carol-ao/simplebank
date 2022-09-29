<html>

<body align="justify" > 
<em>Warning: this is a work in progress!</em>

# Simplebank 
This project is a backend application that provides an API to store basic bank accounts and users that can access them via authentication and authorization. 

##Technologies used:
+ Java 11
+ Maven
+ Spring Boot
+ Junit and Mockito
+ Lombok

###Instructions for local testing and development:
Donwload and configure Java 11 JDK and maven to work with your favorite IDE. 
Clone this repository, open the project in IDE and download pom.xml dependencies. To run and debug the app and its unit tests, make sure to add the required environment variables that are in the application.properties file regarding database configuration for test and production environments.

### Environment Variables:
Set the following variables to configure token generation:

+ JWT_SECRET=JLQ3yFGL6X2R0u5meISHRirMiDIl7ZYlkKxHHyPm6XhqQNTLhC (example)
+ JWT_DURATION=86400000  (example)

To use an in-memory bank for testing, set the following variable:
+ PROFILE=test

If you want to use a Postgres Database (suggestion for production environment):
+ PROFILE=production

then set the following variables as well:
+ POSTGRES_PASSWORD=<YOUR_POSTGRES_PASSWORD> 
+ POSTGRES_USERNAME=<YOUR_POSTGRES_USERNAME>
+ POSTGRES_URL=<YOUR_DATABASE_URL>
+ SCHEMA=<YOUR_POSTGRES_SCHEMA>


Things that still have to be done:
- finish unit tests
- add swagger to generate documentation and add link here
- change bank balance to BigDecimal 
- implement bank operations history consultation 

For questions and suggestions, please contact me at carol.amoroli@gmail.com :)
</body>
</html>



