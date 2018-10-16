package io.mosip.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import io.mosip.registration.service.impl.DocumentUploaderServiceImpl;

@SpringBootApplication
@ComponentScan(basePackages="io.mosip.*")
@PropertySource({"classpath:registration-application.properties"})
@PropertySource({"classpath:application.properties"})
@PropertySource({"classpath:core-application.properties"})
public class RegistrationApplication {
	public static void main(String[] args) {
		SpringApplication.run(RegistrationApplication.class, args);
	}
}
