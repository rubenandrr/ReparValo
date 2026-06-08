package com.reparvalo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point of the ReparValo application.
 * 
 * This class boots up the Spring Boot environment, automatically configures
 * the embedded H2 database, and starts the Tomcat servlet container
 * to serve the REST API endpoints and static frontend resources.
 */
@SpringBootApplication
public class ReparValoApplication {

    /**
     * Runs the Spring Boot application by loading the application context,
     * resolving environment properties, and initializing the web server.
     * 
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ReparValoApplication.class, args);
    }
}