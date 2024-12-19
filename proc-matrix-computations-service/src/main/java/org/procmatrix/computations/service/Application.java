package org.procmatrix.computations.service;

import org.procmatrix.storage.client.StorageService;
import org.procmatrix.storage.client.StorageServiceClientFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {
	private enum Mode { trivial, orchestrated }

	public static void main(String[] args) {
		final Mode mode = Arrays.asList(args).contains(Mode.orchestrated.name())
				? Mode.orchestrated
				: Mode.trivial;

		final Map<String, Object> properties = new HashMap<>();
		properties.put("spring.application.name", "storage-service");
		if (mode == Mode.trivial) {
			properties.put("server.port", 8082);
			properties.put("eureka.client.register-with-eureka", false);
			properties.put("eureka.client.fetch-registry", false);
		} else { // orchestrated
			properties.put("server.port", Integer.toString((int) (Math.random() * 1000) + 9000));
		}

		final SpringApplication app = new SpringApplication(Application.class);
		app.setDefaultProperties(properties);
		app.run(args);
	}

	@Bean
	public StorageService storageService() { // todo ak1 here will be kind of load balancing in orchestrated solution
		return StorageServiceClientFactory.createService("http://localhost:8081");
	}
}
