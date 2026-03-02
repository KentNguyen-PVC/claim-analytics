# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.example.claim-analytics' is invalid and this project uses 'com.example.claim_analytics' instead.

# Getting Started
    * Tech Stack
    - Java 17
    - Spring Boot 3
    - Oracle 23
        + docker pull container-registry.oracle.com/database/free:latest
        + docker run -d --name oracle_db -p 1521:1521 -p 5500:5500 -e ORACLE_PWD=123456789 -v oracle_data:/opt/oracle/oradata container-registry.oracle.com/database/free:latest
        + Create schema:
            + docker exec -it oracle_db sqlplus system/123456789@FREEPDB1
            + CREATE USER CLAIM IDENTIFIED BY Claim123;
            + GRANT CONNECT, RESOURCE TO CLAIM;
            + GRANT UNLIMITED TABLESPACE TO CLAIM;
            + GRANT CREATE PROCEDURE TO CLAIM;
            + GRANT CREATE SEQUENCE TO CLAIM;
    - JPA / Hibernate
    - Flyway

    * Setup
    - mvn clean install
    - Run the 3 SQL statements in the file src/main/resources/db/dummyData.sql

    * Run
    mvn spring-boot:run

    * Run Tests
    mvn test

    * Design Decisions
    - Used BigDecimal for money
    - EnumType.STRING to avoid ordinal risk
    - Used layered architecture
    - Used @Transactional at service layer
    - Used fetch join to avoid N+1
    - Used stored procedure for SLA finalization


### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.1.0-SNAPSHOT/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.1.0-SNAPSHOT/maven-plugin/build-image.html)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

