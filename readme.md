<html>

<p align:"center> 
<em>Warning: this is a work in progress!</em>

Simplebank is a backend application that provides an API to store basic bank accounts and users that can access them via authentication and authorization. 

Technologies used:
Java 11,
Maven,
Spring Boot,
Junit and Mockito,
Postgres.

Instructions for local testing and development:

Donwload and configure Java 11 JDK and maven 
to work with your favorite IDE. 

Clone this repository, open the project in IDE and download pom.xml dependencies. 

To run and debug the app and its unit tests, make sure to add the required environment variables that are in the application.properties file regarding database(Postgres was my choice for this project and that's what you'll find it configured to use), and other crucial options.

Todos: 
- add env variables here with sample values
- change bank balance to BigDecimal
- add swagger to generate documentation and add link here
- finish unit tests
- use factory design pattern for entities and dtos in tests

For questions and suggestions, please contact me at carol.amoroli@gmail.com :)
</p>
</html>



