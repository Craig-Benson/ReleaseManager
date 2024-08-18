# ReleaseManager

<h2>Built with</h2>
Kotlin: 1.9.24</br>
Maven: 3.9.2 </br>
SpringBoot: 3.3.2</br>

<h2>To start the application</h2>

- cd into the release_manager folder
- run mvn clean install </br>
- run ReleaseManagerApplication</br>

<h2>Useful links:</h2>
When accessing either of the following links you will likely be prompted for credentials:

![img_1.png](img_1.png)

The credentials are stored in the application.properties file to be used while running locally,
in production actual credentials should be injected on deployment

Credentials:

- Username: user
- Password: password

<h2>Swagger UI</h2>

- Swagger Ui: http://localhost:8080/swagger-ui/index.html
  <h2>H2 Database</h2>

- In memory H2 DB console: http://localhost:8080/h2-console

H2 DB Console credentials:</br>

- url: jdbc:h2:mem:testdb</br>
- username: sa</br>

![img.png](img.png)

<h2>Todo</h2>
- Redis cache to store expensive queries and system version number</br>
- Kafka to store inbound and deployed services messages to add an additional layer of resiliency</br>