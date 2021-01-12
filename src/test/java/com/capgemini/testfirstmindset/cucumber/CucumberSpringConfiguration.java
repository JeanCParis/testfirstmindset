package com.capgemini.testfirstmindset.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.jdbc-url=jdbc:tc:mysql:5.7.22://localhost/bank?TC_INITSCRIPT=file:src/docker/schema.sql",
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",

})
@ActiveProfiles("test")
public class CucumberSpringConfiguration {}
