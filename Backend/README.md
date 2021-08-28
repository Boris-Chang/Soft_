#Afterlife backend

## Dev environment setup

### Create Postgres Database
**From Docker**:

`docker run --name afterlife-postgres -e POSTGRES_USER=afterlife_user -e POSTGRES_DB=afterlife_db -e POSTGRES_PASSWORD=afterlife_password -d -p 5432:5432 postgres`

**Or install it and create user "afterlife_user" with password "afterlife_password" and create database "afterlife_db"**

### Run database migrations
`mvn clean compile flyway:migrate -Dflyway.configFiles=src/main/resources/flyway_db.properties`

### Generate JOOQ records from Database automatically
`mvn jooq-codegen:generate`

### Startup spring boot application
`mvn spring-boot:run`

After that REST API will be available. 

To access **Swagger documentation** open:
`http://localhost:8080/swagger-ui/index.html`
And type:
`/v3/api-docs`
in the input