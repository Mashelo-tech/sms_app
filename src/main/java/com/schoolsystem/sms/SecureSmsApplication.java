package com.schoolsystem.sms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class SecureSmsApplication {

	@Bean
	public CommandLineRunner printLoadedFiles(ApplicationContext ctx){
		return args-> {
			System.out.println("TAKING ATTENDENCE:LOADED FILES");

			String[] allLoadedFiles =ctx.getBeanDefinitionNames();
			Arrays.sort(allLoadedFiles);

			for (String filename : allLoadedFiles){
				if(filename.toLowerCase().contains("securityconfig") || filename.toLowerCase().contains("login") || filename.toLowerCase().contains("controller")) {
					System.out.println ("FOUND AND LOADED" +filename);
				}
			}
			System.out.println("==============================================================");
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(SecureSmsApplication.class, args);
	}

}
