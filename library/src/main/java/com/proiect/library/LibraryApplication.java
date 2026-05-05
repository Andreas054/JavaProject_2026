package com.proiect.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApplication {
//    http://localhost:8080/swagger-ui/index.html

	public static void main(String[] args) {
        try {
            SpringApplication.run(LibraryApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
