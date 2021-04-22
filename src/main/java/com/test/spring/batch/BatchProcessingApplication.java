package com.test.spring.batch;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchProcessingApplication {

	public static void main(String[] args) {
		
		System.out.println("Arguments: ");
		
		for(String arg: args)
			System.out.println("arg: " + arg);
		
		System.out.println("Env: " + System.getenv());
		
		System.exit(SpringApplication.exit(SpringApplication.run(BatchProcessingApplication.class, args)));
	}

}