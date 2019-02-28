package eu.nimble.indexing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
/**
 * Main spring-boot app
 * @author dglachs
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class IndexingApp {

	public static void main(String[] args) {
		SpringApplication.run(IndexingApp.class, args);
	}

}

