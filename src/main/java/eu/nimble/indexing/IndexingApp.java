package eu.nimble.indexing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Main spring-boot app
 * @author dglachs
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class , ManagementWebSecurityAutoConfiguration.class})
@EnableDiscoveryClient
@RestController
@EnableSwagger2
public class IndexingApp {

	public static void main(String[] args) {
		SpringApplication.run(IndexingApp.class, args);
	}

}

